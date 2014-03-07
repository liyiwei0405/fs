package com.funshion.videoService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.funshion.search.ChanneledExportChgHelper;
import com.funshion.search.ConfUtils;
import com.funshion.search.IndexChannel;
import com.funshion.search.IndexableRecord;
import com.funshion.search.IndexableRecordQueue;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.videoService.thrift.VideoletBaseInfo;
import com.funshion.videoService.thrift.VideoletInfo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class ServiceDatasController extends ChanneledExportChgHelper {
	private static final LogHelper log = new LogHelper("VideoletExportHelper");
	private Map<Integer, VideoletInfo> videoMap ;

	private final long totalIntervalSec;
	private final long updateIntervalSec;
	private final long interactiveUpdateInterval;
	private long maxModifydate;
	private final Object updateLock = new Object();
	private final int selectLimit;
	private static ServiceDatasController instance;
	private int mapSize = 1500 * 1000;
	private ServiceDatasController(ConfigReader cr, IndexChannel fs) throws IOException{
		super(cr, fs);
		this.totalIntervalSec = cr.getInt("totalIntervalSec", 24 * 3600);
		this.updateIntervalSec = cr.getInt("updateIntervalSec", 10 * 1000);
		this.interactiveUpdateInterval = cr.getInt("interactiveUpdateInterval", 10 * 1000);
		this.selectLimit = cr.getInt("selectLimit", 10000);
		this.mapSize = cr.getInt("mapSize", 1500 * 1000);
		log.warn("totalIntervalSec %smin, updateIntervalSec %smin", totalIntervalSec/60, updateIntervalSec/60);
	}

	public class InteractiveUpdator extends Thread{
		List<VideoIdUpdator> queues;
		public void run(){
			while(true){
				if(totalExportisReady){
					List<IndexableRecord>updates = new ArrayList<IndexableRecord>();
					synchronized(updateLock){
						for(VideoIdUpdator queue : queues){
							updateMap(queue, updates);
						}
						flushIndexUpdate(updates);
					}
					updates= null;
				}
				try {
					sleep(interactiveUpdateInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		private void flushIndexUpdate(List<IndexableRecord> updates) {
			try {
				IndexableRecordQueue updateChannel = channel.openChannel(false);
				updateChannel.index(updates.iterator());
			} catch (Exception e) {
				log.error(e, "when flush to index");
				e.printStackTrace();
			}
		}

		private void updateMap(VideoIdUpdator queue, List<IndexableRecord>updates){
			try{
				queue.update(updates);
			}catch(Throwable e){
				log.error("when update for %s", queue);
			}
		}
	}
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
	@Override
	public void doExport(boolean totalExport, IndexableRecordQueue indexableQueue) throws Exception{
		
		synchronized(this.updateLock){
			VideoletExportHelper helper = 
					new VideoletExportHelper(selectLimit);
			if(totalExport){
				Map<Integer, VideoletInfo> tmp = this.videoMap;
				if(tmp == null){
					tmp = new ConcurrentHashMap<Integer, VideoletInfo>(mapSize);
				}
				helper.doTotalExport(indexableQueue, tmp);
				
				this.maxModifydate = helper.getLastEnd();
				helper = new VideoletExportHelper(selectLimit);
				helper.updateExport(indexableQueue, this.maxModifydate, tmp);
				this.videoMap = tmp;
				this.maxModifydate = helper.getLastEnd();
				
			}else{
				helper.updateExport(indexableQueue, this.maxModifydate, this.videoMap);
				this.maxModifydate = helper.getLastEnd();
			}
			log.info("update map done, videoMap' s size: %s, last end move to %s", videoMap.size(),
					sdf.format(this.maxModifydate * 1000));
		}
	}

	@Override
	protected boolean needTotalExport() {
		if(lastTotalExportTime == 0){//do total export at startup
			return true;
		}else{
			long hasPassedSeconds = (System.currentTimeMillis()  - this.lastTotalExportTime) / 1000;
			if(hasPassedSeconds > this.totalIntervalSec){
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean needUpdate() {
		long hasPassedSeconds = (System.currentTimeMillis()  - this.lastUpdateExportTime) / 1000;
		if(hasPassedSeconds > this.updateIntervalSec){
			return true;
		}
		return false;
	}


	public synchronized static ServiceDatasController initInstance(ConfigReader cr,
			IndexChannel channel) throws IOException {
		if(instance == null){
			instance = new ServiceDatasController(cr, channel); 
		}
		return instance;
	}
	//not safe? besure initInstance is called we system booting
//	public static ServiceDatasController getInstance() {
//		return instance;
//	}
	public static List<VideoletBaseInfo> getBasicInfo(List<Integer> videoIdList){
		Map<Integer, VideoletInfo> videoMap = instance.videoMap;

		List<VideoletBaseInfo> videoBaseList = new ArrayList<VideoletBaseInfo>();
		for(Integer videoId : videoIdList){
			VideoletInfo videoletInfo = videoMap.get(videoId);
			if(videoletInfo == null){
				continue;
			}
			VideoletBaseInfo videoletBaseInfo = new VideoletBaseInfo(
					videoletInfo.videoId, 
					videoletInfo.title, 
					videoletInfo.picturePath,
					videoletInfo.timeLen,
					videoletInfo.ordering,
					videoletInfo.score, 
					videoletInfo.scoreNum,
					videoletInfo.playNum,
					videoletInfo.playNumInit, 
					videoletInfo.plots, 
					videoletInfo.modifyDate);
			videoBaseList.add(videoletBaseInfo);
		}
		return videoBaseList;
	}

	public static List<VideoletInfo> getVideoletInfo(List<Integer> videoIdList){
		Map<Integer, VideoletInfo> videoMap = instance.videoMap;

		List<VideoletInfo> videoList = new ArrayList<VideoletInfo>();
		for(Integer videoId : videoIdList){
			VideoletInfo inf = videoMap.get(videoId);
			if(inf != null){
				videoList.add(inf);
			}
		}
		return videoList;
	}

	public class VideoIdUpdator extends UpdateMessageQueue{
		LogHelper log = new LogHelper("videoIdMsgQ");
		MongoHelper helper;
		final ConfigReader mongoConfig;
		public VideoIdUpdator() throws IOException{
			super(new ConfigReader(
					ConfUtils.getConfFile("videoService.conf"), 
					"VideoIdMessageQueue"));
			mongoConfig = new ConfigReader(cr.configFile,  cr.getValue("mongoConfig", "mongo"));
		}

		public void update(List<IndexableRecord>updates) throws Exception{
			List<Integer>videoIds = new ArrayList<Integer>();
			while(true){
				try{
					Delivery dev = queue.poll();
					if(dev == null){
						break;
					}
					byte [] bs = dev.getBody();
					int intValue = Integer.parseInt(new String(bs));
					videoIds.add(intValue);
				}catch(Exception e){
					log.error("unrecognizable message!");
				}
			}

			if(videoIds.size() == 0){
				return;
			}
			//select from mongo
			if(helper == null){
				helper = new MongoHelper(mongoConfig);
			}
			DBCursor cur;
			DBObject query = query(videoIds);
			try{
				cur = helper.col.find(query);
			}catch(Exception e){
				helper.close();
				helper = new MongoHelper(mongoConfig);
				cur = helper.col.find(query);
			}
			while(cur.hasNext()){
				DBObject dbo = cur.next();
				VideoletInfo newInfo = null;
				try {
					newInfo = VideoletInfoLoader.loadDBObject(dbo);
					VideoletIndexableRecord rec = VideoletExportHelper.updateVideolet(newInfo, videoMap);
					if(rec != null){
						updates.add(rec);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("when update %s", newInfo == null ? "unknown videolet" : newInfo.videoId + "");
				}
			}
		}
		private DBObject query(List<Integer>videoIds){
			BasicDBList dblist = new BasicDBList();
			dblist.addAll(videoIds);
			DBObject queryObject = new BasicDBObject("videoid", new BasicDBObject("$in", dblist));
			return queryObject;
		}
	}
}
