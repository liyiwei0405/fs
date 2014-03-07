//package com.funshion.search.videolet.dataCollector;
//
//import com.mongodb.DBObject;
//
//public class MicroVideoRecord extends VideoletRecord{
//
//	public MicroVideoRecord(DBObject dbobj)  {
//		super(dbobj);
//	}
//
//	@Override
//	public int isFromFX(DBObject dbobj) {
//		String str = getString(dbobj.get("source"), "");
//		if("fs".equalsIgnoreCase(str)){
//			return isFromFs;
//		}
//		return isNotFromFs;
//	}
//
//	@Override
//	public int getIsMicroVideoOrUgc() {
//		return isMicroVideoType;
//	}
//
//	@Override
//	public int getVideoId(DBObject dbobj) {
//		return getInt(dbobj.get("videoid"), 0);
//	}
//
//	@Override
//	public String getPicturePath(DBObject dbobj) {
//		return getString(dbobj.get("picturepath"), "");
//	}
//
//	@Override
//	public String getTimeLen(DBObject dbobj) {
//		return "";
//	}
//
//	@Override
//	public String getTitle(DBObject dbobj) {
//		return getString(dbobj.get("title"), "");
//	}
//
//	@Override
//	public String getTags(DBObject dbobj) {
//		return rewriteTags(getString(dbobj.get("tags"), ""));
//	}
//
//	@Override
//	public long getCreateTime(DBObject dbobj) {
//		return getLong(dbobj.get("createdate"), 0);
//	}
//
//	@Override
//	public long getModifyDate(DBObject dbobj) {
//		return getLong(dbobj.get("modifydate"), 0);
//	}
//
//	@Override
//	public int getUserId(DBObject dbobj) {
//		return 0;
//	}
//
//	@Override
//	public int getPlayNum(DBObject dbobj) {
//		return getInt(dbobj.get("playnum"), 0);
//	}
//
//
//	@Override
//	public int getDocBase() {
//		return  1000000000;
//	}
//
//}
