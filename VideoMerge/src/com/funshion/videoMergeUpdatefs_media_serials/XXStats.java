package com.funshion.videoMergeUpdatefs_media_serials;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.funshion.flushVideoSpecial.MongoHelper;
import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class XXStats extends AbstractUpdator{
	private MongoHelper mergeMongoHelper = null;
	XXStats() throws Exception{
		ConfigReader mergeCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "merge_mongo");
		mergeMongoHelper = new MongoHelper(mergeCr.getValue("ip"), mergeCr.getInt("port"), mergeCr.getValue("corsair_video_status"), mergeCr.getValue("fs_video_status"));

	}
	public static enum CType{
		Norm("normal"), Ugc("ugc"), News("news");
		
		final String str;
		CType(String str){
			this.str = str;
		}
	}
	
	private static int incId = 1;
	public class Stats {
		public final int id;
		public final int videoId;
		public final int currentDate;
		public final int num;
		Stats(CType type, int videoId, int currentDate, int num) throws Exception{
			this.id = incId ++;
			this.currentDate = currentDate;
			this.num = num;
			Integer newVideoId;
			if(type == CType.Norm){
				newVideoId = videoIds.get(videoId);
			}else if(type == CType.Ugc){
				newVideoId = ugcIds.get(videoId);
			}else if(type == CType.News){
				newVideoId = newsIds.get(videoId);
			}else{
				throw new RuntimeException("unknown Ctype:" + type);
			}
			if(newVideoId == null){
				throw new Exception("no videoId found for type " + type + ", videoId = " + videoId);
			}
			this.videoId = newVideoId;
		}
		public DBObject toObject() {
			BasicDBObject obj = new BasicDBObject();
			obj.put("_id", this.id);
			obj.put("videoid", this.videoId);
			obj.put("num", this.num);
			obj.put("currentdate", this.currentDate);
			return obj;
		}
	}


	@Override
	protected void work() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void loadSerial() throws SQLException, IOException {}

	@Override
	protected void update() throws Exception {
		this.loadVideoids(this.videoIds, CType.Norm.str);
		this.videoIds.clear();

		
		
		this.loadVideoids(this.ugcIds, CType.Ugc.str);
		this.ugcIds.clear();
		
		
		this.loadVideoids(this.newsIds, CType.News.str);
		this.newsIds.clear();
		
		checkFlush(true);
	}
	
	List<DBObject> toInsert = new ArrayList<DBObject>(1024);
	void add(Stats stats){
		toInsert.add(stats.toObject());
		checkFlush(false);
	}

	int hasIns = 0;
	private void checkFlush(boolean force) {
		if(toInsert.size() >= 1000 || force){
			//FIXME insert
		}
		hasIns += toInsert.size();
		log.info("has insert %s", hasIns);
		toInsert.clear();
	}
}
