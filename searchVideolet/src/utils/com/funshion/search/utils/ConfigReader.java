package com.funshion.search.utils;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * TC is a basic tool to read config file
 * the configure file should constructed as
 * #---- notice,lines starting with '#' will be ingnored
 * key=value  <b>key-value pair</b>
 * 
 *  NOTICE:all of the line will be trimed at first
 *  NOTICE:the line contains no '='symbol will be ingored
 *  NOTICE:if no appender after '=' the value will be viewed as 0-length String
 *  NOTICE:if no prifix before '=', the line will be viewed as illegal and ignored
 * 
 * @author beiming
 *
 */
public class ConfigReader {
	Map<String,String> map;
	ArrayList<KeyValuePair<String, String>> list = new ArrayList<KeyValuePair<String, String>>();
	protected LineReader reader=null;
	public final String encode,section;
	public final File configFile;
	private boolean readed;
	static final String encoder = "utf8";
	public ConfigReader(String path,String section){
		this(new File(path), section, encoder);
	}
	public ConfigReader(File path,String section){
		this(path, section, encoder);
	}
	public ConfigReader(String path,String section,String encode){
		this(new File(path),section,encode);
	}
	public ConfigReader(File path,String section,String encode){
		this.configFile=path;
		this.encode=encode;
		if(section.startsWith("[")){
			this.section=section.toUpperCase();
		}else
			this.section="["+section.toUpperCase()+"]";
	}
	/**
	 * read all config into memory.
	 * @return
	 * @throws IOException 
	 */
	public synchronized Map<String,String>read2Map() throws IOException{
		readAll();
		if(map == null){
			map = new HashMap<String,String>();
			for(KeyValuePair<String, String> pair : list){
				map.put(pair.getKeyString(), pair.getValueString());
			}
		}
		return map;
	}


	/**
	 * get all <key = value> pair from a special section.
	 * This method is a step method and need not to close the reader becouse
	 * in this method we has already close the reader
	 * @return never null
	 * @throws IOException 
	 */
	private synchronized void readAll() throws IOException{
		if(this.readed){
			return ;
		}
		this.readed = true;
		reader = new LineReader(configFile, encode);

		boolean enter=false;
		while (true){
			String str=null;
			str = reader.readLine();

			if (str == null)
				break;
			str=str.trim();
			if(enter){		
				if(str.startsWith("[")){//in enter,but new section started, so we break;
					break;
				}
			}else{
				if(str.compareToIgnoreCase(section)==0){
					enter=true;
					continue;
				}else{
					continue;//short cut if not enter the section
				}
			}

			KeyValuePair<String, String> kv=KeyValuePair.parseLine(str);
			if(kv==null)// parse failure,note line or illegal line 
				continue;//ignore the line
			list.add(kv);
		}
		reader.close();
	}


	/**
	 * this method get the value by walking throgh the list.
	 * when finding one of the matched pair,this method will
	 * return, no matter how many pairs will match  
	 * becouse efficiency is low,and TC may not
	 * give all of the good results
	 * @param key,get the specificated key's value pair
	 * @return
	 * @throws IOException 
	 */
	public String getValue(String key) throws IOException{
		return getValue(key, null);
	}

	public String getValue(String key, String defaultValue) throws IOException {
		if(map == null){
			this.read2Map();
		}
		if(key == null)
			return defaultValue;
		String value = map.get(key.trim());
		return value == null ? defaultValue : value;
	}

	/**
	 * if null or not good format return 0
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	public int getInt(String key) throws IOException {
		return getInt(key, 0);
	}

	public int getInt(String key, int defalutValue) throws IOException {
		String v = this.getValue(key);
		if(v == null)
			return defalutValue;
		try {
			return Integer.parseInt(v);
		}catch(Exception e) {
			return defalutValue;
		}
	}

	public ArrayList<KeyValuePair<String, String>>getAllConfig() throws IOException{
		readAll();
		return this.list;
	}
}
