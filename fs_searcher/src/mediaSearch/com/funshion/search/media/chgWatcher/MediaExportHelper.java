package com.funshion.search.media.chgWatcher;

import java.io.File;
import java.io.IOException;
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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * @author liying
 */
public class MediaExportHelper extends ExportChgHelper{
	public static final List<String> relateVideoTypes = new ArrayList<String>();
	public Map<Integer, List<Integer>> mediaToRelatedVideos;
	static{
		relateVideoTypes.add("m_yugao");
		relateVideoTypes.add("m_jingbian");
		relateVideoTypes.add("m_kandian");
		relateVideoTypes.add("m_zixun");
		relateVideoTypes.add("m_yuanchuang");
		relateVideoTypes.add("m_teji");
		relateVideoTypes.add("m_zongyi");
	}

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

	public void doExport(boolean totalExport, DatumFile dFile) throws Exception{
		int tot = 0;
		File tmpFile = fs.prepareTmpFile();
		LineWriter lw = newLineWriter(tmpFile);

		MongoExportHelper mongoExp = new MongoExportHelper();
		try{
			DBObject queryObject = new BasicDBObject("publishflag", "published");
			DBObject fieldObject = new BasicDBObject("videoid", true).append("types", true).append("video_media_ids", true).append("_id", false);


			DBCursor cur = mongoExp.query(queryObject, fieldObject);
			this.mediaToRelatedVideos = genRelatedVideoMap(cur);

			MysqlExportHelper mysqlExp = new MysqlExportHelper(lw);

			long st = System.currentTimeMillis();

			mysqlExp.export(this.mediaToRelatedVideos);

			tot += mysqlExp.getExportNum();
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
			mongoExp.destroy();
		}
	}

	@SuppressWarnings("rawtypes")
	private Map<Integer, List<Integer>> genRelatedVideoMap(DBCursor cur){
		log.info("generating map..");
		Map<Integer, List<Integer>> mediaToRelatedVideos = new HashMap<Integer, List<Integer>>();
		while(cur.hasNext()){
			DBObject dbObject = cur.next();

			boolean containRelatedType = false;
			Object oTypes = dbObject.get("types");
			if(oTypes != null && oTypes instanceof List){
				List types = (List) oTypes;
				for(Object oType : types){
					if(oType != null && oType instanceof String){
						String type = (String) oType;
						if(MediaExportHelper.relateVideoTypes.contains(type)){
							containRelatedType = true;
							break;
						}
					}
				}
			}
			if(containRelatedType){
				Object oVideoId = dbObject.get("videoid");
				if(oVideoId instanceof Number){
					Integer videoId = (Integer)oVideoId;
					Object oVideoMediaIds = dbObject.get("video_media_ids");
					if(oVideoMediaIds != null){
						if(oVideoMediaIds instanceof List){//相关media列表是一个list
							List videoMediaIds = (List) oVideoMediaIds;
							for(Object oMediaId : videoMediaIds){
								if(oMediaId != null && oMediaId instanceof Number){
									Integer mediaId = (Integer)oMediaId;
									List<Integer> videoIds;
									if(!mediaToRelatedVideos.containsKey(mediaId)){
										videoIds = new ArrayList<Integer>();
										mediaToRelatedVideos.put(mediaId, videoIds);
									}else{
										videoIds = mediaToRelatedVideos.get(mediaId);
									}
									videoIds.add(videoId);
								}
								else{
									log.error("mediaid not a number: %s", String.valueOf(oMediaId));						
								}
							}
						}else if(oVideoMediaIds instanceof Integer){//相关media列表是一个integer
							Integer mediaId = (Integer)oVideoMediaIds;
							List<Integer> videoIds;
							if(!mediaToRelatedVideos.containsKey(mediaId)){
								videoIds = new ArrayList<Integer>();
								mediaToRelatedVideos.put(mediaId, videoIds);
							}else{
								videoIds = mediaToRelatedVideos.get(mediaId);
							}
							videoIds.add(videoId);
						}
						else{//相关media列表是其他
							log.error("related media not null and not list or number, videoid: %s, data type: %s", String.valueOf(oVideoId), oVideoMediaIds.getClass());
						}
					}
				}else{
					log.error("videoid not a number: %s", String.valueOf(oVideoId));
				}
			}
		}
		return mediaToRelatedVideos;
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
