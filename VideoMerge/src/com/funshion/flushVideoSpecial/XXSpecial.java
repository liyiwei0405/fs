package com.funshion.flushVideoSpecial;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public abstract class XXSpecial {
	final LogHelper log ;

	//用来映射新老ID
	Map<Integer, Integer> videoIdMap = new HashMap<Integer, Integer>();
	Map<Integer, Integer> newsIdMap = new HashMap<Integer, Integer>();

	MongoHelper mergeMongoHelper, fromMongoHelper, toMongoHelper;

	final String name;
	final String className;
	final String specialKey;
	public XXSpecial(String name, String className, String specialKey) throws IOException{
		log = new LogHelper(name);
		this.className = className;
		this.name = name;
		this.specialKey = specialKey;
	}

	public void work() throws Exception{
		System.out.println("start " + name);
		ConfigReader mergeCr, toCfg, fromCfg;
		mergeCr = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "merge_mongo");
		toCfg = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), "merge_mongo");
		fromCfg = new ConfigReader(ConfUtils.getConfFile("videoMerge.conf"), name);

		mergeMongoHelper = new MongoHelper(mergeCr.getValue("ip"), mergeCr.getInt("port"), mergeCr.getValue("database"), mergeCr.getValue("table"));

		fromMongoHelper = new MongoHelper(fromCfg.getValue("ip"), fromCfg.getInt("port"), fromCfg.getValue("database"), name);
		toMongoHelper = new MongoHelper(toCfg.getValue("ip"), toCfg.getInt("port"), toCfg.getValue("database"), name);

		//加载新老ID to Map
		log.warn("import ids to Map");
		importIdToMap();
		log.warn("OK import ids to Map");
		List<DBObject> videoSpecialList = loadAndRewriteSpecial();
		log.warn("OK load videoSpecialList");
		save(videoSpecialList);
		log.warn("OK save!");
		mergeMongoHelper.close();
		fromMongoHelper.close();
		toMongoHelper.close();
	}

	abstract List<DBObject> loadAndRewriteSpecial() ;

	private void importIdToMap() {
		DBCursor cur = mergeMongoHelper.col.find(new BasicDBObject("class", className), new BasicDBObject("videoid", true).append("mapid", true));
		while(cur.hasNext()){
			DBObject dbObject = cur.next();
			videoIdMap.put((int)Double.parseDouble(dbObject.get("mapid").toString()), (int)Double.parseDouble(dbObject.get("videoid").toString()));
		}
		cur.close();		
		if(className.equals("normal")){
			DBCursor cur1 = mergeMongoHelper.col.find(new BasicDBObject("class", "news"), new BasicDBObject("videoid", true).append("mapid", true));
			while(cur1.hasNext()){
				DBObject dbObject = cur1.next();
				this.newsIdMap.put((int)Double.parseDouble(dbObject.get("mapid").toString()), (int)Double.parseDouble(dbObject.get("videoid").toString()));
			}
			cur1.close();	
		}
	}

	private void save(List<DBObject> videoSpecialList) {
		log.info("sorting");
		Collections.sort(videoSpecialList, new Comparator<DBObject>(){

			@Override
			public int compare(DBObject o1, DBObject o2) {
				int x =0 ;
//				try{
					x = (int)Double.parseDouble(o1.get(specialKey).toString()) - (int)Double.parseDouble(o2.get(specialKey).toString());
//				}catch(Exception e){
//					System.out.println(o1.get(specialKey));
//					System.out.println(o2.get(specialKey));
//					System.exit(1);
//				}
				
				return x;
			}

		});
		log.info("sort!");
		//sort
		this.toMongoHelper.col.insert(videoSpecialList);
		log.info("export DONE!");
		log.info("total load %s", videoSpecialList.size());
	}

}
