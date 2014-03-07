package com.funshion.videoMerge.events;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.flushVideoSpecial.MongoHelper;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.rds.RDS;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Fs_news_rank_Updator {
	MongoHelper helper, mapid;
	RDS load, update;
	Fs_news_rank_Updator() throws IOException, Exception{	

		mapid = new MongoHelper(
				new ConfigReader(
						new File("./config/videoMerge.conf"), "merge_mongo"));
	}

	void doUpdate() throws Exception{
		update();
	}
	public void update() throws Exception{
		LogHelper log = new LogHelper("staff");
		log.log("ugc load%s", "...");
		Map<Integer, Integer>map = new HashMap<Integer, Integer>();

		DBCursor curMap =mapid.col.find(new BasicDBObject("class",  "news"),
				new BasicDBObject("videoid",  true).append("mapid", true));
		while(curMap.hasNext()){
			DBObject dbo = curMap.next();
			map.put((int)Double.parseDouble(dbo.get("mapid").toString()), (int)Double.parseDouble(dbo.get("videoid").toString()));
		}
		log.log("load ugc ok, total %s", map.size());
		//		BasicDBObject fieldsFiltObject =  
		//				new BasicDBObject("eventid", true).append(field, true);

		log.log("loading mysql videoid");

		load = RDS.getRDSByDefine("corsair_0", "select id, videoid from fs_news_rank");
		ResultSet rs = load.load();
		int totalRewrite = 0;
		LineWriter lw = new LineWriter(new File("fs_news_rank" + new SimpleDateFormat(".yyyy-MM-dd.HH.mm.ss.SSS").format(System.currentTimeMillis())), false);
		update = RDS.getRDSByDefine("corsair_0", "update fs_news_rank set videoid=? where id = ?");
		while(rs.next()){
			int id = rs.getInt(1);
			int videoid = rs.getInt(2);
			lw.writeLine(
					String.format("%s=%s", id, videoid));
			lw.flush();

			Integer newid = map.get(videoid);
			if(newid == null){
				log.error("lost mapid %s for id = %s", videoid, id);
				newid = 0;
			}else{
				log.warn("update %s-->%s for id %s", videoid, newid, id);
			}
			update.setInt(1, newid);
			update.setInt(2, id);
			System.out.println(update);

			update.execute();
			Thread.sleep(10);
		}
		load.close();
		lw.close();
		log.log("rewriting staff OK! totalRewrite %s", totalRewrite);

	}
	public static void main(String[]args) throws IOException, Exception{
		Fs_news_rank_Updator upd = new Fs_news_rank_Updator();
		upd.doUpdate();
	}
}
