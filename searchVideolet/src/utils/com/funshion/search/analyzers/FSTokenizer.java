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
public final class FSTokenizer extends Tokenizer {

	private int tokenStart = 0, tokenEnd = 0;
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
	private TokenHandler handler;
	public FSTokenizer(Reader reader) {
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
		char ch, chnew;
		
		ci = input.read();
		ch = (char) ci;

		while (true) {
			if (ci == -1) {
				break;
			} else {
				if (ch >= 65281 && ch <= 65374) {   
					chnew = ((char)(ch - 65248)); 
				}else if (ch == 12288){   
					chnew = (' ');   
				}else if(ch == 65377){   
					chnew = ('。');   
				}else if(ch == 12539){   
					chnew = ('·');   
				}else if(ch ==8226){ 
					chnew = ('·');   
				}else if(ch == '　'){ 
					chnew = (' ');   
				}else if(ch == '—'){ 
					chnew = ('-');   
				}else if(ch == '【'){ 
					chnew = ('[');   
				}else if(ch == '】'){ 
					chnew = (']');   
				}else{   
					chnew = (ch);   
				}
				buffer.append(Character.toLowerCase(chnew));
				tokenEnd++;
				ci = input.read();
				ch = (char) ci;
			}
		}
		this.handler = RMMSegment.instance.segment(buffer.toString());
	}

	@Override
	public void reset() throws IOException {
		tokenStart = tokenEnd = 0;
		handler = null;
	}

}
