//package com.funshion.luc.defines;
//
//import java.io.IOException;
//
//import org.apache.thrift.transport.TTransportException;
//
//import com.funshion.search.ChgSynClient;
//import com.funshion.search.DatumDir;
//import com.funshion.search.FsSearchThriftServer;
//import com.funshion.search.utils.systemWatcher.SSDaemonService;
//import com.funshion.search.utils.systemWatcher.WatcherAction;
//import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
//import com.funshion.search.utils.systemWatcher.message.QueryMessage;
//
//public class SSDaemon extends SSDaemonService{
//	final ChgSynClient client;
//	private FsSearchThriftServer nowInstance = null;
//	public SSDaemon(int port) throws IOException, TTransportException {
//		super(port, "mediaISN", false);
//		
//		client = new ChgSynClient(ITableDefine.instance.synServerConfig(), SSIndexableFS.getInstance());
//		client.start();
//		super.watcher.regAction(new WatcherAction("synLog"){
//			@Override
//			public AnswerMessage run(QueryMessage message) throws Exception {
//				AnswerMessage ret = super.answerTemplate();
//				ret.actionStatus = ACTION_STATUS_OK;
//				DatumDir ddir = client.fs.newCopyOfCurrentDatumDir();
//
//				if(ddir == null) {
//					ret.answerBody.add("CurrentDatumDir is null");
//				}else{
//					ret.addToAnswerBody("dir name " + ddir.dirName());
//					int size = ddir.size();
//
//					ret.addToAnswerBody("size : " + size);
//					for(int x = 0; x < size; x ++){
//						ret.addToAnswerBody("file " + x + ": "  + ddir.get(x));
//					}
//				}
//				return ret;
//			}
//		});
//
//		super.watcher.regAction(new WatcherAction("svrStatus"){
//			@Override
//			public AnswerMessage run(QueryMessage message) throws Exception {
//				AnswerMessage ret = super.answerTemplate();
//				ret.actionStatus = ACTION_STATUS_OK;
//				DatumDir ddir = client.fs.newCopyOfCurrentDatumDir();
//				if(ddir == null) {
//					ret.answerBody.add("CurrentDatumDir is null");
//				}else{
//					ret.answerBody.add("dir name " + ddir.dirName());
//					int size = ddir.size();
//
//					ret.addToAnswerBody("size : " + size);
//					for(int x = 0; x < size; x ++){
//						ret.answerBody.add("file " + x + ": "  + ddir.get(x));
//					}
//					if(!nowInstance.isAtService()){
//						ret.actionStatus = ACTION_STATUS_TMP_ERROR;
//						ret.answerBody.add("ERROR! service not started");
//					}else{
//						ret.answerBody.add("service started at " + nowInstance.port);
//					}
//				}
//				return ret;
//			}
//		});
//	}
//
//	@Override
//	protected void work(Object[] paras) throws Exception {
//		
//		while(true){
//			//I do not know why the service can be closed,
//			//but if the service is closed, try restart new service
//			//Do my best to handle the exception
//			if(nowInstance != null){
//				nowInstance.close();
//			}
//			logd.info("try init new instance for MediaSSDaemon");
//			try {
//				nowInstance = (FsSearchThriftServer) ITableDefine.instance.fsSearchThriftServerClass.getConstructor(int.class, int.class).newInstance((Integer)paras[0], (Integer)paras[1]);
//				
//				while(true){
//					Thread.sleep(10);
//					if(nowInstance.canService()){
//						break;
//					}
//				}
//				nowInstance.startService();
//			} catch (Exception e) {
//				e.printStackTrace();
//				logd.error(e, "MediaSSDaemon instance fail!");
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//			}finally{
//				logd.error("STRANGE! thrift server out of service %s", nowInstance);
//			}
//		}
//	}
//}
