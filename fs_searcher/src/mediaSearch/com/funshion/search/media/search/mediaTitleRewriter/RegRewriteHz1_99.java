package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegRewriteHz1_99 extends RegRewriteHz{

	private static char[] num1_10 = 
			"一二三四五六七八九十".toCharArray();
	Pattern x_y_z = Pattern.compile("([一二三四五六七八九])十([一二三四五六七八九])");
	public RegRewriteHz1_99() {}

	public int getNum(String str) {
		try{
			if(str.length() == 1){
				return retValue_z(str.charAt(0));
			}else if(str.length() == 2){
				if(str.charAt(0) == '十'){
					return retValue_y_z(str);
				}else if(str.charAt(1) == '十' || str.charAt(1) == '0' || str.charAt(1) == 'o'|| str.charAt(1) == 'O'){
					return retValue_x_y(str);
				}else{
					return retValue_x_z(str);
				}
			}else if(str.length() == 3){
				if(str.charAt(1) == '十'){
					return retValue_x_y_z(str);
				}
			}else{
				return errorRet;
			}

			return Integer.parseInt(str);
		}catch(Exception e){
			return errorRet;
		}
	}
	/**
	 * 一二....十
	 * @param c
	 * @return
	 */
	protected int retValue_z(char c){
		for(int x = 0; x < num1_10.length; x ++){
			if(c == num1_10[x]){
				return x + 1;
			}
		}
		return errorRet;
	}
	/**
	 * x十y， 五十三
	 * @param str
	 * @return
	 */
	protected int retValue_x_y_z(String str){
		Matcher m = x_y_z.matcher(str);
		if(!m.matches()){
			return errorRet;
		}
		char x = m.group(1).charAt(0);
		char z = m.group(2).charAt(0);
		return retValue_z(x) * 10 + retValue_z(z);
	}
	/**
	 * x十， 三十， 三0， 三零 三O
	 * @param str
	 * @return
	 */
	protected int retValue_x_z(String str){
		int x = retValue_z(str.charAt(0));
		if(x < 1 || x > 9){
			return errorRet;
		}
		int z = retValue_z(str.charAt(1));
		if(z < 1 || z > 9){
			return errorRet;
		}
		return x * 10 + z;
	}
	/**
	 * x十， 三十， 三0， 三零 三O
	 * @param str
	 * @return
	 */
	protected int retValue_x_y(String str){
		int x = retValue_z(str.charAt(0));
		if(x < 1 || x > 9){
			return errorRet;
		}
		return x * 10;
	}
	/**
	 * 十z， 如十三
	 * @param str
	 * @return
	 */
	protected int retValue_y_z(String str){
		int x = retValue_z(str.charAt(1));
		if(x < 1 || x > 9){
			return errorRet;
		}
		return  10 + x;
	}
}