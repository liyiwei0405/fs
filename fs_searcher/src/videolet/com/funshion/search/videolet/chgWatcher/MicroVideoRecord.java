package com.funshion.search.videolet.chgWatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.funshion.search.utils.LineReader;
import com.mongodb.DBObject;

public class MicroVideoRecord extends VideoletRecord{
	private static Set<String> microVideoFBDTypes = new HashSet<String>();
	static{
		try {
			LineReader lr = new LineReader("./config/VideoletExport/microVideoFbd.set");
			while(lr.hasNext()){
				String line = lr.next().trim().toLowerCase();
				if(line.length() == 0 || line.startsWith("#")){
					continue;
				}
				microVideoFBDTypes.add(line);
			}
			lr.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("read config/microVideoFbd.set ERROR! system exit");
			System.exit(1);
		}
	}
	public MicroVideoRecord(DBObject dbobj)  {
		super(dbobj);
	}

	@Override
	public int getIsMicroVideoOrUgc() {
		return isMicroVideoType;
	}

	@Override
	public int getVideoId(DBObject dbobj) {
		return getInt(dbobj.get("videoid"), 0);
	}

	@Override
	public String getTitle(DBObject dbobj) {
		return getString(dbobj.get("title"), "");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<String> getTags(DBObject dbobj) {
		List<String>ret = new ArrayList<String>();
		Object o = dbobj.get("tags");
		if(o != null){
			if(o instanceof List){
				List lst = (List) o;
				for(Object ob : lst){
					if(ob == null){
						continue;
					}else{
						ret.add(ob.toString());
					}
				}
			}else{
				ret.add(o.toString());
			}
		}
		return ret;
	}

	@Override
	public long getCreateTime(DBObject dbobj) {
		return getLong(dbobj.get("createdate"), 0);
	}

	@Override
	public long getModifyDate(DBObject dbobj) {
		return getLong(dbobj.get("modifydate"), 0);
	}

	@Override
	public int getPlayNum(DBObject dbobj) {
		return getInt(dbobj.get("playnum"), 0);
	}

	public static boolean isFBDMicroVideoType(String t) {
		return microVideoFBDTypes.contains(t.toLowerCase().trim());
	}


}
