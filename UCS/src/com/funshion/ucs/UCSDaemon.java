package com.funshion.ucs;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import com.funshion.search.ChgExportFS;
import com.funshion.search.ConfUtils;
import com.funshion.search.ExportChgBootStrap;
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
	
	public static void work(ThriftServer thriftServer) {
		while(true){
			if(thriftServer != null){
				thriftServer.close();
			}
			log.warn("try init new instance for SearchHintService");
			try {
				thriftServer = new ThriftServer(cr);
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
		
		final int chgWatcherDaemonPort = cr.getInt("chgWatcherDaemonPort");
		final int chgWatcherTransPort = cr.getInt("chgWatcherTransPort");
		final ChgExportFS fs = new ChgExportFS(true, cr);
		final UCSUpdateHelper helper = new UCSUpdateHelper(cr, fs);
		ExportChgBootStrap.startExportDaemon(helper, chgWatcherDaemonPort, chgWatcherTransPort);
		log.log("daemonPort is %s, chgTransPort is %s", chgWatcherDaemonPort, chgWatcherTransPort);	

		ThriftServer thriftServer = null;
		work(thriftServer);
	}
}
