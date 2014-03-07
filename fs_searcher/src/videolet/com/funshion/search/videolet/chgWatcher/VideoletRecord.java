package com.funshion.search.videolet.chgWatcher;

import java.io.IOException;
import java.util.List;

import com.funshion.search.FlushableRecord;
import com.funshion.search.utils.LineWriter;
import com.mongodb.DBObject;

public abstract class VideoletRecord extends FlushableRecord{
	public static final String FIELD_VIDEO_OR_UGC = "a";
	public static final String FIELD_VIDEO_ID = "b";
	public static final String FIELD_IS_DEL = "x";
	public static final String FIELD_TITLE = "c";
	public static final String FIELD_TAGS = "d";
	public static final String FIELD_PLAY_NUM = "e";
	public static final String FIELD_CREATE_DATE = "f";
	public static final String FIELD_MODIFY_DATE = "g";

	public static final int isMicroVideoType = 1;
	public static final int isUGCType = 2;

	public final int videoOrUgc;
	public final int videoId;
	public final String title;
	public final List<String> tags;
	public final long createdate;
	public final long modifydate;
	public final int playNum;
	public boolean isDel;//FIXME this should be checked at index time, delete special segment
	public VideoletRecord( DBObject dbobj){
		this.isDel = !"published".equalsIgnoreCase(this.getString(dbobj.get("publishflag"), ""));
		this.videoOrUgc = this.getIsMicroVideoOrUgc();
		this.videoId = this.getVideoId(dbobj);

		this.title = this.getTitle(dbobj);
		this.tags = this.getTags(dbobj);

		this.createdate = this.getCreateTime(dbobj);
		this.modifydate = this.getModifyDate(dbobj);

		this.playNum = this.getPlayNum(dbobj);
	}

	public abstract int getIsMicroVideoOrUgc();
	public abstract int getVideoId(DBObject dbobj);
	public abstract String getTitle(DBObject dbobj) ;
	public abstract List<String> getTags(DBObject dbobj) ;
	public abstract long getCreateTime(DBObject dbobj) ;
	public abstract long getModifyDate(DBObject dbobj);
	public abstract int getPlayNum(DBObject dbobj) ;

	public int getInt(Object o, int dft){
		long ret = getLong(o, dft);
		return ret > Integer.MAX_VALUE ? dft :(int) ret;
	}
	public long getLong(Object o, long dft){
		if(o == null)
			return dft;

		String str = getString(o, "");
		if(str.length() == 0){
			return dft;
		}
		try{
			return Long.parseLong(str);
		}catch(Exception e){
			log.error("can not covert to long for '" + str + "'");
		}
		return dft;
	}

	protected String getString(Object object, String dft) {
		if(object == null){
			return dft;
		}
		String ret;
		if(object instanceof String){
			ret = (String) object;
		}else{
			ret = object.toString();
		}
		return ret.replace('\t', ' ');
	}

	public String encString(List<String> input) {
		StringBuilder sb = new StringBuilder();
		for(String x : input){
			x = encString(x);
			if(sb.length() > 0){
				sb.append('\t');
			}
			sb.append(x);
		}
		return sb.toString();
	}
	public String encString(String input) {
		if(input == null){
			return "";
		}
		return input.replace('\t', ' ');
	}

	public void flushTo(LineWriter lw) throws IOException{
		lw.writeLine(RECORD_START_FLAG);
		writeItem(lw, FIELD_VIDEO_OR_UGC, this.videoOrUgc);
		writeItem(lw, FIELD_VIDEO_ID, this.videoId);
		writeItem(lw, FIELD_IS_DEL, this.isDel ? "1" : "0");
		writeItem(lw, FIELD_TITLE, this.title);
		writeItem(lw, FIELD_TAGS, this.tags);
		writeItem(lw, FIELD_PLAY_NUM, this.playNum);
		writeItem(lw, FIELD_CREATE_DATE, this.createdate);
		writeItem(lw, FIELD_MODIFY_DATE, this.modifydate);
		lw.writeLine(RECORD_END_FLAG);
	}
}
