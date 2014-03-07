package com.funshion.gamma.atdd.vodInfo;

import java.io.File;
import java.io.IOException;

import com.funshion.gamma.atdd.AbstractThriftService;
import com.funshion.search.utils.ConfigReader;

public class VODInfoServer extends AbstractThriftService{
	public VODInfoServer(int servicePort)
			throws Exception {
		super(servicePort, com.funshion.gamma.atdd.vodInfo.thrift.VodInfoService.class,
				VodInfoServiceImp.instance);
	}

	@Override
	protected void init() throws Exception {
		 PlayInfoMap.nowInstance();
	}

	public static void main(String[]args) throws IOException, Exception{
		File cfg = new File("./config/vodinfo.atdd.conf");
		ConfigReader cr = new ConfigReader(cfg, "service");
		VODInfoServer server = new VODInfoServer(cr.getInt("servicePort"));
		
		server.startDaemon(null);
	}

}
