package com.funshion.search.media.search.mediaTitleRewriter;

public class RegRewriteHzSuffix extends SuffixNum4XYZ{
	
	RegRewriteHz1_99 rw = new RegRewriteHz1_99();
	public RegRewriteHzSuffix() {
		super("0oO一二三四五六七八九十零");
	}

	@Override
	public int getNum(String str) {
		return rw.getNum(str);
	}

}