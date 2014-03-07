package com.funshion.search.videolet.dataCollector;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.funshion.search.utils.ConfigReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MicroVideoRecordTest {
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
			record = (MicroVideoRecord) VideoletFactory.getRecord(VideoletFactory.MICRO_VIDEO, dbobj);
		}else{
			throw new RuntimeException("can not continue test");
		}
	}
	static MicroVideoRecord record;
	static DBObject dbobj;


	@Test
	public void testGetDocBase() {
		assertEquals("docbase for " + VideoletFactory.MICRO_VIDEO + " missmatch!" + record.getDocBase() + "!=" + 1000000000,
				1000000000, record.getDocBase());
	}

	@Ignore("not implement now")
	@Test
	public void testIsFromFX() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIsMicroVideoOrUgc() {
		assertEquals("#getIsMicroVideoOrUgc() return error value " + record.getIsMicroVideoOrUgc(),
				record.getIsMicroVideoOrUgc(), VideoletRecord.isMicroVideoType);
	}

	@Test
	public void testGetVideoId() {
		assertEquals("videoId mismatch", record.videoId, 11583);
	}
	@Ignore("not implement now")
	@Test
	public void testGetPicturePath() {
		fail("Not yet implemented");
	}
	@Ignore("not implement now")
	@Test
	public void testGetTimeLen() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTitle() {
		String title = record.getTitle(dbobj);
		assertFalse("get title fail", title == null || title.length() == 0);
	}

	@Test
	public void testGetTags() {
		String tag = record.getTags(dbobj);
		assertFalse("get tag fail", tag == null || tag.length() == 0);
	}

	@Ignore("not implement now")
	@Test
	public void testGetCreateTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetModifyDate() {
		long modifytime = record.getModifyDate(dbobj);
		assertEquals("get modifytime fail", modifytime, 1337046605);
	}
	@Ignore("not implement now")
	@Test
	public void testGetUserId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayNum() {
		assertFalse("playnum check fail", record.getPlayNum(dbobj) == 0);
	}

	@Test
	public void testMicroVideoRecord() {
		assertEquals("record's docid fail", record.docId , (record.getDocBase() + record.videoId));
	}

}
