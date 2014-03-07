package com.funshion.search;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.document.*;

import com.funshion.search.utils.ConfigReader;

public class FieldIndexDef {
	static int getValidIndex(String[]tokens, int startx){
		for(int start = startx; start < tokens.length; start ++){
			if(tokens[start].length() > 0){
				return start;
			}
		}
		return tokens.length;
	}

	private static final Set<String>accFieldTypes = new HashSet<String>();

	static{
		accFieldTypes.add("IntField".toLowerCase());
		accFieldTypes.add("TextField".toLowerCase());
	}

	private static final Set<String>accSegTypes = new HashSet<String>();
	static{
		accSegTypes.add("noToken".toLowerCase());
		accSegTypes.add("space".toLowerCase());
		accSegTypes.add("segment".toLowerCase());
	}

	public static boolean acceptFieldTypes(String str){
		return accFieldTypes.contains(str.toLowerCase().trim());
	}
	public static class FieldDef{
		final String name;
		final int weight;
		FieldDef(String name, int weight){
			this.name = name;
			this.weight = weight;
		}

		public String toString(){
			return name + " * "  + weight;
		}
	}
	private boolean store;
	private String name;
	private String indexAs;
	private ArrayList<FieldDef>fields = new ArrayList<FieldDef>();
	private String tokenBy;
	private String secName;
	private FieldIndexDef(){}

	public static FieldIndexDef loadSection(File cfgFile, String secName) throws Exception{
		FieldIndexDef ret = new FieldIndexDef();
		ret.secName = secName;
		ConfigReader cr = new ConfigReader(cfgFile, secName);
		ret.name = validConfigValue(cr.getValue("name"));
		if(ret.name.length() == 0){
			throw new Exception("invalid name for secName = " + secName);
		}
		ret.indexAs = cr.getValue("indexAs");
		ret.indexAs = validConfigValue(ret.indexAs);
		if(!acceptFieldTypes(ret.indexAs)){
			throw new Exception("invalid indexAs value for secName = " + secName);
		}

		ret.store = cr.getInt("store", 0) == 1;

		String useField = cr.getValue("useFields");
		useField = validConfigValue(useField);
		if(useField.length() == 0){
			throw new Exception("invalid useField for secName = " + secName);
		}

		String tokens[] = useField.split(",");
		for(String x : tokens){
			x = validConfigValue(x);
			if(x.length() == 0){
				continue;
			}
			String tvs[] = x.split("\\*");
			if(tvs.length == 1){
				ret.fields.add(new FieldDef(x, 1));
			}else if(tvs.length == 2){
				ret.fields.add(new FieldDef(tvs[0].trim(), Integer.parseInt(tvs[1].trim())));
			}else{
				throw new Exception("ERROR Field Define! " + x);
			}
		}
		ret.tokenBy = cr.getValue("tokenBy", "noToken");
		return ret;
	}
	private static String validConfigValue(String str){
		if(str == null){
			return "";
		}
		return str.trim();
	}

	public boolean isStore() {
		return store;
	}

	public String getName() {
		return name;
	}

	public String getIndexAs() {
		return indexAs;
	}

	public ArrayList<FieldDef> getFields() {
		return fields;
	}

	public String getTokenBy() {
		return tokenBy;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(this.secName);
		sb.append(']');
		sb.append('\n');

		sb.append("name = ");
		sb.append(name);
		sb.append('\n');

		sb.append("indexAs = ");
		sb.append(this.indexAs);
		sb.append('\n');

		sb.append("store = ");
		sb.append(this.store ? 1 : 0);
		sb.append('\n');

		sb.append("tokenBy = ");
		sb.append(this.tokenBy);
		sb.append('\n');


		sb.append("useFields = ");
		int idx = 0;
		for(FieldDef fd : this.fields){
			if(idx > 0){
				sb.append(',');
				sb.append(' ');
			}
			idx ++;
			sb.append(fd);
		}
		sb.append('\n');
		return sb.toString();
	}

	class IntFieldHelper{
		public org.apache.lucene.index.IndexableField getField(String[]tokens){
			int value = 0;
			int idx = 0;
			while(++idx < tokens.length){
				idx = getValidIndex(tokens, idx);
				if(idx > tokens.length){
					//FIXME noValue
					return null;
				}else{
					try{
						value = Integer.parseInt(tokens[idx].trim());
						break;
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			IntField inf = new IntField(name, value, store ? Field.Store.YES :  Field.Store.NO);
			return inf;
		}
	}
	class TextFieldHelper{
		public org.apache.lucene.index.IndexableField getField(String[]tokens){
			int value = 0;
			int idx = 0;
			while(++idx < tokens.length){
				idx = getValidIndex(tokens, idx);
				if(idx > tokens.length){
					//FIXME noValue
					return null;
				}else{
					try{
						value = Integer.parseInt(tokens[idx].trim());
						break;
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			TextField inf = new TextField(name, value, store ? Field.Store.YES :  Field.Store.NO);
			return inf;
		}
	}
}
