package com.funshion.videoService.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.thrift.TException;

import com.funshion.luc.defines.ITableDefine;
import com.funshion.luc.defines.IndexChannelImp;
import com.funshion.search.utils.LogHelper;
import com.funshion.videoService.ServiceDatasController;
import com.funshion.videoService.search.VideoletSearcher.SearchInfo;
import com.funshion.videoService.thrift.RetrieveStruct;
import com.funshion.videoService.thrift.VideoBaseListResult;
import com.funshion.videoService.thrift.VideoListResult;
import com.funshion.videoService.thrift.VideoService.Iface;
import com.funshion.videoService.thrift.VideoletBaseInfo;
import com.funshion.videoService.thrift.VideoletBaseRetrieveResult;
import com.funshion.videoService.thrift.VideoletInfo;
import com.funshion.videoService.thrift.VideoletRetrieveResult;

public class VideoServiceIfaceImp implements Iface{
	public static final String videoIdFieldName = ITableDefine.instance.idField.fieldName;
	public static final int VERSION = 1;
	public static final LogHelper log = new LogHelper("MediaQueryIface");
	final String rmtIp;
	final int rmtPort;
	final IndexChannelImp proxy;
	public VideoServiceIfaceImp(String rmtIp, int rmtPort, IndexChannelImp proxy) {
		this.rmtIp = rmtIp;
		this.rmtPort = rmtPort;
		this.proxy = proxy;
	}

	public static int rand(int max){
		int idx = (int) (System.nanoTime() % max);
		return idx;
	}

	@Override
	public VideoBaseListResult getVideoBaseListByIds(List<Integer> videoIdList)
			throws TException {
		long st = System.nanoTime();
		RequestStatistics.reqGetVideoBaseListByIds.requestNumIncrease();
		try{
			if (videoIdList == null) {
				return new VideoBaseListResult(502, "videoIdList error", null);
			}

			List<VideoletBaseInfo> videoBaseList = ServiceDatasController.getBasicInfo(videoIdList);
			VideoBaseListResult videoBaseListResult = new VideoBaseListResult(200, "ok", videoBaseList);
			return videoBaseListResult;
		}finally{
			long usedNan = System.nanoTime() - st;
			RequestStatistics.reqGetVideoBaseListByIds.requestTimeSpentIncreaseInNanoTime(usedNan);

		}
	}

	@Override
	public VideoListResult getVideoListByIds(List<Integer> videoIdList)
			throws TException {
		long st = System.nanoTime();
		RequestStatistics.reqGetVideoListByIds.requestNumIncrease();
		try{
			if (videoIdList == null) {
				return new VideoListResult(502, "videoIdList error", null);
			}

			List<VideoletInfo> videoList = ServiceDatasController.getVideoletInfo(videoIdList);
			VideoListResult videoListResult = new VideoListResult(200, "ok", videoList);
			return videoListResult;
		}finally{
			long usedNan = System.nanoTime() - st;
			RequestStatistics.reqGetVideoListByIds.requestTimeSpentIncreaseInNanoTime(usedNan);
		}
	}
	public void retrieveVideolet(RetrieveStruct rs, Object ret)
			throws Exception {
		long prepareStartTime = System.nanoTime();
		Class<?>retClass = ret.getClass();
		if(rs.ver != VERSION){
			String retMsg = "version mismatch! my version is " + VERSION;
			log.error(retMsg);
			retClass.getField("retCode").set(ret, 501);
			retClass.getField("retMsg").set(ret, retMsg);
			return;
		}
		
		if (rs.fsql == null) {
			retClass.getField("retCode").set(ret, 503);
			retClass.getField("retMsg").set(ret, "fsql is null");
			return;
		}
		try {
			VideoletSearcher vs = (VideoletSearcher) proxy.getSearcher();
			SearchInfo info = vs.search(rs);

			int resultEnd = Math.min(info.docs.totalHits, info.offset + info.limit);
			int toCollect = resultEnd - info.offset;
			List<?> lst;
			if(toCollect > 0){
				List<Integer>ids = new ArrayList<Integer>();
				Document doc;
				for(int x = info.offset; x < resultEnd; x ++){
					doc = vs.sIns.doc(info.docs.scoreDocs[x].doc);
					if(doc == null){
						continue;
					}

					int newId = doc.getField(videoIdFieldName).numericValue().intValue();
					ids.add(newId);
				}
				if(ret instanceof VideoletBaseRetrieveResult){
					lst = ServiceDatasController.getBasicInfo(ids);
				}else{
					lst = ServiceDatasController.getVideoletInfo(ids);
				}
			}else{
				if(ret instanceof VideoletBaseRetrieveResult){
					lst = new ArrayList<VideoletBaseInfo>(0);
				}else{
					lst = new ArrayList<VideoletInfo>(0);
				}
			}

			retClass.getField("retCode").set(ret, 200);
			retClass.getField("retMsg").set(ret, "OK");
			retClass.getField("total").set(ret, info.docs.totalHits);
			retClass.getField("videoList").set(ret, lst);

			double usedTime = (System.nanoTime() - prepareStartTime)/10000 / 100.0;
			retClass.getField("usedTime").set(ret, usedTime);
			if(log.logger.isDebugEnabled()){
				log.debug("total search %s", usedTime);
			}

		} catch (Throwable e) {
			log.error(e, "when execute fsql " + rs);
			e.printStackTrace();
			retClass.getField("retCode").set(ret, 500);
			retClass.getField("retMsg").set(ret, e.getCause() + ":" + e.getMessage());
		}
	}
	@Override
	public VideoletBaseRetrieveResult retrieveVideoletBaseInfo(RetrieveStruct rs)
			throws TException {
		long st = System.nanoTime();
		RequestStatistics.reqRetrieveVideoletBaseInfo.requestNumIncrease();
		try{
			VideoletBaseRetrieveResult ret = new VideoletBaseRetrieveResult();
			try {
				retrieveVideolet(rs, ret);
			} catch (Throwable e) {
				log.error(e, "when execute fsql " + rs);
				ret.retCode = 500;
				ret.retMsg = "ERROR!" + e.getMessage();
			}
			return ret;
		}finally{
			long usedNan = System.nanoTime() - st;
			log.info("query %s use %s ms", rs, usedNan / 1000000);
			RequestStatistics.reqRetrieveVideoletBaseInfo.requestTimeSpentIncreaseInNanoTime(usedNan);
		}
	}

	@Override
	public VideoletRetrieveResult retrieveVideolet(RetrieveStruct rs)
			throws TException {
		long st = System.nanoTime();
		RequestStatistics.reqRetrieveVideolet.requestNumIncrease();
		try{
			VideoletRetrieveResult ret = new VideoletRetrieveResult();
			try {
				retrieveVideolet(rs, ret);
			} catch (Throwable e) {
				log.error(e, "when execute fsql " + rs);
				ret.retCode = 500;
				ret.retMsg = "ERROR!" + e.getMessage();
			}
			return ret;
		}finally{
			
			long usedNan = System.nanoTime() - st;
			log.info("query %s use %s ms", rs, usedNan / 1000000);
			RequestStatistics.reqRetrieveVideolet.requestTimeSpentIncreaseInNanoTime(usedNan);
		}
	}
}
