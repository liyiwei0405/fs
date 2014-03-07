package com.funshion.videoService;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.funshion.luc.defines.IndexActionDetector.ActionType;
import com.funshion.search.ConfUtils;
import com.funshion.search.IndexableRecord;
import com.funshion.search.IndexableRecordQueue;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.videoService.thrift.VideoletInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class VideoletExportHelper{
	private static final LogHelper log = new LogHelper("export");
	final int limitValue;
	private long lastEnd;
	public VideoletExportHelper(final int limitValue){
		this.limitValue = limitValue;
	}
	void updateExport(IndexableRecordQueue indexableQueue,
			long startTime, Map<Integer, VideoletInfo> mapToSave) throws Exception{

		Map<Integer, VideoletInfo> videoMapTmp = new HashMap<Integer, VideoletInfo>();
		export(startTime, videoMapTmp, null);
		if(indexableQueue != null){
			List<IndexableRecord>updates = new LinkedList<IndexableRecord>();
			for(Entry<Integer, VideoletInfo> entry : videoMapTmp.entrySet()){
				VideoletInfo videoletInfo = entry.getValue();
				VideoletIndexableRecord indexable = updateVideolet(videoletInfo, mapToSave);
				if(indexable != null){
					updates.add(indexable);
					log.debug("%s action for %s", indexable.actionType, videoletInfo.videoId);
				}
			}
			if(updates.size() > 0){
				indexableQueue.index(updates.listIterator(0));
				log.info("need to upate index, size %s", updates.size());
			}else{
				log.info("no need to upate index");
			}
		}
	}
	public static VideoletIndexableRecord updateVideolet(VideoletInfo newInfo,
			Map<Integer, VideoletInfo> mapToSave) throws Exception{
		boolean del = VideoletInfoLoader.isDel(newInfo);
		VideoletIndexableRecord ret = null;
		if(del){
			VideoletInfo old = mapToSave.remove(newInfo.videoId);
			if(old != null){
				ret = new VideoletIndexableRecord(newInfo, ActionType.DEL);
			}
		}else{
			VideoletInfo old = mapToSave.put(newInfo.videoId, newInfo);
			if(old == null){
				ret = new VideoletIndexableRecord(newInfo, ActionType.ADD);
			}else{
				if(!VideoletIndexableRecord.isEqualAsIndexableRecord(old, newInfo)){
					ret = new VideoletIndexableRecord(newInfo, ActionType.UPDATE);
				}
			}
		}
		return ret;
	}
	void doTotalExport(IndexableRecordQueue channel,
			Map<Integer, VideoletInfo> mapToSave) throws Exception{
		long startTime = System.currentTimeMillis();
		//save old mapids
		long st, ed;
		st = System.currentTimeMillis();
		HashSet<Integer>orgSet = new HashSet<Integer>(mapToSave.size());
		orgSet.addAll(mapToSave.keySet());
		ed = System.currentTimeMillis();
		log.info("TOTAL, save old ids use %s ms, size %s", ed - st, orgSet.size());

		st = System.currentTimeMillis();
		export(0, mapToSave, orgSet);
		ed = System.currentTimeMillis();
		log.info("TOTAL, export_1 ids use %s ms, currentMapSize %s", ed - st, mapToSave.size());

		//do update
		st = System.currentTimeMillis();
		export(lastEnd, mapToSave, orgSet);
		ed = System.currentTimeMillis();
		log.info("TOTAL, export_2 ids use %s ms, currentMapSize %s", ed - st, mapToSave.size());

		log.info("need remove ids %s", orgSet);
		for(int x : orgSet){
			mapToSave.remove(x);
		}
		log.info("update map done, videoMap' s size: %s, used %s ms",
				mapToSave.size(), ed - startTime);
		if(channel != null){
			st = System.currentTimeMillis();
			Iterator<VideoletInfo> itr = mapToSave.values().iterator();
			channel.index(new TotalAddIndexableRecordInterator(itr));
			ed = System.currentTimeMillis();
			log.info("TOTAL, index use %s ms, currentMapSize %s", ed - st, mapToSave.size());

		}else{
			log.warn("skip index! becouse channel is null");
		}
	}

	private void export(long startTime, Map<Integer, VideoletInfo> mapToSave, 
			Set<Integer>news) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		boolean acceptNotPushlished = startTime == 0 ? false : true;

		MongoHelper mongoHelper = null;
		try{
			mongoHelper = new MongoHelper(new ConfigReader(ConfUtils.getConfFile("videoService.conf"), "mongo"));
			DBObject fieldsObject = new BasicDBObject("_id", false).append("ta_0", false).append("ta_1", false).append("ta_2", false).append("ta_3", false).append("ta_4", false).append("ta_5", false).append("ta_6", false).append("ta_7", false).append("ta_8", false).append("ta_9", false);

			long endTime = getNewestModifydate(mongoHelper.col);
			Thread.sleep(1050);

			if(endTime >= startTime){
				log.warn("newest modifydate is: %s (%s)" + ", startTime is: %s(%s)",
						endTime, sdf.format(endTime * 1000),
						startTime, sdf.format(startTime * 1000));
			}else{
				throw new Exception("newest modifydate is: " + endTime + "(" + sdf.format(endTime * 1000) + ")" + ", startTime is: " + startTime + "(" + sdf.format(startTime * 1000) + ")");	
			}

			int round = 0;
			int collect = 0;
			while(true){	
				round ++;

				if(startTime == endTime){
					break;
				}

				long stFirst = System.currentTimeMillis();
				DBCursor firstCur = mongoHelper.col.find(buildQuery(acceptNotPushlished, startTime, endTime), fieldsObject).sort(new BasicDBObject("modifydate", 1)).limit(limitValue);
				LinkedList<VideoletInfo>tmpList = collect(firstCur);

				if(tmpList.size() == 0){
					break;
				}
				long lastOneTime = tmpList.getLast().modifyDate;
				log.info("first collect %s use %s ms, move flag to %s",
						tmpList.size(), System.currentTimeMillis() - stFirst,
						lastOneTime, 
						sdf.format(1000 * lastOneTime));
				ListIterator<VideoletInfo>itr = tmpList.listIterator(0);
				while(itr.hasNext()){
					VideoletInfo vi = itr.next();
					mapToSave.put(vi.videoId, vi);
					if(news != null){
						news.remove(vi.videoId);
					}
					collect ++;
				}
				startTime = lastOneTime; 

				//second round, collect changes in this second
				long stSecond = System.currentTimeMillis();
				DBCursor secondCur = mongoHelper.col.find(buildQuery(acceptNotPushlished, startTime, startTime), fieldsObject).sort(new BasicDBObject("modifydate", 1));//no limit for this round
				tmpList = collect(secondCur);
				itr = tmpList.listIterator(0);
				while(itr.hasNext()){
					VideoletInfo vi = itr.next();
					mapToSave.put(vi.videoId, vi);
					if(news != null){
						news.remove(vi.videoId);
					}
					collect ++;
				}
				if(log.logger.isDebugEnabled()){
					log.debug("second collect %s use %s ms", System.currentTimeMillis() - stSecond);
				}
				log.info("round %s loading start time %s, endtime %s, has load %s", 
						round,
						sdf.format(startTime * 1000),
						sdf.format(endTime * 1000),
						collect);
				Thread.sleep(10);//avoid cpu overload
			}
			this.lastEnd  = endTime;
			log.warn("read %s in all. last modifydate move to: %s(%s)",
					collect, lastEnd, sdf.format(lastEnd * 1000));
		}finally{
			if(mongoHelper != null){
				mongoHelper.close();
			}
		}		
	}

	private long getNewestModifydate(DBCollection col){
		long lastModifyDate;
		BasicDBObject queryObject = new BasicDBObject();
		BasicDBObject fieldObject = new BasicDBObject("modifydate", true).append("_id", false);
		DBCursor cur = col.find(queryObject, fieldObject).sort(new BasicDBObject("modifydate", -1)).limit(1);

		if(cur.hasNext()){
			DBObject dbo = cur.next();
			lastModifyDate = Long.parseLong(dbo.get("modifydate").toString());
			return lastModifyDate;
		}else{
			throw new RuntimeException("can not find the maxvalue from modifydate");
		}
	}

	private DBObject buildQuery(boolean acceptNotPushlished, long startTime, long endTime){
		DBObject queryObject = new BasicDBObject();
		if(!acceptNotPushlished){
			queryObject.put("publishflag", "published");
		}
		if(startTime == endTime){
			queryObject.put("modifydate",  startTime);

		}else{
			DBObject o = BasicDBObjectBuilder.start("$gt", startTime).add("$lte", endTime).get();
			queryObject.put("modifydate", o);
		}
		return queryObject;
	}


	private LinkedList<VideoletInfo> collect(DBCursor cur){
		LinkedList<VideoletInfo> listToCollect = new LinkedList<VideoletInfo> ();
		while(cur.hasNext()){
			DBObject dbObject = cur.next();
			Object ovideoid = dbObject.get("videoid");
			try {
				VideoletInfo videoletInfo = VideoletInfoLoader.loadDBObject(dbObject);
				if(videoletInfo != null){
					listToCollect.add(videoletInfo);
				}
			} catch (Exception e) {
				log.warn("fail videoid " + ovideoid + "! becouse " + e.getMessage());
				continue;
			} 
		}
		cur.close();
		return listToCollect;
	}
	public long getLastEnd() {
		return lastEnd;
	}


}
