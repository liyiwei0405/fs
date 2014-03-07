package com.funshion.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.KeyValuePair;

public class ISNSchemaDefine {

	final File cfgFile;
	private String businessName;
	private int version;
	private List<KeyValuePair<String, String>>docMap = new ArrayList<KeyValuePair<String, String>>();
	private List<KeyValuePair<String, FieldIndexDef>>fieldMap = new ArrayList<KeyValuePair<String, FieldIndexDef>>();
	public ISNSchemaDefine(File cfgFile) throws Exception{
		this.cfgFile = cfgFile;
		this.init();
	}

	protected void init() throws Exception{
		ConfigReader crMain = new ConfigReader(cfgFile, "schema_main");
		this.version = crMain.getInt("version");
		if(version < 1000){
			throw new Exception("error version " + version);
		}
		businessName = crMain.getValue("businessName");
		if(businessName == null || businessName.length() == 0){
			throw new Exception("No businessName set for config " + cfgFile);
		}
		if(!cfgFile.getName().equals(businessName + "." + version + ".isncfg")){
			throw new Exception("file name not in business.version.isncfg for cfg:" + cfgFile);
		}
		String mapFields = crMain.getValue("mapFields");
		String tokens[] = mapFields.split(",");
		for(String x : tokens){
			x = x.trim();
			KeyValuePair<String, String> pair = KeyValuePair.parse(x);
			if(pair == null){
				throw new Exception("invalid mapFields define");
			}
			docMap.add(pair);
		}
		for(int x = 0; x < 100; x ++){
			String loadModule = "load_" + String.format("%02d", x);
			String section = crMain.getValue(loadModule);
			if(section == null){
				continue;
			}
			section = section.trim();
			if(section.length() == 0){
				continue;
			}
			//check section is load!
			for(KeyValuePair<String, FieldIndexDef> p : this.fieldMap){
				if(p.key.equalsIgnoreCase(section.toLowerCase())){
					throw new Exception("invalid mapFields define, already defined section " + section);
				}
			}
			FieldIndexDef sec = FieldIndexDef.loadSection(cfgFile, section);
			this.fieldMap.add(new KeyValuePair<String, FieldIndexDef>(section, sec));
		}
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[schema_main]\n");
		sb.append("version = " + this.version);
		sb.append('\n');
		sb.append("businessName = " + this.businessName);
		sb.append('\n');
		
		sb.append("mapFields = ");
		int idx = 0;
		for(KeyValuePair<String, String> pair : this.docMap){
			if(idx > 0){
				sb.append(',');
				sb.append(' ');
			}
			idx ++;
			sb.append(pair.key);
			sb.append('=');
			sb.append(pair.value);

		}
		sb.append('\n');

		idx = 1;
		ArrayList<FieldIndexDef> fds = new ArrayList<FieldIndexDef>();
		for(KeyValuePair<String, FieldIndexDef> e : this.fieldMap){
			sb.append(String.format("load_%02d = ", idx));
			idx ++;
			sb.append(e.key);
			fds.add(e.value);
			sb.append('\n');
		}
		sb.append('\n');
		sb.append('\n');
		for(FieldIndexDef def : fds){
			sb.append(def);
			sb.append('\n');
		}
		return sb.toString();
	}
	public static void main(String[]args) throws Exception{
		ISNSchemaDefine def = new ISNSchemaDefine(new File("./config/index/videoletISN.2000.isncfg"));
		System.out.println(def);
	}

	public String getName() {
		return businessName;
	}

	public Iterator<KeyValuePair<String, FieldIndexDef>> fieldIndexDefItr(){
		return fieldMap.iterator();
		
	}
}
