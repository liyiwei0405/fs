package com.funshion.search.media.search;

import java.io.Reader;
import java.util.List;

/**
 * only split word by \t
 * @author liying
 *
 */
public class MediaNamePayloadTokenizer extends AbstractMediaNameTokenizer {

	protected final int nameTypeIdx;

	public MediaNamePayloadTokenizer(Reader reader, int nameTypeIdx) {
		super(reader);
		this.nameTypeIdx = nameTypeIdx;
	}

	@Override
	public List<WordToken> makeTokens(String line){
		return super.toWordToken(line, nameTypeIdx);
	}
	
	
	

	
	
	
	
	
	
}
