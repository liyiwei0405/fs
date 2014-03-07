package com.funshion.search.videolet.chgWatcher;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBObject;

public class UGCVideoRecord extends VideoletRecord{

	public UGCVideoRecord(DBObject dbobj) {
		super(dbobj);
	}

	@Override
	public int getIsMicroVideoOrUgc() {
		return isUGCType;
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

}
