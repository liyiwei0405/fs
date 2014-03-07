package com.funshion.search.segment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LogHelper;

public abstract class Segment {

	public static final Short state_is = 0;//iif this is a word
	public static final Short state_can = 1;//iif this is a word and can construct a longer word
	public static final Short state_may = 2;//iff this can construct a longger word but not a word now

	public static final char separator = ' ';
//	HashMap<String, Short> map;

	public final static int start = 19968;
	public final static int   end = 40864;
	static final String NULL = "";

	public static boolean isSino(int i){		
		if(i >= start&&i <= end)
			return true;
		return false;
	}

	protected boolean addItem(HashMap<String, Short> map, String item,
			boolean reverse, int maxLen) {
		String t;
		Short s;
		int len;
		if(item.length() == 0) {
			return false;
		}
		if (item.length() <= maxLen) {
			t = item;
			s = map.get(t);
			if(s == null)
				map.put(t, state_is);
			else if(s == state_may)
				map.put(t, state_can);
			len = item.length();
			if(len > 2){
				if(reverse) {//FIX this problem
					for(int x = 1; x < len - 1; x ++){
						t = item.substring(x);
						s = map.get(t);
						if(s == null){
							map.put(t, state_may);
						}else if(s == state_is){
							map.put(t, state_can);
						}else if(s == state_may){

						}
					}			
				}else {
					for(int x = 1; x < len; x ++){
						t = item.substring(0, x);
						s = map.get(t);
						if(s == null){
							map.put(t, state_may);
						}else if(s == state_is){
							map.put(t, state_can);
						}else if(s == state_may){

						}
					}
				}
			}
			return true;
		}else {
			return false;
		}
	}
	public abstract TokenHandler segment(String input);
	protected void load(File source, HashMap<String, Short> map,
			boolean reverse) {
		load(source, map, reverse, 4);
	}
	/**
	 * load lexion to hashtable
	 * @param source
	 */
	protected void load(File source, HashMap<String, Short> map,
			boolean reverse, int maxLen){
		String line = null;
		LogHelper.log.debug("Loading Lexicon ...");
		try {
			LineReader lr = new LineReader(source,"utf8");
			int i = 0;
			while ((line = lr.readLine())  !=  null) {
				if (line.indexOf("#")  ==  -1) {
					if(addItem(map, line, reverse, maxLen)) {
						i++;
					}
				}
			}
			lr.close();
			LogHelper.log.debug("total %d words loaded",i);
		} catch (IOException e) {
			LogHelper.log.error("Loading Lexicon failuer");
			e.printStackTrace();

		}
	}

	public static TokenHandler segment(String input, 
			HashMap<String, Short> map, boolean reverse){
		TokenHandler handler ;
		if(input == null)
			return new TokenHandler(null);
		else
			input = input.trim();
		MinStr mstr;
		if(reverse) {
			mstr= new ReverseMinStr(input, 1);
		}else {
			mstr= new NoReverseMinStr(input, 1);
		}
		handler = new TokenHandler(mstr);
		do{
			if(mstr.nextWord(map, reverse)){
				handler.put();
			}else break;
		}while(mstr.forward());
		handler.finish();
		return handler;
	}

	public static short type(char c){
		short ret;
		if(Character.isLetter(c)){
			if(c > 256)
				ret = MinStr.Cn;
			else ret = MinStr.Eng;
		}else{
			if(Character.isDigit(c))
				ret = MinStr.Number;
			else ret = MinStr.Forbidded;
		}
		return ret;
	}
}
