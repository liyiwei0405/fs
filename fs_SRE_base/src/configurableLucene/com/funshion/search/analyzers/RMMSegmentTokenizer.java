package com.funshion.search.analyzers;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import com.funshion.search.segment.RMMSegment;
import com.funshion.search.segment.TokenHandler;

/**
 * check words and convert words into formatted pattern, such as full-width to half-width...
 * @author liying
 *
 */
public final class RMMSegmentTokenizer extends Tokenizer {

	private int tokenStart = 0, tokenEnd = 0;
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
	private TokenHandler handler;
	public RMMSegmentTokenizer(Reader reader) {
		super(reader);
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		if(handler == null){
			init();
		}
		if(handler.hasNext()){
			String var = handler.next();
			tokenStart = tokenEnd;
			tokenEnd = tokenEnd + var.length();
			termAtt.setEmpty().append(var);
			offsetAtt.setOffset(correctOffset(tokenStart), correctOffset(tokenEnd));
			typeAtt.setType(TypeAttribute.DEFAULT_TYPE);
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
			} else {
				char chnew = CharacterFormat.SBCCaseFilter((char) ci);
				buffer.append(chnew);
				tokenEnd++;
			}
		}
		this.handler = RMMSegment.instance.noCoverSegment(buffer.toString()).handler;
	}

	@Override
	public void reset() throws IOException {
		tokenStart = tokenEnd = 0;
		handler = null;
	}

}
