package com.funshion.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.funshion.search.utils.LineWriter;

public abstract class FlushableRecord {
	public static final Logger log = Logger.getLogger("fRecord");
	public static final String RECORD_START_FLAG = "^";
	public static final String RECORD_END_FLAG = "$";
	public static String fmtItem(Object value) throws IOException{

		if(value == null){
			return "";
		}
		String var = value.toString();
		return var.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
	}
	
	public static boolean isRecordStart(String str){
		return RECORD_START_FLAG.equals(str);
	}
	public static boolean isRecordEnd(String str){
		return RECORD_END_FLAG.equals(str);
	}
	/**
	 * 
	 * @param lw
	 * @param name
	 * @param value if value instanceof List, then write them one by one(split by \t, and original \t are replaced by ' '), else write as single token
 	 * @throws IOException
	 */
	//FIXME should be a global method
	@SuppressWarnings("rawtypes")
	protected static void writeItem(LineWriter lw, String name, Object value) throws IOException{
		lw.write(name);
		lw.write('\t');
		if(value != null){
			if(value instanceof Collection){
				Collection lst = (Collection) value;
				int idx = 0;
				for(Object o : lst){
					if(idx > 0){
						lw.write('\t');
					}
					idx ++;
					String itm = fmtItem(o);
					lw.write(itm);
				}
			}else{
				String itm = fmtItem(value);
				lw.write(itm);
			}
		}
		lw.write('\n');
	}
	public abstract void flushTo(LineWriter lw) throws IOException;
}
