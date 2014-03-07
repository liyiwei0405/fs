package com.funshion.videoMergeUpdatefs_media_serials;

import java.io.IOException;
import java.util.HashMap;

import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.KeyValuePair;
import com.funshion.search.utils.LineReader;

public class cmp {

	public static void main(String[]args) throws IOException{
		HashMap<String,String>map = new HashMap<String, String>();
		String str = Consoler.readString("Gammafile:");
		LineReader lr1 = new LineReader(str);
		while(lr1.hasNext()){
			String line = lr1.next();
			KeyValuePair<String,String> pair = KeyValuePair.parse(line);
			map.put(pair.key, pair.value);
		}
		
		str = Consoler.readString("wb file:");
		lr1 = new LineReader(str);
		while(lr1.hasNext()){
			String line = lr1.next();
			KeyValuePair<String,String> pair = KeyValuePair.parse(line);
//			System.out.println("for " + );
			String a = map.get(pair.key);
			System.out.println(line + "-->" + a);
		}
	}
}
