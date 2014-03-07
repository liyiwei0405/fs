package com.funshion.search.media.search;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.funshion.search.analyzers.CharacterFormat;

/**
 * only split word by \t
 * @author liying
 *
 */
public final class SplitTokenizer extends Tokenizer {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private String[] handler;
	private int pos = 0;
	public SplitTokenizer(Reader reader) {
		super(reader);
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		if(handler == null){
			init();
		}else{
			pos ++;
		}
		if(pos < handler.length){
			String rew = CharacterFormat.rewriteString(handler[pos]);
			termAtt.setEmpty().append(rew);
			return true;
		}else{
			return false;
		}
	}
	private void init() throws IOException{
		StringBuilder buffer = new StringBuilder();
		int ci;
		while (true) {
			ci = input.read();
			if (ci == -1) {
				break;
			}else if(ci == '\t'){
				buffer.append('\t');
			}else{
				char chnew = CharacterFormat.SBCCaseFilter((char)ci);
				buffer.append(chnew);
			}
//			System.out.print((char)ci);
		}
//		System.out.println("\n" + buffer);
		String tokens[] = buffer.toString().split("\t");
		this.handler = tokens;
		pos = 0;
	}

	@Override
	public void reset() throws IOException {
		handler = null;
		pos = 0;
	}


}
