package com.funshion.search.media.chgWatcher;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author liying
 *
 */

public class MongoExportHelper {
	private static MongoClient mongoClient;
	private static DB db;
	public MongoExportHelper() throws Exception{
		mongoClient = new MongoClient("192.168.16.161" , 27017 );
		db = mongoClient.getDB("corsair_video");
		
	}
	public static DBCursor query(DBObject queryObject, DBObject fieldObject){
		DBCollection  col = db.getCollection("fs_video");
		DBCursor cur = col.find(queryObject, fieldObject).sort(new BasicDBObject("modifydate", -1));
		return cur;
	}
	
}

