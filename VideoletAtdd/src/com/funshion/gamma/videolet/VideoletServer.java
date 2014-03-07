package com.funshion.gamma.videolet;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import com.funshion.videoService.thrift.VideoId;
import com.funshion.videoService.thrift.VideoListResult;
import com.funshion.videoService.thrift.VideoletInfo;

public class VideoletServer implements com.funshion.videoService.thrift.VideoService.Iface{

	@Override
	public VideoListResult getVideoListByIds(List<VideoId> videoIds, short mask)
			throws TException {
		VideoListResult ret = new VideoListResult();
		if(videoIds == null || videoIds.size() == 0){
			ret.retCode = 400;
			ret.retMsg = "Error...";
			return ret;
		}
		ret.retCode = 200;
		ret.retMsg = "OK";


		ret.videoList = new ArrayList<VideoletInfo>();
		for(VideoId vid : videoIds){
			//从monggo中查videoIds对应的记录
			//填写到 ret.videoList， ret.videoList.add(VideoletInfo rec);
			//TDR中描述的事情在此处处理下
			
			VideoletInfo vi = new VideoletInfo();
			vi.videoId = vid.id;
			
			ret.addToVideoList(vi);
		}
		
		return ret;
	}

}
