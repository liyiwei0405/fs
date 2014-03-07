package com.funshion.flushVideoSpecial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class UgcSpecial extends XXSpecial{

	public UgcSpecial() throws IOException {
		super("fs_video_special_ugc", "ugc", "specialid");
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
			List<Integer>newList = new ArrayList<Integer>();
			for(Object x : lst){
				if(x == null){
					continue;
				}
				int	vx = (int)Double.parseDouble(x.toString());
				Integer newId = this.videoIdMap.get(vx);
				if(newId == null){
					log.error("not found record %s in %s", vx, obj.get("specialid"));
					continue;
				}
				newList.add(newId);
			}
			obj.put("relatedvideo", newList);
			count ++;
			if(count % 1000 == 0){
				log.info("has load %s", count);
			}
			dbObjList.add(obj);
		}
		return dbObjList;
	}
	public static void main(String[] args) throws Exception {
		UgcSpecial spc = new UgcSpecial();
		spc.work();
	}

}
