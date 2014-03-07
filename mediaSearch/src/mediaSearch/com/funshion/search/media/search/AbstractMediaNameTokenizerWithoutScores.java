package com.funshion.search.media.search;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

import com.funshion.search.analyzers.CharacterFormat;
import com.funshion.search.media.chgWatcher.FieldDefine;
import com.funshion.search.media.search.mediaTitleRewriter.F2JConvert;
import com.funshion.search.segment.RMMSegment;
import com.funshion.search.segment.RMMSegment.CoupleResult;

/**
 * only split word by \t
 * @author liying
 *
 */
public abstract class AbstractMediaNameTokenizerWithoutScores  extends Tokenizer{

	public abstract byte[] getWordWeightValue(int typeIndex, String word);
	public abstract byte[] fullNamePayload(int nameTypeIdx);
	public static class WordToken{
		final String word;
		final byte[] payload;
		WordToken(String word, byte[] payload){
			this.word = word;
			this.payload = payload;
		}
		@Override
		public String toString(){
			return word + "\t" + payload[0];
		}
	}
	protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	protected final PayloadAttribute payloadAtt = addAttribute(PayloadAttribute.class);
	private List<WordToken> handler;
	private int pos = 0;

	public AbstractMediaNameTokenizerWithoutScores(Reader reader) {
		super(reader);
	}
	public List<WordToken> toWordToken(String line, final int nameTypeIdx){
		List<WordToken> ret = new ArrayList<WordToken>();
		Set<String>iset = new HashSet<String>();
		String tokens[] = line.trim().split("\t");

		for(String token : tokens){
			
			token = F2JConvert.instance.convert(token);
			String fullWord = CharacterFormat.rewriteString(token);
			
			if(fullWord.length() == 0){
				continue;
			}
			WordToken t =  new WordToken(fullWord + FieldDefine.FULL_NAME_END, fullNamePayload(nameTypeIdx));
			ret.add(t);
			iset.clear();
			CoupleResult cr = RMMSegment.instance.noCoverSegment(token);

			while(cr.handler.hasNext()){
				String word = cr.handler.next();
				word = CharacterFormat.rewriteString(word);
				if(iset.contains(word)){
					continue;
				}
				iset.add(word);
				WordToken tk =  new WordToken(word, getWordWeightValue(nameTypeIdx, word));
				ret.add(tk);
			}
			for(String word : cr.set){
				word = CharacterFormat.rewriteString(word);
				if(iset.contains(word)){
					continue;
				}
				iset.add(word);
				WordToken tk =  new WordToken(word, getWordWeightValue(nameTypeIdx, word));
				ret.add(tk);
			}
		}
		return ret;
	}
	
	@Override
	public void reset() throws IOException {
		handler = null;
		pos = 0;
	}
	protected void init() throws IOException{
		StringBuilder buffer = new StringBuilder();

		while (true) {
			int ci = input.read();
			if (ci == -1) {
				break;
			}else if(ci == '\t'){
				buffer.append('\t');
			}else{
				char chnew = CharacterFormat.SBCCaseFilter((char)ci);
				buffer.append(chnew);
			}
		}
		this.handler = makeTokens(buffer.toString());
		pos = 0;
	}
	protected abstract  List<WordToken> makeTokens(String line);

	@Override
	public final boolean incrementToken() throws IOException {
		clearAttributes();
		if(handler == null){
			init();
		}else{
			pos ++;
		}
		if(pos < handler.size()){
			clearAttributes();
			WordToken wt = handler.get(pos);
			termAtt.setEmpty().append(wt.word);

			BytesRef br = new BytesRef(wt.payload);
			payloadAtt.setPayload(br);
			return true;
		}else{
			return false;
		}
	}
}
