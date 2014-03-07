package com.funshion.videoService.test;

import com.funshion.videoService.MongoHelper;
import com.mongodb.BasicDBObject;

public class ModifyDB {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MongoHelper mongo = new MongoHelper("192.167.3.95", 27077, "corsair_video", "fs_video");
//		int n = mongo.col.update(new BasicDBObject("videoid", 2003077), 
//				new BasicDBObject("$set", new BasicDBObject("playnum", 28).append("modifydate", System.currentTimeMillis()/1000))).getN();

//		int n = mongo.col.insert(new BasicDBObject("videoid",3333333).append("modifydate", System.currentTimeMillis()/1000)).getN();
		
		int n = mongo.col.update(new BasicDBObject("videoid", 3333333), new BasicDBObject("$set", new BasicDBObject("publishflag", "topublish").append("modifydate", System.currentTimeMillis()/1000))).getN();
		System.out.println(n);
		
	}
}