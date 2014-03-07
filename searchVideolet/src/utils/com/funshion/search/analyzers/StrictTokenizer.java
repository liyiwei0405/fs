package com.funshion.search.analyzers;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * only split word by \t
 * @author liying
 *
 */
public final class StrictTokenizer extends Tokenizer {

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private String[] handler;
	private int pos = 0;
	public StrictTokenizer(Reader reader) {
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
			String rew = rewriteString(handler[pos]);
			termAtt.setEmpty().append(rew);
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
			}else if(ci == '\t'){
				buffer.append('\t');
			}else{
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
			}
			ci = input.read();
			ch = (char) ci;

		}
		this.handler = buffer.toString().split("\t");
		pos = 0;
	}

	@Override
	public void reset() throws IOException {
		handler = null;
		pos = 0;
	}

	public static String rewriteString(String str){
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < str.length(); x ++){
			char c = str.charAt(x);
			if(Character.isLetterOrDigit(c)){
				sb.append(Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

}
