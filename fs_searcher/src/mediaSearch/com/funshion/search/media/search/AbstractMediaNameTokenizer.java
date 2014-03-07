package com.funshion.search.media.search;

import java.io.Reader;

/**
 * only split word by \t
 * @author liying
 *
 */
public abstract class AbstractMediaNameTokenizer  extends AbstractMediaNameTokenizerWithoutScores{
	static final byte namePayload[] = new byte[]{
		6, 3, 2, 2
	};
	static final byte[] lenMap = new byte[]{
		1, 3, 5, 7, 10
	};
	static final byte fullNamePayload[][] = new byte[][]{
		new byte[]{-1}, 
		new byte[]{-8}, 
		new byte[]{-127}, 
		new byte[]{-128}
	};
	static final byte[][][] wordWeightMap = new byte [namePayload.length][][];
	static{
		for(int x = 0; x < wordWeightMap.length; x ++){
			wordWeightMap[x] = new byte[lenMap.length][];
			for(int y = 0; y < lenMap.length; y++){
				wordWeightMap[x][y] = new byte[]{(byte) (namePayload[x] * lenMap[y])};
			}
		}
//		for(int x = 0; x < namePayload.length; x ++){
//			for(int y = 0; y < lenMap.length; y ++){
//				System.out.print(wordWeightMap[x][y][0] + " ");
//			}
//		}
	}
	public byte[] getWordWeightValue(int typeIndex, String word){
		int y = word.length() - 1;
		if(y < 0){
			return null;
		}
		if(y > 4){
			y = 4;
		}
		return wordWeightMap[typeIndex][y];
	}

	public AbstractMediaNameTokenizer(Reader reader) {
		super(reader);
	}
	public byte[] fullNamePayload(int nameTypeIdx){
		return fullNamePayload[nameTypeIdx];
	}
}
