package com.funshion.search.media.search.mediaTitleRewriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineReader;

public class CnNumRewrite {
	//	Pattern p = Pattern.compile("0oO一二三四五六七八九十零百千万壹贰叁肆伍陆柒捌玖拾");
	static final Pattern p = Pattern.compile("ｏ０Ｏ一二三四五六七八九十零百千万壹贰叁肆伍陆柒捌玖拾");
	static final String strWords = "ｏ０Ｏ一二三四五六七八九十零百千万仟萬壹贰叁肆伍陆柒捌玖拾";

	Set<Character>set = new HashSet<Character>();
	CnNumRewrite(){
		for(char c : strWords.toCharArray()){
			set.add(c);
		}
	}
	public static CnNumRewrite instance = new CnNumRewrite();
	
	/**
	 * convert cnNum to alb-num if possible
	 * @param input
	 * @return
	 */
	public static String cnNumRewrite(String input){
		String ret = instance.tryRewrite(input);
		if(ret == null){
			return input;
		}
		return ret;
	}
	public static String cnNumTryRewrite(String input){
		return instance.tryRewrite(input);
	}
	/**
	 * XXX if not rewrite return null;
	 * @param input
	 * @return
	 */
	public String tryRewrite(String input){
		int cnt = 0;
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < input.length(); x ++){
			char c = input.charAt(x);
			switch(c){
			case 'ｏ':
			case '０':
			case 'Ｏ':
				c = '零';
				break;
			case '仟':
				c = '千';
				break;
			case '萬':
				c = '万';
				break;
			case '壹':
				c = '一';
				break;
			case '贰':
				c = '二';
				break;
			case '叁':
				c = '三';
				break;
			case '肆':
				c = '四';
				break;
			case '伍':
				c = '五';
				break;
			case '陆':
				c = '六';
				break;
			case '柒':
				c = '七';
				break;
			case '捌':
				c = '八';
				break;
			case '玖':
				c = '九';
				break;
			case '拾':
				c = '十';
				break;
			}
			if(set.contains(c)){
				sb.append(c);
				cnt ++;
			}else{
				sb.append(' ');
			}
		}
		if(cnt == 0){
			return null;
		}

		StringBuilder ret = new StringBuilder();
		int lastType = 0;// 1 is hanzi, 2 alb, 3 other
		for(int x = 0; x < input.length(); x ++){
			char c = sb.charAt(x);
			if(c == ' '){
				char nchar = input.charAt(x);
				if(Character.isDigit(nchar)){
					if(lastType == 1){
						ret.append(' ');
					}
					lastType = 2;
				}else{
					lastType = 3;
				}
				ret.append(input.charAt(x));
				
			}else{
				int start = x;
				for(;x < input.length(); x ++){
					char nchar = sb.charAt(x);
					if(nchar == ' '){
						break;
					}
				}
				String sub = input.substring(start, x);
				int num = rewrite(sub);
				if(num > 0){
					if(lastType == 2){
						ret.append(' ');
					}
					ret.append(num);
					lastType = 1;
				}else{//parse fail, append org value
					ret.append(sub);
					lastType = 0;
				}
				x --;
			}
		}
		String retString = ret.toString();
		if(retString.equals(input)){
			return null;
		}
		return ret.toString();
	}
	private int rewrite(String str){
		if(str == null || str.length() == 0){
			return RegRewriteHz.errorRet;
		}
		if(str.length() ==1){
			char c = str.charAt(0);
			if(c == '百' || c =='千' || c == '万' || c == '零'){
				return RegRewriteHz.errorRet;
			}
		}

		int v = RegRewriteHz.parseNum(str);
		if(v > 0){
			return v;
		}
		return RegRewriteHz.errorRet;
	}
	
	public static void main(String[]args) throws IOException{
		CnNumRewrite cf = new CnNumRewrite();
		LineReader lr = new LineReader("/testWords");

		while(lr.hasNext()){
			String line = lr.next().trim();
			//			System.out.println(line);
			String ctt = cf.tryRewrite(line);
			if(ctt == null){
				System.out.println(line + " ----> " + ctt);
				System.out.flush();
			}else{
				System.err.println(line + " ----> " + ctt);
				System.err.flush();
				Consoler.readString("press enter to continue...");
			}
		}
		lr.close();
	}
}
