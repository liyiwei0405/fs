package com.funshion.search.videolet.search;

import java.io.File;
import java.io.IOException;

import org.apache.thrift.transport.TTransportException;

import com.funshion.search.ChgSynClient;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.systemWatcher.SSDaemonService;

public class VideoletSSDaemon extends SSDaemonService{
	public static final File daemonConfig = new File("./config/ss/VideoLetSSDaemon.conf");
	VideoletSearcher searcher;
	public VideoletSSDaemon(int port) throws IOException, TTransportException {
		super(port, "videoletISN", false);
		ConfigReader cr = new ConfigReader(daemonConfig, "synServer");
		ChgSynClient client = new ChgSynClient(cr, VideoletIndexableFS.instance);
		client.start();
	}

	@Override
	protected void work(Object[] paras) throws Exception {
		final ConfigReader vssCfg = new ConfigReader(daemonConfig, "service");
		VideoletSS nowInstance = null;

		while(true){
			if(nowInstance != null){
				nowInstance.close();
			}
			logd.info("try init new instance for VideoletSS");
			try {
				nowInstance = new VideoletSS(vssCfg);
				nowInstance.startService();
			} catch (Exception e) {
				logd.error(e, "VideoletSS instance fail!");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[]args) throws Exception{
		ConfigReader cr = new ConfigReader(daemonConfig, "main");
		int daemonPort = cr.getInt("ssDaemonSvcPort", -1);
		VideoletSSDaemon d = new VideoletSSDaemon(daemonPort);
		d.startDaemon(null);
	}
	
}
