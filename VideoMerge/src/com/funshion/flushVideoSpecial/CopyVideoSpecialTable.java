//package com.funshion.flushVideoSpecial;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.funshion.search.ConfUtils;
//import com.funshion.search.utils.ConfigReader;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//
//public class CopyVideoSpecialTable {
//	//private static final LogHelper log = new LogHelper("FlushVideoSpecial");
//	private ConfigReader videoSpecialCr, ugcSpecialCr;
//	
//	private Map<Integer, DBObject> videoSpecialMap = new HashMap<Integer, DBObject>();
//	
//	
//	public CopyVideoSpecialTable() throws IOException{
//		this.videoSpecialCr = new ConfigReader(ConfUtils.getConfFile("FlushVideoSpecial.conf"), "videoSpecialCr");
//		this.ugcSpecialCr = new ConfigReader(ConfUtils.getConfFile("FlushVideoSpecial.conf"), "ugcSpecialCr");
//	}
//	
//	public void work() throws Exception{
//		System.out.println("start");
//		
//		MongoHelper videoMongoHelper = null;
//		MongoHelper copy_videoMongoHelper = null;
//		try{
//			videoMongoHelper = new MongoHelper(videoSpecialCr.getValue("ip"), videoSpecialCr.getInt("port"), videoSpecialCr.getValue("database"), videoSpecialCr.getValue("table"));
//		
//			DBCursor cur = videoMongoHelper.col.find(new BasicDBObject(), new BasicDBObject());
//			while(cur.hasNext()){
//				DBObject dbObject = cur.next();
//				try{
//					Integer sid = Integer.parseInt(dbObject.get("sid").toString());
//					this.videoSpecialMap.put(sid, dbObject);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//			System.out.println("import video done");
//			
//			Collection<DBObject> dbObjects = this.videoSpecialMap.values();
//			List<DBObject> dbObjList = new ArrayList<DBObject>(dbObjects.size());
//			dbObjList.addAll(dbObjects);
//			
//			copy_videoMongoHelper = new MongoHelper("localhost", 27017, "corsair_video", "fs_video_special");
//			copy_videoMongoHelper.col.insert(dbObjList);
//			//			videoMongoHelper.col.find(new BasicDBObject("_id", 11), new BasicDBObject("relatedvideo", true)).limit(1);
//			System.out.println("insert video done");
//		}finally{
//			if(videoMongoHelper != null){
//				videoMongoHelper.close();
//			}
//			if(copy_videoMongoHelper != null){
//				copy_videoMongoHelper.close();
//			}
//		}
//		
//		this.videoSpecialMap.clear();
//		
//		MongoHelper ugcMongoHelper = null;
//		MongoHelper copy_ugcMongoHelper = null;
//		try{
//			ugcMongoHelper = new MongoHelper(ugcSpecialCr.getValue("ip"), ugcSpecialCr.getInt("port"), ugcSpecialCr.getValue("database"), ugcSpecialCr.getValue("table"));
//		
//			DBCursor cur = ugcMongoHelper.col.find(new BasicDBObject(), new BasicDBObject());
//			while(cur.hasNext()){
//				DBObject dbObject = cur.next();
//				this.videoSpecialMap.put(Integer.parseInt(dbObject.get("specialid").toString()), dbObject);
//			}
//			System.out.println("import ugc done");
//			
//			Collection<DBObject> dbObjects = this.videoSpecialMap.values();
//			List<DBObject> dbObjList = new ArrayList<DBObject>(dbObjects.size());
//			dbObjList.addAll(dbObjects);
//			
//			copy_ugcMongoHelper = new MongoHelper("localhost", 27017, "corsair_ugc", "fs_video_special_ugc");
//			copy_ugcMongoHelper.col.insert(dbObjList);
//			//			videoMongoHelper.col.find(new BasicDBObject("_id", 11), new BasicDBObject("relatedvideo", true)).limit(1);
//			System.out.println("insert ugc done");
//		}finally{
//			if(ugcMongoHelper != null){
//				ugcMongoHelper.close();
//			}
//			if(copy_ugcMongoHelper != null){
//				copy_ugcMongoHelper.close();
//			}
//		}
//	}
//	
//	public static void main(String[] args) throws Exception {
//		CopyVideoSpecialTable fm = new CopyVideoSpecialTable();
//		
//		fm.work();
//	}
//
//}
