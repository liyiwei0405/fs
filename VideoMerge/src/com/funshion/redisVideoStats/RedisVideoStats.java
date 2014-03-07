package com.funshion.redisVideoStats;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

import com.funshion.flushVideoSpecial.MongoHelper;
import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class RedisVideoStats {
	private static final LogHelper log = new LogHelper("RedisVideoStats");
	private static final String s_video_stats_pfx = "corsair:hash:video_event_num:";
	
	private Jedis jedis;
	private ConfigReader jedisCr, mergeCr;
	
	private Map<Integer, Integer> videoIdMap = new HashMap<Integer, Integer>();
	private MongoHelper mergeMongoHelper;
	
	
	public RedisVideoStats() throws Exception{
		this.jedisCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "redis_stats");
		this.jedis = new Jedis(jedisCr.getValue("ip"), jedisCr.getInt("port"), jedisCr.getInt("timeout"));
		log.warn("jedis: %s, %s, timeout: %s", jedisCr.getValue("ip"), jedisCr.getInt("port"), jedisCr.getInt("timeout"));
		
		this.mergeCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "merge_mongo");
	}
	
	private void work() throws Exception {
		log.warn("importId To Map");
		importIdToMap();
		
		log.warn("setNewRedisStats");
		setNewRedisStats();
		
		log.warn("done!");
	}
	
	private void importIdToMap() throws Exception {
		mergeMongoHelper = new MongoHelper(mergeCr);

		DBCursor cur = mergeMongoHelper.col.find(new BasicDBObject("class", "normal"), new BasicDBObject("videoid", true).append("mapid", true));
		while(cur.hasNext()){
			DBObject dbObject = cur.next();
			videoIdMap.put((Integer)dbObject.get("mapid"), (Integer)dbObject.get("videoid"));
		}
		cur.close();	
		mergeMongoHelper.close();
	}
	
	private void setNewRedisStats() {
		int count = 0;
		jedis.connect();

		for(Entry<Integer, Integer> entry : this.videoIdMap.entrySet()){
			Integer mapid = entry.getKey();
			Integer videoid = entry.getValue();
			
			Map<String, String> statsVal = jedis.hgetAll(s_video_stats_pfx + mapid);
			if(statsVal != null && !statsVal.isEmpty()){
				String newKey = s_video_stats_pfx + videoid;
				for(Entry<String, String> hEntry : statsVal.entrySet()){
					jedis.hset(newKey, hEntry.getKey(), hEntry.getValue());
				}
			}
			count ++;
			if(count % 1000 == 0){
				log.info("load count %s", count);
			}
		}
		log.info("total " + count);

		jedis.disconnect();			
	}

	public static void main(String[] args) throws Exception {
		RedisVideoStats rvs = new RedisVideoStats();
		rvs.work();
	}
}
