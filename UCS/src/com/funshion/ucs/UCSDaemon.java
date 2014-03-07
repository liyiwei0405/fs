package com.funshion.ucs;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;

public class UCSDaemon {
	private static final LogHelper log = new LogHelper("UCSDaemon");
	private static ConfigReader cr = null;
	static{
		 try {
			cr = new ConfigReader(ConfUtils.getConfFile("ucs.conf"), "ucs");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void work(UCSThriftServer thriftServer) {
		while(true){
			if(thriftServer != null){
				thriftServer.close();
			}
			log.warn("try init new instance for SearchHintService");
			try {
				thriftServer = new UCSThriftServer(cr);
				thriftServer.startService();
			} catch (Exception e) {
				log.error(e, "server start failed!");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[]args) throws Exception{
		PropertyConfigurator.configureAndWatch(ConfUtils.getConfFile("log4j.properties").getAbsoluteFile().toString());
		
		final UCSUpdateHelper helper = new UCSUpdateHelper(cr);
		helper.start();
		
		UCSThriftServer thriftServer = null;
		work(thriftServer);
	}
}
