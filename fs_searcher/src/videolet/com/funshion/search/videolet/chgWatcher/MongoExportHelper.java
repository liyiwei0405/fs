package com.funshion.search.videolet.chgWatcher;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
/**
 * @author liying
 *
 */
public class MongoExportHelper {

	LogHelper log = new LogHelper("export");
	private long maxModifyValue;
	final long startTimeInSeconds;
	private int exportNum = 0;
	final ConfigReader cr;
	final LineWriter writeTo;
	final String videoType;
	final boolean checkTypes;
	/// if startTimeInSeconds = 0 will trigger total export, otherwise trigger update export 
	public MongoExportHelper(long startTimeInSeconds, String videoType,
			LineWriter writeTo) throws Exception{
		this.startTimeInSeconds = startTimeInSeconds;//if startTimeInSecond bigger than zero, do total export
		this.cr = VideoletFactory.getMongoClientConfig(videoType);
		this.writeTo = writeTo;
		this.videoType = videoType;

		this.checkTypes = this.videoType.equals(VideoletFactory.MICRO_VIDEO);
	}
	/**
	 * get current newest modify flag
	 * @param col
	 * @return
	 */
	private long newestModifyFlag(DBCollection  col){
		long lastModifyDate;
		//get newest update time
		BasicDBObject qPubStatus = new BasicDBObject("videoid", new BasicDBObject("$gt", 0));
		BasicDBObject topQuery = new BasicDBObject("modifydate", true).append("_id", false);
		DBCursor topCursor = col.find(qPubStatus, topQuery).sort(new BasicDBObject("modifydate", -1)).limit(1);

		if(topCursor.hasNext()){
			DBObject dbo = topCursor.next();
			lastModifyDate = Long.parseLong(dbo.get("modifydate").toString());
			return lastModifyDate;
		}else{//FIXME if do not get max(modifydate), just exits or throws exception?
			throw new RuntimeException("can not find the maxvalue from modifydate");
		}
	}

	/**
	 * get maxModify time for export action. 
	 * @return 0 if there no data collected, the Unixtime for successful Export action.
	 */
	public long getMaxModifyValue() {
		return maxModifyValue;
	}
	/**
	 * if startTime = endTime， the limit　will be ignored
	 * @param acceptNotPublished
	 * @param startTime
	 * @param endTime
	 * @param limit
	 */

	private BasicDBObject buildQuery(boolean acceptNotPublished, long startTime, long endTime){
		if(startTime == endTime){
			BasicDBObject query = new BasicDBObject("modifydate",  startTime);
			if(!acceptNotPublished){
				query = query.append("publishflag", "published");
			}
			return query;
		}else{
			DBObject o = BasicDBObjectBuilder.start("$gt", startTime).add("$lte", endTime).get();
			BasicDBObject query = new BasicDBObject();
			query.put("modifydate", o);
			if(!acceptNotPublished){
				query = query.append("publishflag", "published");
			}
			return query;
		}
	}
	class CollectInfo{
		int cntAll;
		int cntFail;
		int cntSkip;
		long lastModifyTime;
		HashSet<Integer>lastIds = new HashSet<Integer>();
		public void reg(VideoletRecord record){
			if(record.modifydate == this.lastModifyTime){
				lastIds.add(record.videoId);
			}else{
				if(record.modifydate > this.lastModifyTime){
					this.lastModifyTime = record.modifydate;
					lastIds.clear();
					lastIds.add(record.videoId);
				}else{
					log.error("this should not occur! record.modifydate = %s, this.lastModifyTime = %s",
							record.modifydate, this.lastModifyTime);
				}
			}
		}
		public int acceptCount() {
			return cntAll - cntFail - cntSkip;
		}
	}
	private CollectInfo collect(DBCursor cursor, HashSet<Integer>fbd) throws IOException{
		CollectInfo ci = new CollectInfo();

		while(cursor.hasNext()){
			ci.cntAll ++;
			DBObject dbo = cursor.next();
			
			VideoletRecord record = null;
			try {
				record = VideoletFactory.getRecord(videoType, dbo);
				if(fbd != null){
					if(fbd.contains(record.videoId)){
						ci.cntSkip ++;
						log.warn("skip videoId " + record.videoId + ", for lastModifytime " + record.modifydate);
						continue; //FIXME maybe we want get the number of the ignored?
					}
				}
				if(checkTypes){
					Object type = dbo.get("types");
					if(type != null){
						boolean skipByType = false;
						if(type instanceof List){
							@SuppressWarnings("rawtypes")
							List typeList = (List) type;
							for(Object o : typeList){
								if(o == null){
									continue;
								}
								String typeV = o.toString().trim();
								if(MicroVideoRecord.isFBDMicroVideoType(typeV)){
									skipByType = true;
									break;
								}
							}
						}else{
							log.warn("illegal dataType when filter for " + dbo);
						}
						if(skipByType){
							log.warn("becouse of FBDTYPE! skip forbid record " + dbo);
							ci.cntSkip ++;
							ci.lastModifyTime = record.modifydate;
							continue;
						}
					}

				}
				
				ci.reg(record);

			} catch (Exception e) {
				ci.cntFail ++;
				log.warn("skip record! becouse " + e);
				e.printStackTrace();
				continue;
			}
			record.flushTo(writeTo);
			writeTo.append('\n');

		}
		cursor.close();

		return ci;
	}
	BasicDBObject getFieldFilt() throws IOException{
		String fields = "videoid:title:tags:modifydate:playnum:publishflag";
		if(checkTypes){
			fields = fields + ":types";
		}
		String fields_token[] = fields.split(":");
		BasicDBObject fieldsFiltObject =  new BasicDBObject("_id", false);
		for(String x : fields_token){  
			x = x.trim();
			if(x.length() == 0){
				continue;
			}
			fieldsFiltObject.append(x, true);
		}
		return fieldsFiltObject;
	}
	public void export() throws Exception{
		log.warn("start export for " + this.videoType + "... ...");
		long startTime = this.startTimeInSeconds;
		boolean acceptNotPushlished = this.startTimeInSeconds == 0 ? false : true;
		final long endTime;
		int limitValue = cr.getInt("limitSelectResult", 10000);

		MongoClient mongoClient = new MongoClient(cr.getValue("ip") , cr.getInt("port") );


		try{
			DB db = mongoClient.getDB(cr.getValue("database"));
			DBCollection  col = db.getCollection(cr.getValue("table"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			endTime = newestModifyFlag(col);
			Thread.sleep(1200);

			//avoid multi-record are modified in one seconds and we only get part of them
			BasicDBObject fieldsFiltObject = this.getFieldFilt();
			if(endTime <= startTime){//TODO we may short cut at here
				log.error(this.videoType + "'s newest update date is: " + sdf.format(endTime * 1000) + ", starttime=" + sdf.format(startTime * 1000) );	
			}else{
				log.warn(this.videoType + "'s newest update date is: " + sdf.format(endTime * 1000) + ", starttime=" + sdf.format(startTime * 1000) );		
			}

			int cntall = 0, cntok = 0;
			boolean allFinished = false;
			while(!allFinished){
				while(true){
					CollectInfo firstCi;
					if(startTime == endTime){//becouse startTime is exluded, this is safe
						allFinished = true;
						break;
					}
					BasicDBObject queryWithLimit = buildQuery(acceptNotPushlished, startTime, endTime);
					long stWithLimit = System.currentTimeMillis();
					DBCursor cursorWithLimit = col.find(queryWithLimit, fieldsFiltObject).sort(new BasicDBObject("modifydate", 1)).limit(limitValue);
					log.debug("for select " + cr.section + " use " + (System.currentTimeMillis() - stWithLimit) + " ms");

					stWithLimit = System.currentTimeMillis();	
					firstCi = collect(cursorWithLimit, null);
					cntall += firstCi.cntAll;
					log.info("for collect " + cr.section + " use " + 
							(System.currentTimeMillis() - stWithLimit) + " ms for all-record=" + 
							firstCi.cntAll + ", fail=" +firstCi.cntFail);

					cursorWithLimit.close();
					cntok += firstCi.acceptCount();
					if(firstCi.cntAll == 0){
						allFinished = true;
						break;
					}
					
					if(firstCi.lastModifyTime == 0){//when lastModifyTime == 0 means no data collected
						allFinished = true;
						break;
					}
					
					log.info("for first collect modifydate, time flag move to " + sdf.format(1000 * firstCi.lastModifyTime));
					startTime = firstCi.lastModifyTime; //second round, collect changes in this second

					BasicDBObject queryInSecond = buildQuery(acceptNotPushlished, startTime, startTime);
					long stInMs = System.currentTimeMillis();
					DBCursor cursorInSecond = col.find(queryInSecond, fieldsFiltObject).sort(new BasicDBObject("modifydate", 1));//no limit for this round
					log.debug("for second collect, select " + cr.section + " use " + (System.currentTimeMillis() - stInMs) + " ms");

					stInMs = System.currentTimeMillis();	
					CollectInfo secondCi = collect(cursorInSecond, firstCi.lastIds);
					cntall += secondCi.cntAll;
					cntok += secondCi.acceptCount();
					log.info("for second collect " + cr.section + " use " + (System.currentTimeMillis() - stInMs) + 
							" ms for all-record=" + secondCi.cntAll + ", fail=" + secondCi.cntFail  + ", skip=" + secondCi.cntSkip);

					cursorInSecond.close();
				}
				Thread.sleep(100);
			}

			log.info(cr.section + " get all records: " + cntall + ", ok flushed: " + cntok);
			this.maxModifyValue = endTime;
			this.exportNum += cntok;
		}finally{
			if(mongoClient != null){
				mongoClient.close();
			}
		}
	}
	public int getExportNum() {
		return exportNum;
	}

}
