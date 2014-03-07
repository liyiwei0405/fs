//package com.funshion.flushVideoSpecial;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import com.funshion.search.ConfUtils;
//import com.funshion.search.utils.ConfigReader;
//import com.funshion.search.utils.LogHelper;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//
//public class FlushVideoSpecial {
//	private static final LogHelper log = new LogHelper("FlushVideoSpecial");
//
//	private ConfigReader mergeCr, videoSpecialCr, ugcSpecialCr, toVideoSpecialCr, toUgcSpecialCr;
//	//装载special中的记录
//	private List<DBObject> videoSpecialList = new LinkedList<DBObject>();
//	private List<DBObject> ugcSpecialList = new LinkedList<DBObject>();
//	//用来映射新老ID
//	private Map<Integer, Integer> videoIdMap = new HashMap<Integer, Integer>();
//	private Map<Integer, Integer> ugcIdMap = new HashMap<Integer, Integer>();
//
//	private MongoHelper mergeMongoHelper = null;
//
//	public FlushVideoSpecial() throws IOException{
//		this.mergeCr = new ConfigReader(ConfUtils.getConfFile("FlushVideoSpecial.conf"), "mergeCr");
//		this.videoSpecialCr = new ConfigReader(ConfUtils.getConfFile("FlushVideoSpecial.conf"), "videoSpecialCr");
//		this.ugcSpecialCr = new ConfigReader(ConfUtils.getConfFile("FlushVideoSpecial.conf"), "ugcSpecialCr");
//		this.toVideoSpecialCr = new ConfigReader(ConfUtils.getConfFile("FlushVideoSpecial.conf"), "toVideoSpecialCr");
//		this.toUgcSpecialCr = new ConfigReader(ConfUtils.getConfFile("FlushVideoSpecial.conf"), "toUgcSpecialCr");
//	}
//
//	public void work() throws Exception{
//		System.out.println("start");
//
//		mergeMongoHelper = new MongoHelper(mergeCr.getValue("ip"), mergeCr.getInt("port"), mergeCr.getValue("database"), mergeCr.getValue("table"));
//
//		MongoHelper videoMongoHelper = new MongoHelper(videoSpecialCr.getValue("ip"), videoSpecialCr.getInt("port"), videoSpecialCr.getValue("database"), videoSpecialCr.getValue("table"));
//		MongoHelper ugcMongoHelper = new MongoHelper(ugcSpecialCr.getValue("ip"), ugcSpecialCr.getInt("port"), ugcSpecialCr.getValue("database"), ugcSpecialCr.getValue("table"));
//		MongoHelper toVideoMongoHelper = new MongoHelper(toVideoSpecialCr.getValue("ip"), toVideoSpecialCr.getInt("port"), toVideoSpecialCr.getValue("database"), toVideoSpecialCr.getValue("table"));
//		MongoHelper toUgcMongoHelper = new MongoHelper(toUgcSpecialCr.getValue("ip"), toUgcSpecialCr.getInt("port"), toUgcSpecialCr.getValue("database"), toUgcSpecialCr.getValue("table"));
//
//		//加载新老ID to Map
//		log.warn("import ids to Map");
//		importIdToMap("normal", this.videoIdMap);
//		importIdToMap("ugc", this.ugcIdMap);
//
//		//加载video、ugc special to List
//		log.warn("import special info to List");
//		importSpecialToList(videoMongoHelper, this.videoSpecialList);
//		log.warn("fs_video_special' s size: " + this.videoSpecialList.size());
//		importSpecialToList(ugcMongoHelper, this.ugcSpecialList);
//		log.warn("fs_video_special_ugc' s size: " + this.ugcSpecialList.size());
//
//		//转换video新老id
//		log.warn("convert ids");
//		convertVideoId();
//		convertUgcId();
//
//		//写入special新表
//		toVideoMongoHelper.col.insert(this.videoSpecialList);
//		toUgcMongoHelper.col.insert(this.ugcSpecialList);
//		log.warn("DONE!!");
//
//		mergeMongoHelper.close();
//		videoMongoHelper.close();
//		ugcMongoHelper.close();
//		toVideoMongoHelper.close();
//		toUgcMongoHelper.close();
//	}
//
//	private void importIdToMap(String cls, Map<Integer, Integer> videoIdMap) {
//		DBCursor cur = mergeMongoHelper.col.find(new BasicDBObject("class", cls), new BasicDBObject("videoid", true).append("mapid", true));
//		while(cur.hasNext()){
//			DBObject dbObject = cur.next();
//			videoIdMap.put((Integer)dbObject.get("mapid"), (Integer)dbObject.get("videoid"));
//		}
//		cur.close();		
//	}
//
//	private void importSpecialToList(MongoHelper videoMongoHelper, List<DBObject> videoSpecialList) {
//		DBCursor cur = videoMongoHelper.col.find(new BasicDBObject(), new BasicDBObject());
//		while(cur.hasNext()){
//			DBObject dbObject = cur.next();
//			videoSpecialList.add(dbObject);
//		}		
//		cur.close();
//	}
//
//	@SuppressWarnings("rawtypes")
//	private void convertVideoId() throws Exception {
//		for(DBObject dbObject : this.videoSpecialList){
//			Object relVideoObjList = dbObject.get("relatedvideo");
//
//			if(relVideoObjList == null){
//				continue;
//			}
//			List<BasicDBObject> newRelVideoObjList = new ArrayList<BasicDBObject>();
//
//			if(relVideoObjList instanceof List){
//				for(Object relVideoObj : (List)relVideoObjList){
//					if(relVideoObj != null){
//						if(relVideoObj instanceof BasicDBObject){
//							BasicDBObject basicObj = (BasicDBObject) relVideoObj;
//
//							Object oVideoid = basicObj.get("videoid");
//							if(oVideoid != null){
//								if(oVideoid instanceof Integer){
//									basicObj.remove("videoid");
//									basicObj.put("videoid", this.videoIdMap.get(oVideoid));
//								}else{
//									throw new Exception("Unknown videoid data type: " + oVideoid.getClass() + "  relVideoObj: " + oVideoid);
//								}
//							}else{
//								log.warn("videoid is null!! sid: " + dbObject.get("sid"));
//							}
//							if(basicObj.get("videoid") != null){
//								newRelVideoObjList.add(basicObj);
//							}
//						}else{
//							throw new Exception("Unknown relVideoObj data type: " + relVideoObj.getClass() + "  relVideoObj: " + relVideoObj);
//						}
//					}else{
//						log.warn("relVideoObj is null!! sid: " + dbObject.get("sid"));
//					}
//				}
//			}else if(relVideoObjList instanceof BasicDBObject){
//				Collection<Object> c = ((BasicDBObject)relVideoObjList).values();
//				for(Object relVideoObj : c){
//					if(relVideoObj != null){
//						if(relVideoObj instanceof BasicDBObject){
//							BasicDBObject basicObj = (BasicDBObject) relVideoObj;
//
//							Object oVideoid = ((BasicDBObject) relVideoObj).get("videoid");
//							if(oVideoid != null){
//								if(oVideoid instanceof Integer){
//									basicObj.remove("videoid");
//									basicObj.put("videoid", this.videoIdMap.get(oVideoid));
//								}else{
//									throw new Exception("Unknown videoid data type: " + oVideoid.getClass() + "  relVideoObj: " + oVideoid);
//								}
//							}else{
//								log.warn("videoid is null!! sid: " + dbObject.get("sid"));
//							}
//							if(basicObj.get("videoid") != null){
//								newRelVideoObjList.add(basicObj);
//							}
//						}else{
//							throw new Exception("Unknown relVideoObj data type: " + relVideoObj.getClass() + "  relVideoObj: " + relVideoObj);
//						}
//					}else{
//						log.warn("relVideoObj is null!! sid: " + dbObject.get("sid"));
//					}
//				}
//			}else{
//				throw new Exception("Unknown relVideoObjList data type: " + relVideoObjList.getClass() + "  relVideoObjList: " + relVideoObjList);
//			}
//			dbObject.removeField("relatedvideo");
//			dbObject.put("relatedvideo", newRelVideoObjList);
//		}		
//	}
//	@SuppressWarnings("rawtypes")
//	private void convertUgcId() throws Exception {
//		for(DBObject dbObject : this.ugcSpecialList){
//			Object relVideoObjList = dbObject.get("relatedvideo");
//
//			if(relVideoObjList == null){
//				continue;
//			}
//			List<Integer> newRelVideoObjList = new ArrayList<Integer>();
//
//			if(relVideoObjList instanceof List){
//				for(Object oVideoid : (List)relVideoObjList){
//					if(oVideoid != null){
//						if(oVideoid instanceof Integer){
//							Integer newId = this.ugcIdMap.get(oVideoid);
//							if(newId != null){
//								newRelVideoObjList.add(newId);
//							}
//						}else{
//							throw new Exception("Unknown videoid data type: " + oVideoid.getClass() + "  videoid: " + oVideoid);
//						}
//					}else{
//						log.warn("videoid is null!! specialid: " + dbObject.get("specialid"));
//					}
//				}
//			}else if(relVideoObjList instanceof BasicDBObject){
//				Collection<Object> c = ((BasicDBObject)relVideoObjList).values();
//				for(Object oVideoid : c){
//					if(oVideoid != null){
//						if(oVideoid instanceof Integer){
//							Integer newId = this.ugcIdMap.get(oVideoid);
//							if(newId != null){
//								newRelVideoObjList.add(newId);
//							}
//						}else{
//							throw new Exception("Unknown videoid data type: " + oVideoid.getClass() + "  videoid: " + oVideoid);
//						}
//					}else{
//						log.warn("videoid is null!! specialid: " + dbObject.get("specialid"));
//					}
//				}
//			}else{
//				throw new Exception("Unknown relVideoObjList data type: " + relVideoObjList.getClass() + "  relVideoObjList: " + relVideoObjList);
//			}
//			dbObject.removeField("relatedvideo");
//			dbObject.put("relatedvideo", newRelVideoObjList);
//		}				
//	}
//
//	public static void main(String[] args) throws Exception {
//		FlushVideoSpecial fm = new FlushVideoSpecial();
//		fm.work();
//	}
//
//}
