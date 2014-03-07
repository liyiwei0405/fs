package com.funshion.retrieve.media.chgWatcher;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.funshion.search.DatumFile;
import com.funshion.search.ChgExportFS;
import com.funshion.search.ExportChgHelper;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;

/**
 * @author liying
 */
public class MediaExportHelper extends ExportChgHelper{
	private int exportNum = 0;
	private LineWriter writeTo;
	
	private Map<Integer, Set<String>> mediaToCategory = new HashMap<Integer, Set<String>>();
	private Map<Integer, Set<String>> mediaToTag = new HashMap<Integer, Set<String>>();
	private Map<Integer, Set<Integer>> mediaToCountry = new HashMap<Integer, Set<Integer>>();
	private Map<Integer, Set<Integer>> mediaToRegion = new HashMap<Integer, Set<Integer>>();
	private Map<Integer, Set<Integer>> mediaToClassId = null;
	private Map<Integer, Set<String>> mediaToTactics = null;
	
	final int rotateItvSeconds;
	final int rotateAtHour;
	private int totalExpDayOfyear = -1;

	MediaExportHelper(ConfigReader cr, ChgExportFS fs) throws IOException{
		super(cr, fs);
		this.rotateItvSeconds = cr.getInt("rotateItvSeconds", 600);

		//rotateAtHour, if valid value is set, the indexes will be total rotate at this hour, 
		//if valid value is set, rotateItvSeconds will be overrided
		rotateAtHour = cr.getInt("rotateAtHour", -1);
		if(rotateAtHour > -1){
			log.warn("rotate at hour %s, and rotateItvSeconds disabled", this.rotateAtHour);
		}
	}

	@Override
	public void doExport(boolean totalExport, DatumFile dFile) throws Exception{
		int tot = 0;
		File tmpFile = fs.prepareTmpFile();
		LineWriter lw = newLineWriter(tmpFile);
		this.writeTo = lw;
		try{
			long st = System.currentTimeMillis();
			export();

			tot += this.exportNum;
			long ed = System.currentTimeMillis();	
			log.info("export Media use ms %s", (ed - st));

			lw.close();
			if(tot > 0){
				if(!tmpFile.renameTo(dFile.file)){
					log.error("can not rename tmp file to %s", dFile.file);
					throw new Exception("we can not rename tmpFile '"+ tmpFile.getCanonicalPath() + "'  to '" + dFile.file.getCanonicalFile() + "'");
				}
				dFile.md5Bytes();
				log.info("new chg gen ok: %s", dFile);
				if(dFile.isMain){
					fs.switchChgDir(dFile);
				}else{
					fs.putDatumFile(dFile);
				}
			}else{
				tmpFile.delete();
				log.info("export 0 records! for %s", (totalExport ? "totalExport" : "updateExport"));
			}
		}finally{
			if(lw != null){
				lw.close();
			}
		}
	}

	public void export() throws Exception{
		MysqlHelper mysqlHelper = new MysqlHelper();
		try {
			String sql_multi_publish = "SELECT mediaid, category, tag, country, region FROM fs_publish";
			ResultSet rs_multi_publish = mysqlHelper.getCursor(sql_multi_publish);
			genMapFromPublish(rs_multi_publish);
			
			String sql_media_class_relation = "SELECT mediaid, media_classid FROM fs_media_class_relation";
			ResultSet rs_media_class_relation = mysqlHelper.getCursor(sql_media_class_relation);
			this.mediaToClassId = genMap(rs_media_class_relation, "media_classid");
			
			String sql_media_area_tactic_relation = "SELECT mediaid, tactic, client FROM fs_media_area_tactic_relation where tactic in (SELECT tactic FROM fs_area_info WHERE tacticvalid>0 and isvalid>0 UNION SELECT 0 FROM (SELECT count(tactic) as num FROM fs_area_info WHERE tacticvalid=0 AND isvalid>0) v WHERE v.num > 0)";
			ResultSet rs_media_area_tactic_relation = mysqlHelper.getCursor(sql_media_area_tactic_relation);
			this.mediaToTactics = genAreaTacticMap(rs_media_area_tactic_relation);

			String sql_publish = "SELECT DISTINCT mediaid, name_cn, mtype, mediaid as mid, modifydate, wantseenum, votenum, karma, iscutpic, ishd, imagefilepath, fsp_status, fsp_lang_status, fsp_original_status, fsp_info, adword, vote_adword, isrank, period, releasedate, playnum, ordering, updateflag, ishot, isclassic, isblack, copyright, zone_1, zone_2, zone_3, zone_4, zone_5, zone_6, zone_7, zone_8, zone_9, z1hour, z2hour, z3hour, z4hour, z5hour, z6hour, z7hour, z8hour, z9hour, z1week, z2week, z3week, z4week, z5week, z6week, z7week, z8week, z9week, peernum, nation, year, issue, ta_0, ta_1, ta_2, ta_3, ta_4, ta_5, ta_6, ta_7, ta_8, ta_9, daynum, weeknum, playafternum, coverpicid, program_type FROM fs_publish";
			ResultSet rs_publish = mysqlHelper.getCursor(sql_publish);

			collect(rs_publish);
		} catch(Exception e) {
			log.error("mysql error: %s", e.getMessage());
			e.printStackTrace();
		} finally{
			mysqlHelper.close();
		}
	}
	
	private void genMapFromPublish(ResultSet rs) throws Exception{
		while(rs.next()){
			Integer mediaid = rs.getInt("mediaid");
			
			Set<String> categorySet = mediaToCategory.get(mediaid);
			if(categorySet == null){
				categorySet = new HashSet<String>();
				mediaToCategory.put(mediaid, categorySet);
			}
			categorySet.add(rs.getString("category"));
			
			Set<String> tagSet = mediaToTag.get(mediaid);
			if(tagSet == null){
				tagSet = new HashSet<String>();
				mediaToTag.put(mediaid, tagSet);
			}
			tagSet.add(rs.getString("tag"));
			
			Set<Integer> countrySet = mediaToCountry.get(mediaid);
			if(countrySet == null){
				countrySet = new HashSet<Integer>();
				mediaToCountry.put(mediaid, countrySet);
			}
			countrySet.add(rs.getInt("country"));
			
			Set<Integer> regionSet = mediaToRegion.get(mediaid);
			if(regionSet == null){
				regionSet = new HashSet<Integer>();
				mediaToRegion.put(mediaid, regionSet);
			}
			regionSet.add(rs.getInt("region"));
		}
		rs.close();
	}
	
	private Map<Integer, Set<Integer>> genMap(ResultSet rs, String valueName) throws Exception{
		Map<Integer, Set<Integer>> mediaMap = new HashMap<Integer, Set<Integer>>();
		while(rs.next()){
			Integer mediaid = rs.getInt("mediaid");
			Set<Integer> valueSet = mediaMap.get(mediaid);
			if(valueSet == null){
				valueSet = new HashSet<Integer>();
				mediaMap.put(mediaid, valueSet);
			}
			valueSet.add(rs.getInt(valueName));
		}
		rs.close();
		return mediaMap;
	}
	
	private Map<Integer, Set<String>> genAreaTacticMap(ResultSet rs) throws Exception{
		Map<Integer, Set<String>> mediaMap = new HashMap<Integer, Set<String>>();
		while(rs.next()){
			Integer mediaid = rs.getInt("mediaid");
			int tactic = rs.getInt("tactic");
			String client = rs.getString("client");
			String mix = client + String.valueOf(tactic);
			Set<String> valueSet = mediaMap.get(mediaid);
			if(valueSet == null){
				valueSet = new HashSet<String>();
				mediaMap.put(mediaid, valueSet);
			}
			valueSet.add(mix);
		}
		rs.close();
		return mediaMap;
	}
	
	private void collect(ResultSet rs) throws Exception{
		while(rs.next()){
			MediaRecord record = null;
			Integer mediaid = rs.getInt("mediaid");
			try {
				record = new MediaRecord();
				record.flush(writeTo, rs, this.mediaToCategory.get(mediaid), this.mediaToTag.get(mediaid), this.mediaToCountry.get(mediaid), this.mediaToRegion.get(mediaid), this.mediaToClassId.get(mediaid), this.mediaToTactics.get(mediaid));
				writeTo.append('\n');
				this.exportNum++;
			} catch (Exception e) {
				log.warn("skip record! becouse " + e);
				e.printStackTrace();
				continue;
			}
		}
		rs.close();
	}
	
	@Override
	protected boolean needTotalExport() {
		if(lastTotalExportTime == 0){//do total-index at startup
			return true;
		}else{
			if(rotateAtHour > -1){//at this hour to all-export
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int day = c.get(Calendar.DAY_OF_YEAR);
				if(hour == this.rotateAtHour && (totalExpDayOfyear != day)){
					totalExpDayOfyear = day;
					return true;
				}
			}else{//set not daily total-rotate, then check rotate inteval
				long hasPassedSeconds = (System.currentTimeMillis()  - lastTotalExportTime) / 1000;
				if(hasPassedSeconds > this.rotateItvSeconds){
					return true;
				}
			}
			return false;
		}
	}
	@Override
	protected boolean needUpdate() {
		return false;
	}

}
