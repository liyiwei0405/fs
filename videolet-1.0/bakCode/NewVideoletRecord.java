package com.funshion.search.videolet.dataCollector;

import com.mongodb.DBObject;

public class NewVideoletRecord extends VideoletRecord{

	public NewVideoletRecord(DBObject dbobj) {
		super(dbobj);
	}

	@Override
	public int getVideoId(DBObject dbobj) {
		return getInt(dbobj.get("videoid"), 0);
	}

	@Override
	public String getTitle(DBObject dbobj) {
		return getString(dbobj.get("title"), "");
	}

	@Override
	public String getTags(DBObject dbobj) {
		return rewriteTags(getString(dbobj.get("tags"), ""));
	}

	@Override
	public long getModifyDate(DBObject dbobj) {
		return getLong(dbobj.get("modifydate"), 0);
	}

	@Override
	public int getPlayNum(DBObject dbobj) {
		return getInt(dbobj.get("playnum"), 0);
	}

	@Override
	public int getDocBase() {
		return 2000000000;
	}

}
