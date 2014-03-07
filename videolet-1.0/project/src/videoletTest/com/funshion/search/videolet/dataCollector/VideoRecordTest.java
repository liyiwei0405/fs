package com.funshion.search.videolet.dataCollector;

import static org.junit.Assert.fail;

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
import static org.junit.Assert.*;

public class VideoRecordTest {
	class CovtDouble{
		double id;
		CovtDouble(double id){
			this.id = id;
		}
		public String toString(){
			return "" + id;
		}
	}
	class CovtLong{
		long id;
		CovtLong(long id){
			this.id = id;
		}
		public String toString(){
			return "" + id;
		}
	}

	static MongoClient mongoClient;
	@AfterClass
	public static void closeConn(){
		if(mongoClient != null){
			mongoClient.close();
		}
	}
	@BeforeClass
	public static void prepareObject() throws IOException{
		ConfigReader cr = VideoletFactory.getConfig(VideoletFactory.MICRO_VIDEO);
		mongoClient = new MongoClient(cr.getValue("ip") , cr.getInt("port") );
		DB db = mongoClient.getDB(cr.getValue("database"));
		DBCollection  col = db.getCollection(cr.getValue("table"));
		BasicDBObject qPubStatus = new BasicDBObject("_id", 11583);
		DBCursor topCursor = col.find(qPubStatus);

		if(topCursor.hasNext()){
			obj = topCursor.next();
			record = (MicroVideoRecord) VideoletFactory.getRecord(VideoletFactory.MICRO_VIDEO, obj);
		}else{
			throw new RuntimeException("can not continue test");
		}
	}
	static MicroVideoRecord record;
	static DBObject obj;
	@Ignore("have not find good test method")
	@Test
	public void testWriteToBufferBufferedWriterStringString() {
		fail("Not yet implemented");
	}
	@Ignore("have not find good test method")
	@Test
	public void testWriteToBufferBufferedWriter() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDouble() {
		Object [][]toCheck = new Object[][]{
				new Object[]{"sdfa", 150d, 150d},
				new Object[]{null, 160d, 160d},
				new Object[]{new CovtLong(170), 15d, 170d},
				new Object[]{new CovtLong(-170), 15d, -170d},
				new Object[]{6789d/1257, 1d, 6789/1257d},

		};


		for(int x = 0; x < toCheck.length; x ++){
			Object [] objs = toCheck[x];
			double ret = record.getDouble(objs[0], (Double) objs[1]);
			assertEquals("fail check getDouble at index " + x, ret, objs[2]);
		}
	}

	@Test
	public void testGetInt() {
		Object [][]toCheck = new Object[][]{
				new Object[]{"sdfa", 150, 150},
				new Object[]{null, 160, 160},
				new Object[]{new CovtLong(Integer.MAX_VALUE + 44545L), 15, 15},
				new Object[]{new CovtLong(-170), 15, -170},
				new Object[]{6789d/1257, 1, 1},

		};


		for(int x = 0; x < toCheck.length; x ++){
			Object [] objs = toCheck[x];
			int ret = record.getInt(objs[0], (Integer) objs[1]);
			assertEquals("fail check getInt at index " + x, ret, objs[2]);
		}
	}

	@Test
	public void testGetLong() {
		Object [][]toCheck = new Object[][]{
				new Object[]{"sdfa", 150l, 150l},
				new Object[]{null, 160l, 160l},
				new Object[]{new CovtLong(Integer.MAX_VALUE + 44545L), 15l, Integer.MAX_VALUE + 44545L},
				new Object[]{new CovtLong(-170), 15l, -170l},
				new Object[]{6789d/1257, 1l, 1l},

		};


		for(int x = 0; x < toCheck.length; x ++){
			Object [] objs = toCheck[x];
			long ret = record.getLong(objs[0], (Long) objs[1]);
			assertEquals("fail check getLong at index " + x, ret, objs[2]);
		}
	}

	@Test
	public void testGetString() {
		Object[][] toCheck = new Object[][]{
				new Object[]{512, "p", "512"},
				new Object[]{"51 2 ", "p", "51 2 "},
				new Object[]{null, "p", "p"},	
		};
		for(int x = 0; x < toCheck.length; x ++){
			Object [] objs = toCheck[x];
			String ret = record.getString(objs[0], (String) objs[1]);
			assertEquals("fail check getLong at index " + x, ret, objs[2]);
		}

	}

	@Test
	public void testRewriteTags() {
		String tags = "[ moshou世界 的了 ]";
		tags = VideoletRecord.rewriteTags(tags);
		assertEquals("rewriteTags() fail", "moshou世界 的了", tags);
	}

	@Test
	public void testEncString() {
		String ret = record.encString("48wesf以&><'\"><");
		if(!"48wesf以&amp;&gt;&lt;&apos;&quot;&gt;&lt;".equals(ret)){
			fail("test encString fail!");
		}
	}
	@Ignore("have not find good test method")
	@Test
	public void testIsGoodUtf8() {
		char c[][] = new char[][]{
				new char[]{'嵹', 'y'},	//㳜
				new char[]{(char)(-128), 'y'},
				new char[]{'比', 'y'}
		};
		for(char []cs : c){
			boolean b = VideoletRecord.isGoodUtf8(cs[0]);
			if(b){
				if(cs[1] != 'y'){
					fail("test isGoodUtf8 fail Y: " + ((int)cs[0]) );
				}
			}else{
				if(cs[1] != 'n'){
					fail("test isGoodUtf8 fail N: " + ((int)cs[0]) );
				}
			}
		}
	}

	@Ignore("have not find good test method")
	@Test
	public void testStripNonCharCodepoints() {
		fail("Not yet implemented");
	}



}
