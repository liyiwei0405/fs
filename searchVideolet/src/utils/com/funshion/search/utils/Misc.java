package com.funshion.search.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Misc {
	public static String bytesToStr(byte[]bs) {
		StringBuilder sb = new StringBuilder();
		for(byte b : bs) {
			sb.append((char)((b&0x0f) + 65));
			sb.append((char)(((b&0xf0) >> 4) + 65));
		}
		return sb.toString();
	}

	public static byte []strToBytes(String a) {

		int len = a.length()/2;
		byte[] b = new byte[len];
		int pos;
		for(int i = 0; i < len; i++) {
			pos = i * 2;
			b[i] = (byte) (a.charAt(pos) - 65 + ((a.charAt(pos+1) - 65) << 4));
		}
		return b;
	}

	public static String substring(String str,
			String prefix, String sufix){
		return getSubString(str,
				prefix, sufix);
	}
	/**
	 * 
	 * @param str
	 * @param prefix perfix, null be viewed as from the begining
	 * @param sufix suffix, null be view as to the end
	 * @return null returned if nothing found
	 */
	public static String getSubString(String str,
			String prefix, String sufix){
		if(str==null)
			return null;
		int st,ed,stLen;
		if(prefix==null){
			st=0;
			stLen=0;
		}else{
			st=str.indexOf(prefix);
			stLen=prefix.length();
		}
		if(st == -1)
			return null;

		if(sufix==null)
			ed=str.length();
		else
			ed=str.indexOf(sufix,st+stLen);
		if(ed==-1)
			return null;
		return str.substring(st+stLen,ed);
	}

	public static ArrayList<String>subs(String str, char prefix,char suffix){
		ArrayList<String>lst = new ArrayList<String>();
		int pos=0,end;
		while(true){
			pos = str.indexOf(prefix,pos);
			if(pos == -1)
				break;
			pos+=1;
			end = str.indexOf(suffix, pos);
			if(end == -1)
				break;
			lst.add(str.substring(pos,end));
			pos = end + 1;
		}
		return lst;
	}
	public static double atod(String str){
		int start=-1;
		int st = 0;
		if(str == null)
			throw new NumberFormatException(str);
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				break;
			}			
		}
		if(st >=  str.length())
			throw new NumberFormatException(str);
		start = st;
		st += 1;
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				continue;
			}else{
				break;
			}
		}
		int ed = st;

		if(st < str.length()){
			if(str.charAt(st) == '.'){
				st++;
				for(;st<str.length();st++){
					char c = str.charAt(st);
					if(c >= '0' && c <= '9'){
						continue;
					}else
						break;
				}
			}
		}
		if(st - ed >1){
			ed = st;
		}
		return Double.parseDouble(str.substring(start,ed));
	}

	/**
	 * works just like c api atoi, 
	 * search for good formatted slice and convert to int
	 * @param str
	 * @return
	 */

	public static int atoi(String str)throws NumberFormatException{
		int start=-1;
		int st = 0;
		if(str == null)
			throw new NumberFormatException(str);
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				break;
			}			
		}
		if(st >=  str.length())
			throw new NumberFormatException(str);
		start = st;
		st += 1;
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				continue;
			}else
				break;
		}
		return Integer.parseInt(str.substring(start,st));
	}
	static String fbdFileName_rewriteTo[] = {
		"CON_",
		"PRN_",
		"AUX_",
		"NUL_",
		"COM1_",
		"COM2_",
		"COM3_",
		"COM4_",
		"COM5_",
		"COM6_",
		"COM7_",
		"COM8_",
		"COM9_",
		"LPT1_",
		"LPT2_",
		"LPT3_",
		"LPT4_",
		"LPT5_",
		"LPT6_",
		"LPT7_",
		"LPT8_",
		"LPT9_",
	};
	static String fbdFileName[] = {
		"CON",
		"PRN",
		"AUX",
		"NUL",
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
		"COM7",
		"COM8",
		"COM9",
		"LPT1",
		"LPT2",
		"LPT3",
		"LPT4",
		"LPT5",
		"LPT6",
		"LPT7",
		"LPT8",
		"LPT9",
	};
	/**
	 * format org to a validate file name
	 * @param org
	 * @return
	 */
	public static String formatFileName(String org){
		return toFileNameFormat(org, '~');
	}
	public static String formatFileName(String org, char c){
		return toFileNameFormat(org, c);
	}
	public static String toFileNameFormat(String org, char rep){
		if(org == null || org.length() == 0){
			return System.currentTimeMillis() + "";
		}
		char cs[] = org.toCharArray();
		char c;
		int len = cs.length;
		for(int i = 0; i < len; i++){
			c = cs[i];
			if(c == '\\'||
					c == '/'||
					c == ':'||
					c == '*'||
					c == '?'||
					c == '"'||
					c == '|'||
					c == '<'||
					c == '>'){
				cs[i] = rep;
			}
		}
		String ret = new String(cs);
		for(int x = 0; x < fbdFileName.length; x ++){
			ret = ret.replace(fbdFileName[x], fbdFileName_rewriteTo[x]);
		}
		return ret;
	}

	public static void del(File file){
		if(file.isDirectory()){
			LogHelper.log.debug("remove %s...", file);
			File[]fs=file.listFiles();
			for(int i=0;i<fs.length;i++){
				del(fs[i]);
			}
			file.delete();
		}else
			file.delete();
	}

	public static void main(String[]a) throws IOException {
		File f;
		f = new File("/0.");
		f.createNewFile();
		
		File f2 = new File("/0");
		System.out.println(f2.equals(f));
		
		
		String s = "POSIX character classes (US-ASCII only) \r\n" + 
				"\\p{Lower} A lower-case alphabetic character: [a-z] \r\n" + 
				"\\p{Upper} An upper-case alphabetic character:[A-Z] \r\n" + 
				"\\p{ASCII} All ASCII:[\\x00-\\x7F] \r\n" + 
				"\\p{Alpha} An alphabetic character:[\\p{Lower}\\p{Upper}] \r\n" + 
				"\\p{Digit} A decimal digit: [0-9] \r\n" + 
				"\\p{Alnum} An alphanumeric character:[\\p{Alpha}\\p{Digit}] \r\n" + 
				"\\p{Punct} Punctuation: One of !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ \r\n" + 
				"\\p{Graph} A visible character: [\\p{Alnum}\\p{Punct}] \r\n" + 
				"\\p{Print} A printable character: [\\p{Graph}\\x20] \r\n" + 
				"\\p{Blank} A space or a tab: [ \\t] \r\n" + 
				"\\p{Cntrl} A control character: [\\x00-\\x1F\\x7F] \r\n" + 
				"\\p{XDigit} A hexadecimal digit: [0-9a-fA-F] \r\n" + 
				"\\p{Space} A whitespace character: [ \\t\\n\\x0B\\f\\r] \r\n" + 
				"";
		System.out.println(formatFileName(s, '~'));
	}
	public static int randInt(int from, int to){
		int itv = to - from;
		if(itv == 0)
			return from;
		int it  = (int) (Math.random() * itv);
		return (from + it);
	}
}