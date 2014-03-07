package com.funshion.search.media.search.atdd;

public abstract class ATDDChecker {

	public static void is(Boolean realValue, String failInfo, Object...args) throws Exception{
		equals(realValue, true, failInfo, args);
	}
	public static void equals(Object realValue, Object target, String failInfo, Object...args) throws Exception{
		if(realValue == target){
			return;
		}
		if(realValue == null || target == null){
			throw new Exception(String.format(failInfo, args));
		}
		if(!target.equals(realValue)){
			throw new Exception(String.format(failInfo, args));
		}
	}
	
	public static void info(String pattern, Object...args){
		System.out.println(String.format(pattern, args));
	}
	
	public abstract void test() throws Exception;
	
}
