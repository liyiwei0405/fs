package com.funshion.search.utils;

import java.io.IOException;
import java.util.HashSet;

public class CharsetGbkRecheck {

	HashSet<Character>set = new HashSet<Character>();


	private CharsetGbkRecheck () {
		loadSet();
	}
	private synchronized void loadSet(){
		if(set.size() > 0){
			return;
		}
		try {
			LineReader lr = new LineReader("./config/gbkErrorTable.lex");
			while(lr.hasNext()){
				String line = lr.next().trim();
				if(line.length() > 0){
					try{
						set.add((char) Integer.parseInt(line));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			lr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static final CharsetGbkRecheck instance = new CharsetGbkRecheck() ;

	public boolean isGoodGBK(String str){
		return isGoodGBK(str, 0.3);
	}

	public boolean isGoodGBK(String str, double errorFragment){

		int Cn = 0, Error = 0;
		char[] cs = str.toCharArray();
		for(char c : cs){
			if(c > 128){
				Cn++;
				if(this.set.contains(c)){
					Error ++;
				}
			}
		}
		if(Cn > 0 && Error > 0){
			double d = Error / (double)Cn;
			if(errorFragment < d){
				return false;
			}
		}
		return true;
	}

}
