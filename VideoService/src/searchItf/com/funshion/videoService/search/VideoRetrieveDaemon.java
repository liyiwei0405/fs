package com.funshion.videoService.search;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.transport.TTransportException;

import com.funshion.luc.defines.IndexChannelImp;
import com.funshion.luc.defines.ITableDefine;
import com.funshion.search.ConfUtils;
import com.funshion.search.FsSearchThriftServer;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.systemWatcher.SSDaemonService;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;
import com.funshion.videoService.ServiceDatasController;

public class VideoRetrieveDaemon extends SSDaemonService{
	static File cfgFile = ConfUtils.getConfFile("videoService.conf");
	
	final IndexChannelImp proxy;
	private FsSearchThriftServer nowInstance = null;
	final int servicePort, timeout;
	final VideoServiceProcessorFactory factory;
	public VideoRetrieveDaemon(ConfigReader cr) throws IOException, TTransportException {
		super(cr.getInt("daemonPort", -1), "ssDaemon", false);
		
		this.servicePort = cr.getInt("retrievePort", -1);
		this.timeout = cr.getInt("timeout", 500);
		
		proxy = new IndexChannelImp(ITableDefine.instance.getIndexConfig());

		super.watcher.regAction(new WatcherAction("svrStatus"){
			@Override
			public AnswerMessage run(QueryMessage message) throws Exception {
				AnswerMessage ret = super.answerTemplate();
				ret.actionStatus = ACTION_STATUS_OK;
				ret.answerBody.add("isCanServiceNow = " + proxy.isCanServiceNow());
				return ret;
			}
		});
		factory = new VideoServiceProcessorFactory(proxy);
	}

	@Override
	protected void work(Object[] paras) throws Exception {
		
		ServiceDatasController ctrl = ServiceDatasController.initInstance(
				new ConfigReader(cfgFile, "video-service"),
				proxy);
		ctrl.start();
		while(true){
			//I do not know why the service can be closed,
			//but if the service is closed, try restart new service
			//Do my best to handle the exception
			if(nowInstance != null){
				nowInstance.close();
			}
			logd.info("try init new instance for videoServiceDaemon");
			try {
				nowInstance = new VideoServiceThriftServer(servicePort, timeout, factory);

				while(true){
					Thread.sleep(10);
					if(proxy.isCanServiceNow()){
						break;
					}
				}
				nowInstance.startService();
			} catch (Exception e) {
				e.printStackTrace();
				logd.error(e, "videoServiceDaemon instance fail!");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}finally{
				logd.error("STRANGE! thrift server out of service %s", nowInstance);
			}
		}
	}

	public static void main(String[]args) throws Exception{
		PropertyConfigurator.configureAndWatch(ConfUtils.getConfFile("log4j.properties").getAbsoluteFile().toString());
		ConfigReader cr = new ConfigReader(cfgFile, "search-service");
		VideoRetrieveDaemon daemon = new VideoRetrieveDaemon(cr);
		daemon.startDaemon(null);
		
	}
}
