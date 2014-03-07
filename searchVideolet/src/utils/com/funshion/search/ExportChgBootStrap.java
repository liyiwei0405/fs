package com.funshion.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.funshion.search.ChgTransAnswerHeadStruct;
import com.funshion.search.ChgTransQueryHeadStruct;
import com.funshion.search.DatumDir;
import com.funshion.search.ChgExportFS;
import com.funshion.search.utils.IOUtils;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.TCPServer;
import com.funshion.search.utils.systemWatcher.SSDaemonService;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;
/**
 * @author liying
 */
public class ExportChgBootStrap{
	public static void startExportDaemon(final ExportChgHelper helper, final int daemonPort, final int chgTransPort) throws Exception {
		final ChgExportFS fs = new ChgExportFS(true);
		LogHelper.log.log("daemonPort is %s, chgTransPort is %s", daemonPort, chgTransPort);
		final ExecutorService exeSvc = Executors.newFixedThreadPool(6);
		
		SSDaemonService ssd = new SSDaemonService(daemonPort, "videolet-daemon", false){
			@Override
			protected void work(Object[] paras) throws Exception {
				helper.start();
				this.watcher.regAction(new WatcherAction("doTotalExport"){

					@Override
					public AnswerMessage run(QueryMessage message)
							throws Exception {
						logd.info("ask for totalExport from %s:%s", this.remoteIp, this.remotePort);
						helper.setMakeATotalUpdate();
						AnswerMessage am = WatcherAction.answerTemplate();
						return am;
					}
				});
				this.watcher.regAction(new WatcherAction("crtDatum"){
					@Override
					public AnswerMessage run(QueryMessage message)
							throws Exception {
						logd.debug("ask for crtDatum from %s@%s:%s", message.ienv, this.remoteIp, this.remotePort);

						DatumDir dir = fs.newCopyOfCurrentDatumDir();

						AnswerMessage am = WatcherAction.answerTemplate();
						am.addToAnswerBody("" + chgTransPort);
						if(dir == null){
							am.answerStatus = WatcherAction.ACTION_STATUS_TMP_ERROR;
						}else{
							am.addToAnswerBody(dir.dirName());
							am.addToAnswerBody(dir.size() + "");
						}
						return am;
					}
				});
				this.watcher.regAction(new WatcherAction("crtDatumDetail"){
					@Override
					public AnswerMessage run(QueryMessage message)
							throws Exception {
						logd.debug("ask for crtDatumDetail from %s@%s:%s", message.ienv, this.remoteIp, this.remotePort);

						DatumDir dir = fs.newCopyOfCurrentDatumDir();
						AnswerMessage am = WatcherAction.answerTemplate();
						am.addToAnswerBody("" + chgTransPort);
						if(dir == null){
							am.answerStatus = WatcherAction.ACTION_STATUS_TMP_ERROR;
						}else{
							am.addToAnswerBody(dir.dirName());
							am.addToAnswerBody(dir.size() + "");
							am.addToAnswerBody("details:");
							for(int x = 0; x < dir.size(); x ++){
								am.addToAnswerBody(dir.get(x).toString());
							}
						}
						return am;
					}
				});
				logd.info("deamon thread started!");
			}
		};
		

		TCPServer server = new TCPServer(chgTransPort){
			@Override
			public void doYourJob(Socket clientSocket) {
				ChgLogServerThread t = new ChgLogServerThread(clientSocket, fs);
				exeSvc.execute(t);
			}
		};
		server.start();
		ssd.logd.log("chgTransPort %s is bind and started", chgTransPort);
		ssd.startDaemon(null);
	}

	public static class ChgLogServerThread implements Runnable{
		public static final LogHelper log = new LogHelper("ChgLogServerThread");
		final Socket clientSocket;
		final ChgExportFS fs;
		ChgLogServerThread(Socket clientSocket, ChgExportFS fs) {
			this.clientSocket = clientSocket;
			this.fs = fs;
		}
		public void run(){
			try {
				runWork();
			} catch (Exception e) {
				log.error(e, "when runWork called");
				e.printStackTrace();
			}
		}
		public void runWork() throws Exception{
			this.clientSocket.setSoTimeout(1000);//cofigurable FIXME
			InputStream ips = clientSocket.getInputStream();
			OutputStream ops = this.clientSocket.getOutputStream();

			ChgTransQueryHeadStruct tq = new ChgTransQueryHeadStruct(ips);
			SocketAddress sa = clientSocket.getRemoteSocketAddress();

			String rmtIp = null;
			int rmtPort = 0;
			if(sa instanceof InetSocketAddress){
				InetSocketAddress ia = (InetSocketAddress)sa;
				rmtIp = ia.getAddress().getHostAddress();
				rmtPort = ia.getPort();
			}
			
			log.warn("query from %s@%s:%s, for %s's %s'th chgLog",  tq.envName, rmtIp, rmtPort, tq.dirName, tq.index);
			DatumDir ddir = fs.newCopyOfCurrentDatumDir();

			int status = 0;
			if(ddir.dirName().equals(tq.dirName)){
				if(ddir.size() <= tq.index){
					status = 403;
				}
			}else{
				status = 404;
			}
			
			//tell status!
			IOUtils.writeInt(ops, status);
			if(status != 0){
				ops.flush();
				Thread.sleep(1000);
				this.clientSocket.close();
				log.warn("bad status for client syn %s", status);
			}else{
				log.debug("ok status for client syn %s", status);
				ChgTransAnswerHeadStruct ta = new ChgTransAnswerHeadStruct(ddir, tq.index);
				ta.writeTo(ops);
				FileInputStream fis = new FileInputStream(ddir.get(tq.index).file);
				try{
					byte[]buff = new byte[1024 * 32];
					int sleepItv = 30;
					//max speed as: 1000/30 *32k
					while(true){
						long st = System.currentTimeMillis();
						int read = fis.read(buff);
						if(read < 0){
							break;
						}
						ops.write(buff, 0, read);
						long used = System.currentTimeMillis() - st;
						if(sleepItv > used){
							Thread.sleep(sleepItv - used);
						}
					}
				}finally{
					fis.close();
				}
				ops.flush();
			}

		}
		public void finalize(){
			close();
		}
		private void close() {
			if(clientSocket != null){
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
