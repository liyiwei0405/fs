package com.funshion.videoMerge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

import com.funshion.flushVideoSpecial.MongoHelper;
import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * @author liyiwei
 */
public class VideoMergeHelper{
	int countBase;
	int insertNumOnce;
	//全量导入
	private static final LogHelper log = new LogHelper("VideoMergeHelper");
	private static final String RDB_VIDEOID = "corsair:string:videoid_new";

	private MongoHelper mergeMongoHelper = null;

	private final ConfigReader mergeCr, microCr, ugcCr, newsCr;

	public static void main(String[] args) throws Exception {
		VideoMergeHelper h = new VideoMergeHelper();
		h.doExport();
		h.setRedis();
	}

	private void setRedis() throws IOException {
		ConfigReader jedisCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "redis_count");
		Jedis jedis = new Jedis(jedisCr.getValue("ip"), jedisCr.getInt("port"), jedisCr.getInt("timeout"));
		log.warn("jedis: %s, %s, timeout: %s", jedisCr.getValue("ip"), jedisCr.getInt("port"), jedisCr.getInt("timeout"));

		jedis.connect();
		jedis.set(RDB_VIDEOID,  "" + 2000001);
		jedis.disconnect();
		log.warn("redis update done!");

	}
	final int sleepInMs;
	VideoMergeHelper() throws IOException{
		this.mergeCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "merge_mongo");
		this.countBase = mergeCr.getInt("countBase");
		this.sleepInMs = mergeCr.getInt("sleepInMs", 100);
		this.insertNumOnce = mergeCr.getInt("insertNumOnce");
		this.microCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "micro_mongo");
		this.ugcCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "ugc_mongo");
		this.newsCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "news_mongo");
	}

	public void doExport() throws Exception{
		mergeMongoHelper = new MongoHelper(mergeCr.getValue("ip"), mergeCr.getInt("port"), mergeCr.getValue("database"), mergeCr.getValue("table"));

		log.warn("now total export");

		log.warn("micro: start");
		MongoHelper microMongoHelper = null;
		try{
			microMongoHelper = new MongoHelper(microCr.getValue("ip"), microCr.getInt("port"), microCr.getValue("database"), microCr.getValue("table"));
			importAndExport(microMongoHelper, "normal");
		}finally{
			if(microMongoHelper != null){
				microMongoHelper.close();
			}
		}

		log.warn("ugc: start");
		MongoHelper ugcMongoHelper = null;
		try{
			ugcMongoHelper = new MongoHelper(ugcCr.getValue("ip"), ugcCr.getInt("port"), ugcCr.getValue("database"), ugcCr.getValue("table"));
			importAndExport(ugcMongoHelper, "ugc");
		}finally{
			if(ugcMongoHelper != null){
				ugcMongoHelper.close();
			}
		}

		log.warn("news: start");
		MongoHelper newsMongoHelper = null;	
		try{
			newsMongoHelper = new MongoHelper(newsCr.getValue("ip"), newsCr.getInt("port"), newsCr.getValue("database"), newsCr.getValue("table"));
			importAndExport(newsMongoHelper, "news");
		}finally{
			if(newsMongoHelper != null){
				newsMongoHelper.close();
			}
		}
		log.warn("All done! countBase: " + this.countBase +"\n");
	}

	/**	导入+导出
	 * @param mongoHelper	
	 * @param nowIs				当前导入的小视频类型
	 * @param lastModifydate	若全量更新，为-1；若增量更新为上次更新的最大时间戳
	 * @throws Exception 
	 */
	private void importAndExport(MongoHelper mongoHelper, String nowIs) throws Exception{

		DBCursor cur = null;
		//全量
		cur = mongoHelper.col.find();
		log.warn(nowIs + ": total import old to map");
		Map<Integer, DBObject> videoMap = totalOldToMap(cur, nowIs);
		cur.close();
		log.warn(nowIs + ": total export map to new");
		totalMapToNew(videoMap.values());

		videoMap.clear();
	}


	private Map<Integer, DBObject> totalOldToMap(DBCursor cur, String nowIs) {
		Map<Integer, DBObject> videoMap = new HashMap<Integer, DBObject>();
		int count = 0;
		while(cur.hasNext()){
			DBObject dbObject = cur.next();
			Object oMapid = dbObject.get("videoid");
			if(oMapid == null){
				log.error("error! null mapId");
				continue;
			}
			dbObject.removeField("_id");
			dbObject.put("mapid", oMapid);
			dbObject.put("class", nowIs);
			dbObject.put("videoid",  ++ countBase);
			dbObject.put("_id", countBase);
			if(nowIs.equals("normal")){
				dbObject.put("pic_title_modifydate", dbObject.get("modifydate"));
			}
			if(nowIs.equals("ugc")){
				dbObject.put("recommend", dbObject.get("rec"));
				dbObject.removeField("rec");
			}

			Integer mapid = (int)Double.parseDouble(oMapid.toString());
			videoMap.put(mapid, dbObject);
			count ++;
			if(count % 1000 == 0){
				log.info("load count %s", count);
				//				break;
			}

		}
		log.info("TOTOAL count %s", count);
		return videoMap;
	}
	//全量导出
	private void totalMapToNew(Collection<DBObject> dbObjects) throws Exception {
		log.info("exporting...");
		List<DBObject> dbObjList = new ArrayList<DBObject>(dbObjects.size());
		dbObjList.addAll(dbObjects);
		log.info("sorting");
		Collections.sort(dbObjList, new Comparator<DBObject>(){

			@Override
			public int compare(DBObject o1, DBObject o2) {
				
				return (Integer)o1.get("videoid") - (Integer)o2.get("videoid");
			}
			
		});
		log.info("sort!");
//sort
		for(int x = 0; x < dbObjList.size(); ){
			int end = x + insertNumOnce;//1000 parameters
			if(end > dbObjList.size()){
				end = dbObjList.size();
			}
			List<DBObject> dbObjListNew = new ArrayList<DBObject>(end - x);
			dbObjListNew.addAll(dbObjList.subList(x, end));
			log.info("merging from %s to %s", x, end);
			mergeMongoHelper.col.insert(dbObjListNew);
			x = end;
			Thread.sleep(sleepInMs);
		}
		log.info("export DONE!");
	}

}
