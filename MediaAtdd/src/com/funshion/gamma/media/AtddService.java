package com.funshion.gamma.media;

import java.io.File;
import java.io.IOException;

import com.funshion.gamma.atdd.AbstractThriftService;
import com.funshion.search.utils.ConfigReader;

public class AtddService extends AbstractThriftService{

	public AtddService(int servicePort)
			throws Exception {
		super(servicePort, 
				//com.funshion.videoService.thrift.VideoService.class, 
				com.funshion.gamma.media.thrift.MediaServiceV2.class,

				new MediaServerImpl());
		System.out.println("service boot successfully, listening at port " + servicePort);
	}

	protected void init() throws Exception {
		
	}
	public static void main(String[]args) throws IOException, Exception{
		AtddService server = new AtddService(56321);
		server.startDaemon(null);
	}
}

