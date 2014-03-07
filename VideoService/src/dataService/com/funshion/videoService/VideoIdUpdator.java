//package com.funshion.videoService;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.funshion.search.ConfUtils;
//import com.funshion.search.IndexableRecord;
//import com.funshion.search.utils.ConfigReader;
//import com.funshion.search.utils.LogHelper;
//import com.funshion.videoService.thrift.VideoletInfo;
//import com.mongodb.BasicDBList;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//import com.rabbitmq.client.QueueingConsumer.Delivery;
//
//public class VideoIdUpdator extends UpdateMessageQueue{
//	LogHelper log = new LogHelper("videoIdMsgQ");
//	MongoHelper helper;
//	final ConfigReader mongoConfig;
//	public VideoIdUpdator() throws IOException{
//		super(new ConfigReader(
//				ConfUtils.getConfFile("videoService.conf"), 
//				"VideoIdMessageQueue"));
//		mongoConfig = new ConfigReader(cr.configFile,  cr.getValue("mongoConfig", "mongo"));
//	}
//
//	public void update(List<IndexableRecord>updates) throws Exception{
//		List<Integer>videoIds = new ArrayList<Integer>();
//		while(true){
//			try{
//				Delivery dev = queue.poll();
//				if(dev == null){
//					break;
//				}
//				byte [] bs = dev.getBody();
//				int intValue = Integer.parseInt(new String(bs));
//				videoIds.add(intValue);
//			}catch(Exception e){
//				log.error("unrecognizable message!");
//			}
//		}
//
//		if(videoIds.size() == 0){
//			return;
//		}
//		//select from mongo
//		if(helper == null){
//			helper = new MongoHelper(mongoConfig);
//		}
//		DBCursor cur;
//		DBObject query = query(videoIds);
//		try{
//			cur = helper.col.find(query);
//		}catch(Exception e){
//			helper.close();
//			helper = new MongoHelper(mongoConfig);
//			cur = helper.col.find(query);
//		}
//		while(cur.hasNext()){
//			DBObject dbo = cur.next();
//			VideoletInfo newInfo = null;
//			try {
//				newInfo = VideoletInfoLoader.loadDBObject(dbo);
//				VideoletIndexableRecord rec = ServiceDatasController.getInstance().updateVideolet(newInfo);
//				if(rec != null){
//					updates.add(rec);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.error("when update %s", newInfo == null ? "unknown videolet" : newInfo.videoId + "");
//			}
//		}
//	}
//	private DBObject query(List<Integer>videoIds){
//		BasicDBList dblist = new BasicDBList();
//		dblist.addAll(videoIds);
//		DBObject queryObject = new BasicDBObject("videoid", new BasicDBObject("$in", dblist));
//		return queryObject;
//	}
//}
