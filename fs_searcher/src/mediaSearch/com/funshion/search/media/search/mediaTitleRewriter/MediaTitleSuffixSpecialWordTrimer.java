package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.ArrayList;
import java.util.HashMap;

import com.funshion.search.media.search.MediaSSDaemon;
import com.funshion.search.utils.ConfigReader;

public class MediaTitleSuffixSpecialWordTrimer {
	public static final MediaTitleSuffixSpecialWordTrimer instance = new MediaTitleSuffixSpecialWordTrimer();
	HashMap<String, Integer>aliaMap = new HashMap<String, Integer>();
	HashMap<Integer, String>aliaGroup = new HashMap<Integer,  String>();
	private final String[] halfTrime;
	public String getAlia(String key){
		Integer x = this.aliaMap.get(key);
		if(x == null){
			return key;
		}
		String ret = this.aliaGroup.get(x);
		if(ret == null){
			return key;
		}else{
			return ret;
		}
	}
	
	private MediaTitleSuffixSpecialWordTrimer(){
		try{
			ConfigReader cr = new ConfigReader(MediaSSDaemon.daemonConfig, "indexHalfTrime");
			String indexHalfTrime = cr.getValue("words");
			if(indexHalfTrime == null || indexHalfTrime.length() == 0){
				halfTrime = new String[0];
			}else{
				ArrayList<String>lst = new ArrayList<String>();
				String tokens[] = indexHalfTrime.toLowerCase().split(",");
				for(String x : tokens){
					x = x.trim();
					if(x.length() > 0){
						lst.add(x);
					}
				}
				halfTrime = new String[lst.size()];
				lst.toArray(halfTrime);
			}
			String aliaStr = "alias_";
			for(int x = 0; x < 100; x ++){
				String aliaName = aliaStr + x;
				String str = cr.getValue(aliaName);
				if(str != null && str.length() > 0){
					String [] tokens = str.toLowerCase().split(",");
					ArrayList<String>lst = new ArrayList<String>();
					for(String v : tokens){
						v = v.trim();
						if(v.length() == 0){
							continue;
						}
						lst.add(v);
					}
					if(lst.size() > 0){
						aliaGroup.put(x, lst.get(0));
					}
					for(String xx : lst){
						this.aliaMap.put(xx, x);
					}
				}
				
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public String[] trim(String str){
		if(str == null){
			return null;
		}
		str = str.trim();
		for(String x : this.halfTrime){
			if(str.startsWith(x)){
				return new String[]{
						str.substring(x.length()).trim(), 
						x};
			}else if(str.endsWith(x)){
				return new String[]{
						str.substring(0, str.length() - x.length()).trim(),
						x};
			}
		}
		return null;
	}
}
