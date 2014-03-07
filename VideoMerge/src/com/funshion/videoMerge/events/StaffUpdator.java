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

public class StaffUpdator {
	MongoHelper helper, mapid;
	StaffUpdator() throws IOException, Exception{
		helper = new MongoHelper(
				new ConfigReader(
						new File("./config/videoMerge.conf"), "fs_staff_mongo"));	

		mapid = new MongoHelper(
				new ConfigReader(
						new File("./config/videoMerge.conf"), "merge_mongo"));
	}

	void doUpdate() throws Exception{
		update();
	}
	@SuppressWarnings("rawtypes")
	public void update() throws Exception{
		LogHelper log = new LogHelper("staff");
		log.log("normal load%s", "...");
		Map<Integer, Integer>map = new HashMap<Integer, Integer>();

		DBCursor curMap =mapid.col.find(new BasicDBObject("class",  "normal"),
				new BasicDBObject("videoid",  true).append("mapid", true));
		while(curMap.hasNext()){
			DBObject dbo = curMap.next();
			map.put((int)Double.parseDouble(dbo.get("mapid").toString()), (int)Double.parseDouble(dbo.get("videoid").toString()));
		}
		log.log("load normal ok, total %s", map.size());
		//		BasicDBObject fieldsFiltObject =  
		//				new BasicDBObject("eventid", true).append(field, true);

		log.log("loading staffid<--> staff_video_ids");
		BasicDBObject queryFileds = new BasicDBObject("staffid",  true).append("staff_video_ids", true).append("_id", false);

		DBCursor cur = helper.col.find(new BasicDBObject(), queryFileds);
		int totalRewrite = 0;
		LineWriter lw = new LineWriter(new File("staff" + new SimpleDateFormat(".yyyy-MM-dd.HH.mm.ss.SSS").format(System.currentTimeMillis())), false);
		while(cur.hasNext()){
			final DBObject dbo = cur.next();
			Object staffStrId = dbo.get("staffid");
			if(staffStrId == null){
				System.out.println("Error record:" + dbo);
				continue;
			}
			int staffid = (int) Double.parseDouble(staffStrId.toString());
			List l = (List) dbo.get("staff_video_ids");
			if(l == null){
				log.error("missing staff_video_ids for staffid %s", staffid);
				continue;
			}
			if(l.size() == 0){
//				log.error("skip staff_video_ids for staffid %s", staffid);
				continue;
			}
			List<Integer>newlst = new ArrayList<Integer>();
			lw.writeLine(
					String.format("%s=%s", staffid, l));
			lw.flush();
			for(Object x : l){
				if(x == null){
					continue;
				}
				Integer vid = (int) Double.parseDouble(x.toString());

				Integer newid = map.get(vid);
				if(newid == null){
					log.error("lost mapid %s of %s", vid, staffid);
					continue;
				}
				newlst.add(newid);
			}

			totalRewrite ++;
//			System.out.print(dbo + " --> ");
			dbo.put("staff_video_ids", newlst);
			dbo.put("staffid", staffid);
//			System.out.println(dbo);
			if(true){
				helper.col.update(
						new BasicDBObject("staffid", staffid), new BasicDBObject("$set", dbo));
			}
			Thread.sleep(10);
		}
		lw.close();
		log.log("rewriting staff OK! totalRewrite %s", totalRewrite);

	}
	public static void main(String[]args) throws IOException, Exception{
		StaffUpdator upd = new StaffUpdator();
		upd.doUpdate();
	}
}
