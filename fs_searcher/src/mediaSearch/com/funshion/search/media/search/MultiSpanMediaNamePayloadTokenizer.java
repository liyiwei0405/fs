package com.funshion.search.media.search;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.funshion.search.analyzers.CharacterFormat;

/**
 * only split word by \t
 * @author liying
 *
 */
public final class MultiSpanMediaNamePayloadTokenizer extends AbstractSpanableMediaNameTokenizer {

	public MultiSpanMediaNamePayloadTokenizer(Reader reader) {
		super(reader);
	}

	public List<WordToken> makeTokens(String linesStr){
		String lines[] = linesStr.split("\n");
		List<WordToken> ret = new ArrayList<WordToken>();
		for(int nameTypeIdx = 0; nameTypeIdx < 4; nameTypeIdx ++){
			String line = lines[nameTypeIdx];
			try {
				ret.addAll(toSingleWordToken(line, nameTypeIdx));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	private ArrayList<String> seg(String token) throws IOException {
		StringBuilder buffer = new StringBuilder();
		int ci;
		ArrayList<String>lst = new ArrayList<String>();
		StringReader input = new StringReader(token);
		short type = 0;
		while (true) {
			ci = input.read();
			boolean end = ci == -1;
			boolean needAppend = false;
			char chnew = (char) ci;
			if(!end){
				if(ci == '\t'){
					end = true;
					needAppend = true;
					chnew = '$';
					type = 0;
				}else{
					chnew = CharacterFormat.SBCCaseFilter((char)ci);

					if(!Character.isLetterOrDigit(chnew)) {
						end = true;
					}else{
						needAppend = true;
						if(chnew < 128){
							if(type != 1){
								end = true;
							}
						}else{
							end = true;
						}
					}
				}
			}

			if(end && buffer.length() > 0){
				String ret = buffer.toString().trim();
				if(ret.length() > 0){
					lst.add(ret);
				}
				//reinit
				buffer.setLength(0);
				type = 0;
			}
			if(ci == -1){
				break;
			}
			if(needAppend){
				buffer.append(chnew);
				if('\t' == ci){
					type = 0;
				}else if(chnew < 128){
					type = 1;
				}else{
					type = 2;
				}
			}

		}
		return lst;
	}

	public List<WordToken> toSingleWordToken(String line, final int nameTypeIdx) throws IOException{
		List<WordToken> ret = new ArrayList<WordToken>();
		String tokens[] = line.trim().split("\t");

		for(String token : tokens){

			String fullWord = CharacterFormat.rewriteString(token);
			if(fullWord.length() == 0){
				continue;
			}
			ArrayList<String> lst = seg(fullWord);
			if(lst.size() > 0){
				for(String word : lst){
					WordToken tk =  new WordToken(word, getWordWeightValue(nameTypeIdx, word));
					ret.add(tk);
				}
				ret.add(new WordToken("$", fullNamePayload(nameTypeIdx)));
			}
		}
		return ret;
	}
}

