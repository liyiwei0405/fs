package com.funshion.retrieve.media.chgWatcher;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.funshion.search.FlushableRecord;
import com.funshion.search.utils.LineWriter;

public class MediaRecord extends FlushableRecord{
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

	public String convertTA_x(ResultSet rs) throws SQLException{
		boolean att[] = new boolean[]{
				rs.getInt("ta_0") == 0,
						rs.getInt("ta_1") == 0,
						rs.getInt("ta_2") == 0,
						rs.getInt("ta_3") == 0,
						rs.getInt("ta_4") == 0,
						rs.getInt("ta_5") == 0,
						rs.getInt("ta_6") == 0,
						rs.getInt("ta_7") == 0,
						rs.getInt("ta_8") == 0,
						rs.getInt("ta_9") == 0,
		};
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < att.length; x ++){
			if(att[x]){
				if(sb.length() > 0){
					sb.append('\t');
				}
				sb.append(x);
			}
		}
		return sb.toString();
	}

	public void flush(LineWriter lw, ResultSet rs, Set<String> categorySet, Set<String> tagSet, Set<Integer> countrySet, Set<Integer> regionSet, Set<Integer> classIdSet, Set<String> tacticSet ) throws IOException, SQLException{
		lw.writeLine(RECORD_START_FLAG);

		writeItem(lw, FieldDefine.FIELD_NAME_UNIC_ID, rs.getInt("mediaid"));
		writeItem(lw, FieldDefine.FIELD_NAME_MTYPE, rs.getString("mtype"));
		writeItem(lw, FieldDefine.FIELD_NAME_MODIFYDATE, rs.getInt("modifydate"));
		writeItem(lw, FieldDefine.FIELD_NAME_WANTSEENUM, rs.getInt("wantseenum"));
		writeItem(lw, FieldDefine.FIELD_NAME_VOTENUM, rs.getInt("votenum"));
		writeItem(lw, FieldDefine.FIELD_NAME_KARMA, rs.getFloat("karma"));
		writeItem(lw, FieldDefine.FIELD_NAME_ISCUTPIC, rs.getInt("iscutpic"));
		writeItem(lw, FieldDefine.FIELD_NAME_ISHD, rs.getInt("ishd"));
		writeItem(lw, FieldDefine.FIELD_NAME_FSP_STATUS, rs.getString("fsp_status"));
		writeItem(lw, FieldDefine.FIELD_NAME_FSP_LANG_STATUS, rs.getString("fsp_lang_status"));
		writeItem(lw, FieldDefine.FIELD_NAME_FSP_ORIGINAL_STATUS, rs.getString("fsp_original_status"));
		writeItem(lw, FieldDefine.FIELD_NAME_FSP_INFO, rs.getString("fsp_info"));
		writeItem(lw, FieldDefine.FIELD_NAME_ADWORD, rs.getString("adword"));
		writeItem(lw, FieldDefine.FIELD_NAME_ISRANK, rs.getInt("isrank"));
		writeItem(lw, FieldDefine.FIELD_NAME_PERIOD, rs.getInt("period"));
		writeItem(lw, FieldDefine.FIELD_NAME_RELEASEDATE, rs.getInt("releasedate"));
		writeItem(lw, FieldDefine.FIELD_NAME_PLAYNUM, rs.getInt("playnum"));
		writeItem(lw, FieldDefine.FIELD_NAME_ORDERING, rs.getInt("ordering"));
		writeItem(lw, FieldDefine.FIELD_NAME_UPDATEFLAG, rs.getInt("updateflag"));
		writeItem(lw, FieldDefine.FIELD_NAME_ISHOT, rs.getInt("ishot"));
		writeItem(lw, FieldDefine.FIELD_NAME_ISCLASSIC, rs.getInt("isclassic"));
		writeItem(lw, FieldDefine.FIELD_NAME_ISBLACK, rs.getInt("isblack"));
		writeItem(lw, FieldDefine.FIELD_NAME_COPYRIGHT, rs.getInt("copyright"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_1, rs.getInt("zone_1"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_2, rs.getInt("zone_2"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_3, rs.getInt("zone_3"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_4, rs.getInt("zone_4"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_5, rs.getInt("zone_5"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_6, rs.getInt("zone_6"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_7, rs.getInt("zone_7"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_8, rs.getInt("zone_8"));
		writeItem(lw, FieldDefine.FIELD_NAME_ZONE_9, rs.getInt("zone_9"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z1HOUR, rs.getInt("z1hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z2HOUR, rs.getInt("z2hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z3HOUR, rs.getInt("z3hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z4HOUR, rs.getInt("z4hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z5HOUR, rs.getInt("z5hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z6HOUR, rs.getInt("z6hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z7HOUR, rs.getInt("z7hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z8HOUR, rs.getInt("z8hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z9HOUR, rs.getInt("z9hour"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z1WEEK, rs.getInt("z1week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z2WEEK, rs.getInt("z2week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z3WEEK, rs.getInt("z3week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z4WEEK, rs.getInt("z4week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z5WEEK, rs.getInt("z5week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z6WEEK, rs.getInt("z6week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z7WEEK, rs.getInt("z7week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z8WEEK, rs.getInt("z8week"));
		writeItem(lw, FieldDefine.FIELD_NAME_Z9WEEK, rs.getInt("z9week"));
		writeItem(lw, FieldDefine.FIELD_NAME_PEERNUM, rs.getInt("peernum"));
		writeItem(lw, FieldDefine.FIELD_NAME_NATION, rs.getString("nation"));
		writeItem(lw, FieldDefine.FIELD_NAME_YEAR, rs.getInt("year"));
		writeItem(lw, FieldDefine.FIELD_NAME_ISSUE, rs.getString("issue"));
		writeItem(lw, FieldDefine.FIELD_NAME_TA, convertTA_x(rs));
		writeItem(lw, FieldDefine.FIELD_NAME_DAYNUM, rs.getInt("daynum"));
		writeItem(lw, FieldDefine.FIELD_NAME_WEEKNUM, rs.getInt("weeknum"));
		writeItem(lw, FieldDefine.FIELD_NAME_PLAYAFTERNUM, rs.getInt("playafternum"));
		writeItem(lw, FieldDefine.FIELD_NAME_PROGRAM_TYPE, rs.getInt("program_type"));
		writeItem(lw, FieldDefine.FIELD_NAME_CATEGORY, categorySet);
		writeItem(lw, FieldDefine.FIELD_NAME_TAG, tagSet);
		writeItem(lw, FieldDefine.FIELD_NAME_COUNTRY, countrySet);
		writeItem(lw, FieldDefine.FIELD_NAME_REGION, regionSet);
		writeItem(lw, FieldDefine.FIELD_NAME_MEDIA_CLASSID, classIdSet);
		writeItem(lw, FieldDefine.FIELD_NAME_TACTIC, tacticSet);

		lw.writeLine(RECORD_END_FLAG);
	}

	@Override
	public void flushTo(LineWriter lw){

	}
}
