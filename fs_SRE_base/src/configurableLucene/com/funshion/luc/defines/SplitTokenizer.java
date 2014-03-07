package com.funshion.luc.defines;

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
			}else{
				buffer.append(Character.toLowerCase((char)ci));
			}
		}
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
