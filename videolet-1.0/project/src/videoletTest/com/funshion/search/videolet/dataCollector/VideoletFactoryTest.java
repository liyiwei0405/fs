package com.funshion.search.videolet.dataCollector;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.funshion.search.utils.ConfigReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class VideoletFactoryTest {
	static MongoClient mongoClient;
	@AfterClass
	public static void afterClass(){
		if(mongoClient != null){
			mongoClient.close();
		}
	}
	@BeforeClass
	public static void beforeClass() throws IOException{
		ConfigReader cr = VideoletFactory.getConfig(VideoletFactory.MICRO_VIDEO);
		mongoClient = new MongoClient(cr.getValue("ip") , cr.getInt("port") );
		DB db = mongoClient.getDB(cr.getValue("database"));
		DBCollection  col = db.getCollection(cr.getValue("table"));
		BasicDBObject qPubStatus = new BasicDBObject("_id", 11583);
		DBCursor topCursor = col.find(qPubStatus);

		if(topCursor.hasNext()){
			dbobj = topCursor.next();
		}else{
			throw new RuntimeException("can not continue test");
		}
	}
	static DBObject dbobj;
	@Test
	public void testGetConfig() throws IOException {
		ConfigReader cr = VideoletFactory.getConfig(VideoletFactory.MICRO_VIDEO);
		if(cr.getValue("ip", "").length() == 0){
			fail("read config fail");
		}
	}

	
	@Test
	public void testGetRecord() throws IOException {
		VideoletRecord rec = VideoletFactory.getRecord(VideoletFactory.MICRO_VIDEO, dbobj);
		if(rec == null){
			fail("videoleFactory get fail for " + VideoletFactory.MICRO_VIDEO);
		}
	}
	
	@Test(expected=RuntimeException.class)
	public void testGetRecordMisMatch() throws IOException {
		VideoletFactory.getRecord(VideoletFactory.MICRO_VIDEO + "s", dbobj);
		fail("expect got a RuntimeException, but get nothing");
	}

	
}
