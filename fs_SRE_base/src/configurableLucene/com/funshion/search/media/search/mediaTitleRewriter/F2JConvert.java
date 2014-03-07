package com.funshion.search.media.search.mediaTitleRewriter;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LogHelper;

public class F2JConvert {
	LogHelper log = new LogHelper("f2j");
	Map<Character, Character>map = new HashMap<Character, Character>();
	public static final F2JConvert instance = new F2JConvert();
	private F2JConvert(){
		try{
			LineReader lr = new LineReader(ConfUtils.getConfFile("f2j.txt"), Charset.forName("utf-8"));
			while(lr.hasNext()){
				String line = lr.next();
				line = line.trim();
				if(line.length() == 0){
					continue;
				}
				if(line.length() != 3){
					log.warn("error f2j %s", line);
					continue;
				}
				char ft = line.charAt(0);
				char jt = line.charAt(2);
				if(ft == jt){
					continue;
				}
				map.put(ft, jt);
			}
			lr.close();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		log.info("f2j total map size %s", map.size());
	}
	
	public char convert(char c){
		Character ret = this.map.get(c);
		if(ret == null){
			return c;
		}
		return ret;
	}
	public boolean needConvert(String str){
		char[] ret = str.toCharArray();
		for(char c : ret){
			Character cvt = this.map.get(c);
			if(cvt != null){
				return true;
			}
		}
		return false;
	}
	
	public String convert(String str){
		StringBuilder sb = new StringBuilder();
		char[] ret = str.toCharArray();
		for(char c : ret){
			sb.append(convert(c));
		}
		
		return sb.toString();
	}
	
}
