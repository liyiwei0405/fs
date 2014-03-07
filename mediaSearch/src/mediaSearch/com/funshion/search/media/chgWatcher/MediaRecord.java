package com.funshion.search.media.chgWatcher;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.funshion.search.FlushableRecord;
import com.funshion.search.utils.LineWriter;

public class MediaRecord extends FlushableRecord{
	private Map<Integer, List<Integer>> mediaToRelatedVideos;
	private Map<Integer, Set<Integer>> mediaToIshds;
	private Map<Integer, Set<Integer>> mediaToPreids;
	private Map<Integer, Set<Integer>> mediaToClassids;
	private Map<Integer, Set<String>> mediaToTactics;
	private Map<Integer, Set<Integer>> mediaToIsdisplays;
	
	private int mediaid;
	private String name_cn;
	private String name_en;
	private String name_ot;
	private String name_sn;
	private int tactics;
	private int ordering;
	private int coverpicid;
	private String issue;
	private int ta_0;
	private int ta_1;
	private int ta_2;
	private int ta_3;
	private int ta_4;
	private int ta_5;
	private int ta_6;
	private int ta_7;
	private int ta_8;
	private int ta_9;
	private int copyright;
	private int playnum;
	private int playafternum;
	private float karma;
	private int votenum;
	private int wantseenum;
	private int program_type;
	private String displaytype;
	private String country;
	private String tag4editor;
	private String releaseinfo;
	private String imagefilepath;
	private String webplay;
	private int releasedate;
	//public  int deleted;
	
	public MediaRecord(){}
	
	public MediaRecord(ResultSet rs, Map<Integer, List<Integer>> mediaToRelatedVideos, Map<Integer, Set<Integer>> mediaToIshds, Map<Integer, Set<Integer>> mediaToPreids, Map<Integer, Set<Integer>> mediaToClassids, Map<Integer, Set<String>> mediaToTactics, Map<Integer, Set<Integer>> mediaToIsdisplays) throws Exception{
		this.mediaToRelatedVideos = mediaToRelatedVideos;
		this.mediaToIshds = mediaToIshds;
		this.mediaToPreids = mediaToPreids;
		this.mediaToClassids = mediaToClassids;
		this.mediaToTactics = mediaToTactics;
		this.mediaToIsdisplays = mediaToIsdisplays;
		
		this.mediaid = this.getInt(rs.getInt("mediaid"), 0);
		this.name_cn = this.getString(rs.getString("name_cn"), "");
		this.name_en = this.getString(rs.getString("name_en"), "");
		this.name_ot = this.getString(rs.getString("name_ot"), "");
		this.name_sn = this.getString(rs.getString("name_sn"), "");
		this.tactics = this.getInt(rs.getInt("tactics"), 0);
		this.ordering = this.getInt(rs.getInt("ordering"), 0);
		this.coverpicid = this.getInt(rs.getInt("coverpicid"), 0);
		this.issue = this.getString(rs.getString("issue"), "");
		this.ta_0 = this.getInt(rs.getInt("ta_0"), 0);
		this.ta_1 = this.getInt(rs.getInt("ta_1"), 0);
		this.ta_2 = this.getInt(rs.getInt("ta_2"), 0);
		this.ta_3 = this.getInt(rs.getInt("ta_3"), 0);
		this.ta_4 = this.getInt(rs.getInt("ta_4"), 0);
		this.ta_5 = this.getInt(rs.getInt("ta_5"), 0);
		this.ta_6 = this.getInt(rs.getInt("ta_6"), 0);
		this.ta_7 = this.getInt(rs.getInt("ta_7"), 0);
		this.ta_8 = this.getInt(rs.getInt("ta_8"), 0);
		this.ta_9 = this.getInt(rs.getInt("ta_9"), 0);
		this.copyright = this.getInt(rs.getInt("copyright"), 0);
		this.playnum = this.getInt(rs.getInt("playnum"), 0);
		this.playafternum = this.getInt(rs.getInt("playafternum"), 0);
		this.karma = rs.getFloat("karma");
		this.votenum = this.getInt(rs.getInt("votenum"), 0);
		this.wantseenum = this.getInt(rs.getInt("wantseenum"), 0);
		this.program_type = this.getInt(rs.getInt("program_type"), 0);
		this.displaytype = this.getString(rs.getString("displaytype"), "");
		this.country = this.getString(rs.getString("country"), "");
		this.tag4editor = this.getString(rs.getString("tag4editor"), "");
		this.releaseinfo = this.getString(rs.getString("releaseinfo"), "");
		this.imagefilepath = this.getString(rs.getString("imagefilepath"), "");
		this.webplay = this.getString(rs.getString("webplay"), "");
		this.releasedate = this.getInt(rs.getInt("releasedate"), 0);
		//this.deleted = rs.getInt("deleted");
	}

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
	
	public List<String> splitToList(String input, String seperator){
		String arr[] = input.split(seperator);
		return Arrays.asList(arr);
	}
	
	@Override
	public void flushTo(LineWriter lw) throws IOException{
		lw.writeLine(RECORD_START_FLAG);
		
		writeItem(lw, FieldDefine.FIELD_NAME_UNIC_ID, this.mediaid);
		writeItem(lw, FieldDefine.FIELD_NAME_NAME_CN, splitToList(this.name_cn, " / "));
		writeItem(lw, FieldDefine.FIELD_NAME_NAME_EN, splitToList(this.name_en, " / "));
		writeItem(lw, FieldDefine.FIELD_NAME_NAME_OT, splitToList(this.name_ot, " / "));
		writeItem(lw, FieldDefine.FIELD_NAME_NAME_SN, splitToList(this.name_sn, " / "));
		writeItem(lw, FieldDefine.FIELD_NAME_TATICS, this.tactics);
		writeItem(lw, FieldDefine.FIELD_NAME_ISPLAY, this.mediaToIsdisplays.get(this.mediaid));
		writeItem(lw, FieldDefine.FIELD_NAME_ORDERING, this.ordering);
		writeItem(lw, FieldDefine.FIELD_NAME_COVER_PIC_ID, this.coverpicid);
		writeItem(lw, FieldDefine.FIELD_NAME_ISSUE, this.issue);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_0, this.ta_0);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_1, this.ta_1);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_2, this.ta_2);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_3, this.ta_3);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_4, this.ta_4);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_5, this.ta_5);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_6, this.ta_6);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_7, this.ta_7);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_8, this.ta_8);
		writeItem(lw, FieldDefine.FIELD_NAME_TA_9, this.ta_9);
		writeItem(lw, FieldDefine.FIELD_NAME_COPYRIGHT, this.copyright);
		writeItem(lw, FieldDefine.FIELD_NAME_PLAY_NUM, this.playnum);
		writeItem(lw, FieldDefine.FIELD_NAME_PLAY_AFTER_NUM, this.playafternum);
		writeItem(lw, FieldDefine.FIELD_NAME_KARMA, this.karma);
		writeItem(lw, FieldDefine.FIELD_NAME_VOTENUM, this.votenum);
		writeItem(lw, FieldDefine.FIELD_NAME_WANT_SEE_NUM, this.wantseenum);
		writeItem(lw, FieldDefine.FIELD_NAME_PROGRAM_TYPE, this.program_type);
		writeItem(lw, FieldDefine.FIELD_NAME_DISPLAY_TYPE, this.displaytype);
		writeItem(lw, FieldDefine.FIELD_NAME_COUNTRY, splitToList(this.country, " / "));
		writeItem(lw, FieldDefine.FIELD_NAME_TAG_4_EDITOR, splitToList(this.tag4editor, " "));
		writeItem(lw, FieldDefine.FIELD_NAME_RELEASE_INFO, splitToList(this.releaseinfo, " / "));
		writeItem(lw, FieldDefine.FIELD_NAME_IMAGE_FILE_PATH, this.imagefilepath);
		writeItem(lw, FieldDefine.FIELD_NAME_WEB_PLAY, this.webplay);
		writeItem(lw, FieldDefine.FIELD_NAME_RELEASE_DATE, this.releasedate);
		//writeItem(lw, FieldDefine.FIELD_NAME_DELETED, this.deleted);
		writeItem(lw, FieldDefine.FIELD_NAME_IS_HD, this.mediaToIshds.get(this.mediaid));
		writeItem(lw, FieldDefine.FIELD_NAME_RELATED_VIDEOLET, this.mediaToRelatedVideos.get(this.mediaid));
		writeItem(lw, FieldDefine.FIELD_NAME_RELATED_PREIDS, this.mediaToPreids.get(this.mediaid));
		writeItem(lw, FieldDefine.FIELD_NAME_MEDIA_CLASSID, this.mediaToClassids.get(this.mediaid));
		writeItem(lw, FieldDefine.FIELD_NAME_AREA_TACTIC, this.mediaToTactics.get(this.mediaid));
		
		lw.writeLine(RECORD_END_FLAG);
	}
}
