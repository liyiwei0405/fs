package com.funshion.videoMerge.events;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.flushVideoSpecial.MongoHelper;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class EventUpdator {
	MongoHelper helper, mapid;
	EventUpdator() throws IOException, Exception{
		helper = new MongoHelper(
				new ConfigReader(
						new File("./config/videoMerge.conf"), "fs_event_mongo"));	

		mapid = new MongoHelper(
				new ConfigReader(
						new File("./config/videoMerge.conf"), "merge_mongo"));
	}

	void doUpdate() throws Exception{
		update("video_comment", "follow_video_ids", "normal");
		update("uvideo_comment", "follow_uvideo_ids", "ugc");
		update("news_comment", "follow_news_ids", "news");
	}
	@SuppressWarnings("rawtypes")
	public void update(String type, String field, String className) throws Exception{
		LogHelper log = new LogHelper(className);
		log.log("loading %s", className);
		Map<Integer, Integer>map = new HashMap<Integer, Integer>();

		DBCursor curMap =mapid.col.find(new BasicDBObject("class",  className),
				new BasicDBObject("videoid",  true).append("mapid", true));
		while(curMap.hasNext()){
			DBObject dbo = curMap.next();
			map.put((int)Double.parseDouble(dbo.get("mapid").toString()), (int)Double.parseDouble(dbo.get("videoid").toString()));
		}
		log.log("load %s ok, total %s", className, map.size());
		//		BasicDBObject fieldsFiltObject =  
		//				new BasicDBObject("eventid", true).append(field, true);

		log.log("rewriting comment %s", className);
		BasicDBObject query = new BasicDBObject("type",  type);

		DBCursor cur = helper.col.find(query);
		int totalRewrite = 0;
		LineWriter lw = new LineWriter(new File(type + new SimpleDateFormat(".yyyy-MM-dd.HH.mm.ss.SSS").format(System.currentTimeMillis())), false);
		while(cur.hasNext()){
			final DBObject dbo = cur.next();
			int eventid = (int) Double.parseDouble(dbo.get("eventid").toString());
			List l = (List) dbo.get(field);
			if(l == null || l.size() == 0){
				log.error("missing %s's %s for eventid %s", type, field, eventid);
				continue;
			}
			List<Integer>newlst = new ArrayList<Integer>();
			lw.writeLine(
					String.format("%s=%s", eventid, l));
			lw.flush();
			for(Object x : l){
				if(x == null){
					continue;
				}
				Integer vid = (int) Double.parseDouble(x.toString());
				if(vid >= 1000000){
					newlst.add(vid);
				}else{
					Integer newid = map.get(vid);
					if(newid == null){
						log.error("lost mapid %s of %s", vid, className);
						continue;
					}
					newlst.add(newid);
				}
			}
			totalRewrite ++;
//			System.out.println(dbo);
			dbo.put("follow_video_ids", newlst);
			dbo.put("type", "video_comment");
			dbo.put("oldtype", type);
			dbo.removeField("_id");
//			System.out.println(dbo);

			helper.col.update(
					new BasicDBObject("eventid", eventid), new BasicDBObject("$set", dbo));
			
			Thread.sleep(10);
		}
		lw.close();
		log.log("rewriting comment %s ok, total %s", className, totalRewrite);

	}
	public static void main(String[]args) throws IOException, Exception{
		EventUpdator upd = new EventUpdator();
		upd.doUpdate();
	}
}
