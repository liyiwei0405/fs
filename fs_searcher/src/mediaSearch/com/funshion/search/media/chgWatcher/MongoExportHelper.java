package com.funshion.search.media.chgWatcher;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
/**
 * @author liyw
 *
 */
public class MongoExportHelper {
	private ConfigReader cr;
	private MongoClient mongoClient;
	private DB db;
	
	public MongoExportHelper() throws Exception{
		this.cr = new ConfigReader(MediaFactory.mongoClientConfigFile, "fs_video");
		this.mongoClient = new MongoClient(cr.getValue("ip") , cr.getInt("port") );
		this.db = mongoClient.getDB(cr.getValue("database"));
		LogHelper.log.log("connect to mongo");
	}
	
	public DBCursor query(DBObject queryObject, DBObject fieldObject) throws Exception{
		DBCollection  col = db.getCollection(cr.getValue("table"));
		DBCursor cur = col.find(queryObject, fieldObject).sort(new BasicDBObject("modifydate", -1));
		return cur;
	}
	
	public void destroy() throws Exception{
		this.mongoClient.close();
	}
}

