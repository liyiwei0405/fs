package com.funshion.videoService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.funshion.luc.defines.ITableDefine;
import com.funshion.luc.defines.IndexActionDetector.ActionType;
import com.funshion.search.IndexableRecord;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.KeyValuePair;
import com.funshion.search.utils.LogHelper;
import com.funshion.videoService.thrift.VideoletInfo;

public class VideoletIndexableRecord extends IndexableRecord{
	static Class<?> clazz = VideoletInfo.class;
	static LogHelper log = new LogHelper("indexableRec");
	private static final Map<String, FiledInfo>fieldsMap = new HashMap<String, FiledInfo>();
	static void putToMap(String fieldName, FiledInfo fi){
		fieldsMap.put(fieldName.toUpperCase(), fi);
	}
	static void init() throws Exception{
		//titleLike 
		putToMap("titleLike", new FiledInfo("title"));
		
		//others
		ConfigReader cr = new ConfigReader(ITableDefine.instance.cfgFile, "fieldAlias");
		ArrayList<KeyValuePair<String, String>> lst = cr.getAllConfig();
		for(KeyValuePair<String, String> pair : lst){
			String key = pair.key.toUpperCase();
			if(fieldsMap.containsKey(key)){
				continue;
			}
			Field f = clazz.getField(pair.key);
			if(f != null){
				putToMap(pair.key, new FiledInfo(pair.key));
			}else{
				throw new RuntimeException("NOT DEFINED RELATION for index-field :" + pair.key);
			}
		}
	}
	static{
		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException("cat not init VideoletIndexableRecord", e);
		}
	}
	private static class FiledInfo{
		public final String videoletFieldName;
		public FiledInfo(String videoletFieldName) {
			this.videoletFieldName = videoletFieldName;
		}
		public Object value(VideoletInfo videoletInfo) throws Exception{
			return clazz.getField(videoletFieldName).get(videoletInfo);
		}

		public boolean equalValue(VideoletInfo v1, VideoletInfo v2) throws Exception{
			Object o = value(v1);
			if(o instanceof List){
				return listEquals((List<?>)o, (List<?>)value(v2));
			}else{
				return value(v1).equals(value(v2));
			}
		}
		boolean listEquals(List<?> l1, List<?> l2){
			if(l1.size() == l2.size()){
				for(int x = 0; x < l1.size(); x ++){
					if(!l1.get(x).equals(l2.get(x))){
						return false;
					}
				}
				return true;
			}else{
				return false;
			}
		}
	}

	final VideoletInfo videoInfo;
	public final ActionType actionType;
	VideoletIndexableRecord(VideoletInfo videoletInfo, ActionType actionType) throws Exception{
		this.videoInfo = videoletInfo;
		this.actionType = actionType;
	}
	public String valueOf(String key){
		FiledInfo fi = fieldsMap.get(key);

		try {
			return stringValue(fi.value(videoInfo));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static String fmtItem(Object value){
		if(value == null){
			return "";
		}
		String var = value.toString();
		return var.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
	}
	private static String stringValue(Object value){
		if(value != null){
			if(value instanceof Collection){
				StringBuilder sb = new StringBuilder();
				Collection<?> lst = (Collection<?>) value;
				for(Object o : lst){
					if(sb.length() > 0){
						sb.append('\t');
					}
					String itm = fmtItem(o);
					sb.append(itm);
				}
				return sb.toString();
			}else{
				return fmtItem(value);
			}
		}else{
			return "";
		}
	}

	public static boolean isEqualAsIndexableRecord(VideoletInfo old, VideoletInfo newOne) throws Exception{
		Iterator<FiledInfo> fields = fieldsMap.values().iterator();
		while(fields.hasNext()){
			FiledInfo fi = fields.next();
			Object oldValue = fi.value(old);
			Object newValue = fi.value(newOne);
			if(!fi.equalValue(old, newOne)){
				if(log.logger.isDebugEnabled()){
					log.debug("NEED_INDEX for videoid %s, becouse field %s version different[%s --> %s]", newOne.videoId, fi.videoletFieldName, oldValue, newValue);
				}
				return false;
			}
		}
		return true;
	}
	@Override
	public ActionType getActionType() {
		return this.actionType;
	}
	public static void main(String[] args) {
		System.out.println(fieldsMap);
	}
}