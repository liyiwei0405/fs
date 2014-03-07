package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * a 万
 * b 千
 * c 百
 * d 十
 * e 个
 * @author liying
 *
 */
public class RegRewriteHz100_99999 extends RegRewriteHz{

	private static char[] num1_10 = 
			"一二三四五六七八九十".toCharArray();
	private static char[] num0_10 = 
			"零一二三四五六七八九十".toCharArray();
	Pattern e_a = Pattern.compile("([一二三四五六七八九])万");
	Pattern e_b = Pattern.compile("([一二三四五六七八九])千");
	Pattern e_c = Pattern.compile("([一二三四五六七八九])百");
	Pattern c_d_e = Pattern.compile("([零一二三四五六七八九])([零一二三四五六七八九])([零一二三四五六七八九])");
	Pattern b_c_d_e = Pattern.compile("([零一二三四五六七八九])([零一二三四五六七八九])([零一二三四五六七八九])([零一二三四五六七八九])");
	Pattern b_0_e = Pattern.compile("([一二三四五六七八九])千零([一二三四五六七八九])");
	Pattern b_c = Pattern.compile("([一二三四五六七八九])千([一二三四五六七八九])");
	Pattern c_d = Pattern.compile("([一二三四五六七八九])百([一二三四五六七八九])");
	Pattern d_ex = Pattern.compile("([一二三四五六七八九])([零一二三四五六七八九])");
	Pattern c_d_e_0 = Pattern.compile("([一二三四五六七八九])百([零一二三四五六七八九])十([零一二三四五六七八九])");
	//一万三千
	Pattern a_b_0 = Pattern.compile("([一二三四五六七八九])万([一二三四五六七八九])千");
	//一万三千
	Pattern b_c_0 = Pattern.compile("([一二三四五六七八九])千([一二三四五六七八九])百");
	//三百六十
	Pattern c_d_0 = Pattern.compile("([一二三四五六七八九])百([一二三四五六七八九])十");
	public RegRewriteHz100_99999() {}
	int ret_d_ex(String str){
		Matcher m = d_ex.matcher(str);

		if(m.matches()){
			int ret = intNum(m.group(1).charAt(0)) * 10  + 
					intNum(m.group(2).charAt(0)) ;
//			System.out.println("\t" + str + "<===========>" + ret);
			if(ret != errorRet){
				return ret;
			}
		}
		return errorRet;
	}
	int ret_b_c(String str){
		Matcher m = b_c.matcher(str);

		if(m.matches()){
			int ret = intNum(m.group(1).charAt(0)) * 1000  + 
					intNum(m.group(2).charAt(0)) *100 ;
			//			System.out.println("\t" + str + "<===========>" + ret);
			if(ret != errorRet){
				return ret;
			}
		}
		return errorRet;
	}
	int ret_c_d(String str){
		Matcher  m = c_d.matcher(str);
		if(m.matches()){
			int ret = intNum(m.group(1).charAt(0)) * 100  + 
					intNum(m.group(2).charAt(0)) *10 ;
			//			System.out.println("\t" + str + "<===========>" + ret);
			if(ret != errorRet){
				return ret;
			}
		}
		return errorRet;
	}
	int ret_c_d_0(String str){
		Matcher  m = c_d_e_0.matcher(str);
		if(m.matches()){
			int ret = intNum(m.group(1).charAt(0)) * 100  + 
					intNum(m.group(2).charAt(0)) *10 +
					intNum(m.group(3).charAt(0));
			//			System.out.println("\t" + str + "<===========>" + ret);
			if(ret != errorRet){
				return ret;
			}
		}
		return errorRet;
	}
	int ret_c_d_e(String str){
		Matcher m = c_d_e.matcher(str);
		if(!m.matches()){
			return errorRet;
		}
		return intNum(m.group(1).charAt(0)) * 100  + 
				intNum(m.group(2).charAt(0)) * 10  +
				intNum(m.group(3).charAt(0));

	}
	public int getNum(String str) {
		if(str.length() < 2){
			return errorRet;
		}else if(str.length() == 2){
			if(str.charAt(1) == '万'){
				return this.retValue_e_x(str.charAt(0), 10000);
			}else if(str.charAt(1) == '千'){
				return this.retValue_e_x(str.charAt(0), 1000);
			}else if(str.charAt(1) == '百'){
				return this.retValue_e_x(str.charAt(0), 100);
			}else{
				int ret;
				ret = ret_d_ex(str);
				if(ret != errorRet){
					return ret;
				}
				return errorRet;
			}
		}else if(str.length() == 3){
			int ret;
			ret = ret_c_d_e(str);
			if(ret != errorRet){
				return ret;
			}
			ret = ret_b_c(str);
			if(ret != errorRet){
				return ret;
			}

			ret = ret_c_d(str);
			if(ret != errorRet){
				return ret;
			}

		}else if(str.length() == 4){
			Matcher m = b_c_d_e.matcher(str);
			if(m.matches()){
				return intNum(m.group(1).charAt(0)) * 1000  + 
						intNum(m.group(2).charAt(0)) * 100  + 
						intNum(m.group(3).charAt(0)) * 10 +
						intNum(m.group(4).charAt(0));
			}
			
			m = b_0_e.matcher(str);
			if(m.matches()){
				int ret = intNum(m.group(1).charAt(0)) * 1000  + 
						intNum(m.group(2).charAt(0)) ;
				//					System.out.println("\t" + str + "<===========>" + ret);
				if(ret != errorRet){
					return ret;
				}
			}
			//一万四千
			m = a_b_0.matcher(str);
			if(m.matches()){
				int ret =  intNum(m.group(1).charAt(0)) * 10000  + 
						intNum(m.group(2).charAt(0)) * 1000;
//				System.out.println("\t" + str + "<===========>" + ret);
				return ret;
			}
			//一千五百
			m = b_c_0.matcher(str);
			if(m.matches()){
				int ret =  intNum(m.group(1).charAt(0)) * 1000  + 
						intNum(m.group(2).charAt(0)) * 100;
//				System.out.println("\t" + str + "<===========>" + ret);
				return ret;
			}
			//三百五十
			m = c_d_0.matcher(str);
			if(m.matches()){
				int ret =  intNum(m.group(1).charAt(0)) * 100  + 
						intNum(m.group(2).charAt(0)) * 10;
//				System.out.println("\t" + str + "<===========>" + ret);
				return ret;
			}
		}else if(str.length() == 5){
			int ret = ret_c_d_0(str);
			if(ret != errorRet){
				return ret;
			}
		}
		return errorRet;
	}
	private int intNum(char c){
		for(int x = 0; x < num1_10.length; x ++){
			if(c == num0_10[x]){
				return x;
			}
		}
		return errorRet;
	}
	/**
	 * 一二....十
	 * @param c
	 * @return
	 */
	protected int retValue_e_x(char e, int base){
		for(int x = 0; x < num1_10.length; x ++){
			if(e == num1_10[x]){
				return (x + 1) * base;
			}
		}
		return errorRet;
	}

}