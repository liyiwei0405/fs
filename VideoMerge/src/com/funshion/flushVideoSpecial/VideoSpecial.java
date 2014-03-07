package com.funshion.flushVideoSpecial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class VideoSpecial extends XXSpecial{

	public VideoSpecial() throws IOException {
		super("fs_video_special", "normal", "sid");
	}

	@SuppressWarnings("rawtypes")
	@Override
	List<DBObject> loadAndRewriteSpecial() {

		DBCursor cur = fromMongoHelper.col.find();
		System.out.println(fromMongoHelper);
		int count = 0;
		log.info("exporting...");
		List<DBObject> dbObjList = new ArrayList<DBObject>();
		while(cur.hasNext()){
			DBObject obj = cur.next();
			List lst = (List) obj.get("relatedvideo");
			
			if(obj.get("sid") == null){
				log.error("missing sid for %s", obj);
				continue;
			}
			Map<Integer, Integer> videoIdMap = null;
			
			String type = "";
			if(String.valueOf(obj.get("type")).equals("news")){
				videoIdMap = this.newsIdMap;
				type = "news";
			}else{
				videoIdMap = this.videoIdMap;
				type = "normal";
			}
			List<BasicDBObject>newList = new ArrayList<BasicDBObject>();
			if(lst != null){
				for(Object x : lst){
					if(x == null)
						continue;
					BasicDBObject rel = (BasicDBObject) x;
					String aid = rel.getString("videoid");
					if(aid == null || aid.isEmpty()){
						continue;
					}
					int vx = (int)Double.parseDouble(aid);
					Integer newId = videoIdMap.get(vx);
					if(newId == null){
						log.warn("not found %s record %s in %s", type, vx, obj.get("sid"));
						continue;
					}
					rel.put("videoid", (int)newId);
					newList.add(rel);
				}
				obj.put("relatedvideo", newList);
			}else{
				log.error("missing relatedvideo for %s", obj);
			}
			count ++;
			if(count % 1000 == 0){
				log.info("has load %s", count);
			}
			dbObjList.add(obj);
		}
		return dbObjList;
	}
	public static void main(String[] args) throws Exception {
		VideoSpecial spc = new VideoSpecial();
		spc.work();
	}

}
