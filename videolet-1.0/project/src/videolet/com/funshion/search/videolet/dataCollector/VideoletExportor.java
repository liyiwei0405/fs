package com.funshion.search.videolet.dataCollector;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LineWriter;
import com.mongodb.BasicDBList;
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
public class VideoletExportor {
	static final String fields_token[] = new String[]{
		"videoid","title","tags","modifydate","playnum","publishflag","types","class"
	};
	private static Set<String> microVideoFBDTypes = new HashSet<String>();
	static{
		try {
			LineReader lr = new LineReader("./config/microVideoFbd.set");
			while(lr.hasNext()){
				String line = lr.next().trim().toLowerCase();
				if(line.length() == 0 || line.startsWith("#")){
					continue;
				}
				microVideoFBDTypes.add(line);
			}
			lr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("read config/microVideoFbd.set ERROR! system exit");
			System.exit(1);
		}
	}
	public static boolean isFBDMicroVideoType(String t){
		return microVideoFBDTypes.contains(t.toLowerCase().trim());
	}

	Logger log = Logger.getLogger("export");
	private long maxModifyValue;
	final long startTimeInSeconds;
	private int exportNum = 0;
	final ConfigReader cr;
	final LineWriter writeTo;
	final ArrayList<Integer>lstToDel;
	/// if startTimeInSeconds = 0 will trigger total export, otherwise trigger update export 
	public VideoletExportor(long startTimeInSeconds,
			LineWriter writeTo) throws Exception{
		this.startTimeInSeconds = startTimeInSeconds;//if startTimeInSecond bigger than zero, do total export
		this.cr = VideoletFactory.getConfig();
		this.writeTo = writeTo;
		if(startTimeInSeconds > 0){
			lstToDel = new ArrayList<Integer>();
		}else{
			lstToDel = null;
		}
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
		BasicDBObject query;
		if(startTime == endTime){
			query = new BasicDBObject("modifydate",  startTime);
			if(!acceptNotPublished){
				query = query.append("publishflag", "published");
			}
		}else{
			DBObject o = BasicDBObjectBuilder.start("$gt", startTime).add("$lte", endTime).get();
			query = new BasicDBObject();
			query.put("modifydate", o);
			if(!acceptNotPublished){
				query = query.append("publishflag", "published");
			}
			
		}
		
		BasicDBList or = new BasicDBList();
		or.add(new BasicDBObject("class", "normal"));
		or.add(new BasicDBObject("class", "ugc"));
		query.append("$or", or);
		return query;
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
					//FIXME It can not be! here, we need double check?
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
				try{
					record = VideoletFactory.getRecord(dbo);
				}catch(Exception e){
					log.error("error get record:" + e);
					continue;
				}
				if(fbd != null){
					if(fbd.contains(record.videoId)){
						ci.cntSkip ++;
						log.warn("skip videoId " + record.videoId + ", for lastModifytime " + record.modifydate);
						continue; //FIXME maybe we want get the number of the ignored?
					}
				}
				if(record.videoOrUgc == VideoletRecord.isMicroVideoType){
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
								if(isFBDMicroVideoType(typeV)){
									skipByType = true;
									break;
								}
							}
						}else{
							log.warn("illegal dataType when filter for " + dbo);
						}
						if(skipByType){
							log.debug("becouse of FBDTYPE! skip forbid record " + dbo);
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
			if(record != null){
				if(lstToDel != null){
					lstToDel.add(record.docId);
				}
				if(!record.isDel){
					record.writeToBuffer(writeTo);
					writeTo.append('\n');
				}
			}
		}
		cursor.close();

		return ci;
	}
	
	BasicDBObject getFieldFilt() throws IOException{

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
		log.warn("start export ...");
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
				log.error("newest update date is: " + sdf.format(endTime * 1000) + ", starttime=" + sdf.format(startTime * 1000) );	
			}else{
				log.warn("newest update date is: " + sdf.format(endTime * 1000) + ", starttime=" + sdf.format(startTime * 1000) );		
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

	public static void main(String[]args) throws Exception{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");

		LineWriter lw = new LineWriter("./xml.xml", Charset.forName("utf-8"));
		VideoletExportor exp = new  VideoletExportor(0, 
				lw);
		exp.export();
	}
}
