package com.funshion.search.videolet.search;

import org.apache.thrift.TException;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.videolet.thrift.QueryStruct;
import com.funshion.search.videolet.thrift.QueryVideolet.Iface;
import com.funshion.search.videolet.thrift.VideoletSearchResult;

public class VideoletQueryIface implements Iface{
	public static final LogHelper log = new LogHelper("VideoletQueryIface");
	final String rmtIp;
	final int rmtPort;
	public VideoletQueryIface(String rmtIp, int rmtPort) {
		this.rmtIp = rmtIp;
		this.rmtPort = rmtPort;
	}

	//least recent useage
	public static int rand(int max){
		int idx = (int) (System.nanoTime() % max);
		return idx;
	}

	@Override
	public VideoletSearchResult query(QueryStruct qs) throws TException {
		VideoletSearcher vs = VideoletIndexableFS.instance.getVideoletSearcher();
		long startMs = System.currentTimeMillis();
		if(vs != null){
			try {
				return vs.query(qs);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e, "when videoletSearch %s", qs);
				VideoletSearchResult vrfail = new VideoletSearchResult();
				vrfail.status = 201;
				vrfail.usedTime = System.currentTimeMillis() - startMs;
				return vrfail;
			}
		}else{
			log.error("can not get VideoletSearch instance!");
			VideoletSearchResult vrfail = new VideoletSearchResult();
			vrfail.status = 101;
			vrfail.usedTime = System.currentTimeMillis() - startMs;
			return vrfail;
			
		}
		
	}
}
