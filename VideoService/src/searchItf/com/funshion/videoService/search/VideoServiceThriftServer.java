package com.funshion.videoService.search;

import org.apache.thrift.TProcessorFactory;

import com.funshion.search.FsSearchThriftServer;

public class VideoServiceThriftServer extends FsSearchThriftServer{
	VideoServiceProcessorFactory factory;
	public VideoServiceThriftServer(int port, int timeout, VideoServiceProcessorFactory factory) throws Exception {
		super(port, timeout);
		this.factory = factory;
	}

	@Override
	public TProcessorFactory getProcessorFacotry() {
		return factory;
	}

}
