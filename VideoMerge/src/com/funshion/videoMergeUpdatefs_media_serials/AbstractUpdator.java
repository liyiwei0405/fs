package com.funshion.videoMergeUpdatefs_media_serials;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public abstract class AbstractUpdator {
	protected Map<Integer, Integer>videoIds = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer>ugcIds = new HashMap<Integer, Integer>();
	protected Map<Integer, Integer>newsIds = new HashMap<Integer, Integer>();
	
	protected LogHelper log = new LogHelper("log");
	
	protected void loadVideoids(Map<Integer, Integer> idsMap, String cls) throws Exception{
		log.info("loading from mongo ...");
		ConfigReader cr = new ConfigReader(
				new File("./config/videoMerge.conf"), "merge_mongo");
		MongoClient mongoClient = new MongoClient(cr.getValue("ip") , cr.getInt("port") );
		DB db = mongoClient.getDB(cr.getValue("database"));
		DBCollection col = db.getCollection(cr.getValue("table"));
		BasicDBObject fieldsFiltObject =  new BasicDBObject("_id", false);
		String[] fields_token = {
				"videoid", "mapid"
		};
		
		for(String x : fields_token){  
			x = x.trim();
			if(x.length() == 0){
				continue;
			}
			fieldsFiltObject.append(x, true);
		}
		BasicDBObject query = new BasicDBObject("class",  cls);
		
		DBCursor cur = col.find(query, fieldsFiltObject);
		while(cur.hasNext()){
			DBObject obj = cur.next();
			int videoid = (int)Double.parseDouble(obj.get("videoid").toString());
			int mapid = (int)Double.parseDouble(obj.get("mapid").toString());
			Object org = idsMap.put(mapid, videoid);
			if(org != null){
				throw new Exception("ERROR! mapid " + mapid + " already has map to " + org);
			}
		}
		log.warn("has load video ids %s", idsMap.size());
		log.info("loading from mongo done");
	}
	protected abstract void work() throws Exception;
	
	protected abstract void loadSerial() throws SQLException, IOException;
	
	protected abstract void update() throws SQLException, Exception;
	
	public String toString(List<Integer> lst){
		String str = lst.toString();
		return str.substring(0, str.length() - 1 ).substring(1).replace(" ", "");
	}
	
}
