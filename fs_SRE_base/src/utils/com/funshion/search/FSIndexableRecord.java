package com.funshion.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class FSIndexableRecord extends IndexableRecord{
	Map<String, String>fieldValues = new HashMap<String, String>();
	public void putItem(String key, String values){
		this.fieldValues.put(key.intern(), values);
	}
	public String valueOf(String key){
		String ret = fieldValues.get(key);
		return ret;
	}
//	public String valueOf(char key){
//		String ret = fieldValues.get(key);
//		return ret.substring(2, ret.length()).trim();
//	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, String>> itr = fieldValues.entrySet().iterator();
		while(itr.hasNext()){
			sb.append(itr.next());
			sb.append('\n');
		}
		return sb.toString();
	}
}
