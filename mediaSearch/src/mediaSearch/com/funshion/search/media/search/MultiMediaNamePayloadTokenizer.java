package com.funshion.search.media.search;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * only split word by \t
 * @author liying
 *
 */
public final class MultiMediaNamePayloadTokenizer extends AbstractMediaNameTokenizer {
	
	public MultiMediaNamePayloadTokenizer(Reader reader) {
		super(reader);
	}

	@Override
	public List<WordToken> makeTokens(String linesStr){
		String lines[] = linesStr.split("\n");
		List<WordToken> ret = new ArrayList<WordToken>();
		for(int nameTypeIdx = 0; nameTypeIdx < 4; nameTypeIdx ++){
			String line = lines[nameTypeIdx];
			ret.addAll(super.toWordToken(line, nameTypeIdx));
		}
		return ret;
	}
}
