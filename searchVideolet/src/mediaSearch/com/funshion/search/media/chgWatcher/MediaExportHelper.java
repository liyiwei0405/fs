package com.funshion.search.media.chgWatcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.funshion.search.DatumFile;
import com.funshion.search.ChgExportFS;
import com.funshion.search.ExportChgHelper;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * @author liying
 */
public class MediaExportHelper extends ExportChgHelper{
	
	final int rotateItvSeconds;

	final int rotateAtHour;
	private int totalExpDayOfyear = -1;

	MediaExportHelper(ConfigReader cr, ChgExportFS fs) throws IOException{
		super(cr, fs);
		this.rotateItvSeconds = cr.getInt("rotateItvSeconds", 3600);

		//rotateAtHour, if valid value is set, the indexes will be total rotate at this hour, 
		//if valid value is set, rotateItvSeconds will be overrided
		rotateAtHour = cr.getInt("rotateAtHour", -1);
		if(rotateAtHour > -1){
			log.warn("rotate at hour %s, and rotateItvSeconds disabled", this.rotateAtHour);
		}

		//log.warn("modules load info: loadUGC = %s, loadMicroVideo=%s", loadUGC, loadMicroVideo);
	}
	
	public void doExport(boolean totalExport, DatumFile dFile) throws Exception{
		int tot = 0;
		File tmpFile = fs.prepareTmpFile();

		LineWriter lw = newLineWriter(tmpFile);
		try{
			DBObject queryObject = new BasicDBObject();
			DBObject fieldObject = new BasicDBObject("videoid", true).append("modifydate", true).append("video_media_ids", true).append("_id", false);
			
			DBCursor cur = MongoExportHelper.query(queryObject, fieldObject);
			Map<Integer, List<Integer>> mediaToVideolistMap = genRelatedVideoMap(cur);
			
			MysqlExportHelper vExp = new MysqlExportHelper(lw);
			
			long st = System.currentTimeMillis();
			
			vExp.export(mediaToVideolistMap);
			
			tot += vExp.getExportNum();
			long ed = System.currentTimeMillis();	
			log.info("export Video use ms %s", (ed - st));
			
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

	private Map<Integer, List<Integer>> genRelatedVideoMap(DBCursor cur){
		Map<Integer, List<Integer>> mediaToVideolistMap = new HashMap<Integer, List<Integer>>();
		while(cur.hasNext()){
			DBObject dbObject = cur.next();

			Object oVideoId = dbObject.get("videoid");
			if(oVideoId instanceof Number){
				Integer videoId = (Integer)oVideoId;
				Object oVideoMediaIds = dbObject.get("video_media_ids");
				if(oVideoMediaIds != null || oVideoMediaIds instanceof List){
					List<Object> videoMediaIds = (List<Object>) oVideoMediaIds;
					for(Object oMediaId : videoMediaIds){
						if(oMediaId != null || oMediaId instanceof Number){
							Integer mediaId = (Integer)oMediaId;
							if(!mediaToVideolistMap.containsKey(mediaId)){
								List<Integer> videoIds = new ArrayList<Integer>();
								videoIds.add(videoId);
								mediaToVideolistMap.put(mediaId, videoIds);
							}else{
								List<Integer> videoIds = mediaToVideolistMap.get(mediaId);
								videoIds.add(videoId);
								mediaToVideolistMap.remove(mediaId);
								mediaToVideolistMap.put(mediaId, videoIds);
							}
						}
					}
				}
			}
		}
		return mediaToVideolistMap;
	}
	
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
	protected boolean needUpdate() {
		return false;
	}

}
