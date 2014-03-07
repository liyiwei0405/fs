package com.funshion.search.media.search.mediaTitleRewriter;


public abstract class RegRewriteHz{
	public static final int errorRet = RegRewriteHzSuffix.errorRet;

	public abstract int getNum(String str) ;

	private static RegRewriteHz1_99 r1 = new RegRewriteHz1_99();
	private static RegRewriteHz100_99999 r100 = new RegRewriteHz100_99999();
	public static int parseNum(String str){
		int ret = r1.getNum(str);
		if(ret == errorRet){
			ret = r100.getNum(str);
//			if(ret != errorRet){
//				System.err.println(str + "<==>" + ret);
//			}
		}
		return ret;
	}
}