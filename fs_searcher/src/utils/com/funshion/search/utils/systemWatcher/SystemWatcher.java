package com.funshion.search.utils.systemWatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.systemWatcher.innerAction.ChangeLogExceptionAction;
import com.funshion.search.utils.systemWatcher.innerAction.EchoAction;
import com.funshion.search.utils.systemWatcher.innerAction.GCAction;
import com.funshion.search.utils.systemWatcher.innerAction.HeartBeatAction;
import com.funshion.search.utils.systemWatcher.innerAction.HeartBeatClientsAction;
import com.funshion.search.utils.systemWatcher.innerAction.ShutDownAction;
import com.funshion.search.utils.systemWatcher.innerAction.ShutDownAction.ShutdownHook;
import com.funshion.search.utils.systemWatcher.innerAction.SysInfoAction;
import com.funshion.search.utils.systemWatcher.innerAction.SysPropAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.MessageService;
import com.funshion.search.utils.systemWatcher.message.MessageService.Iface;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

/**
 * a TCP based System stopper,
 * @author Fisher@Beiming
 *
 */
public class SystemWatcher {

	class ActionDealor implements Iface{
		public final String ip;
		public final int port;
		public ActionDealor(String ip, int port){
			this.ip = ip;
			this.port = port;
		}
		@Override
		public AnswerMessage queryMsg(QueryMessage qm) throws TException {
			AnswerMessage ret;
			if(qm == null){
				ret = WatcherAction.answerTemplate(WatcherAction.ANSWER_STATUS_NO_MESSAGE_POINTER);
				ret.answerBody.add("ANSWER_STATUS_NO_MESSAGE_POINTER");
				log.warn("No NO_MESSAGE_POINTER");
			}else if(qm.messageName == null){
				ret = WatcherAction.answerTemplate(WatcherAction.ANSWER_STATUS_NO_MESSAGE_NAME);
				ret.answerBody.add("ANSWER_STATUS_NO_MESSAGE_NAME");
				log.warn("null message name");
			}else if(qm.messageBody == null){
				ret = WatcherAction.answerTemplate(WatcherAction.ANSWER_STATUS_NO_MESSAGE_PARAS);
				ret.answerBody.add("ANSWER_STATUS_NO_MESSAGE_PARAS");
				log.warn("ANSWER_STATUS_NO_MESSAGE_PARAS got for '%s'", qm.messageName);
			}
			try {
				final WatcherAction act = getAction(qm.messageName);
				if(act != null) {
					act.setRemoteIp(ip);
					act.setRemotePort(port);
					ret = act.run(qm);
				}else {
					ret = WatcherAction.answerTemplate(WatcherAction.ANSWER_STATUS_NO_MESSAGE_ACTION_REGISTER);
					ret.answerBody.add("NO_MESSAGE_ACTION_REGISTER for '" + qm.messageName + "'");
					log.warn("Unkonwn command got '%s'", qm.messageName);
				}
			} catch (Exception e) {
				log.warn(e, "got error when del socket", this);
				ret = WatcherAction.answerTemplate(WatcherAction.ANSWER_STATUS_EXCEPTION_WHEN_RUN);
				ret.answerBody.add(e.toString());
			}
			if(ret == null){
				ret = WatcherAction.answerTemplate(WatcherAction.ANSWER_STATUS_ACTION_RET_NULL);
			}
			return ret;
		}
	}


	public static final String WatherLogName="sysWatcher";
	public static final LogHelper wLog = new LogHelper(WatherLogName);

	private Map<String, WatcherAction>actions =
			Collections.synchronizedMap(new HashMap<String, WatcherAction>());

	public final String sysName;
	public final LogHelper log;
	private int port;
	RestartableSvc svc;
	public final boolean asHeartBeatServer;
	public SystemWatcher(final int port, final String sysName, boolean asHeartBeatServer) throws IOException, TTransportException {
		this.sysName = sysName;
		this.port = port;
		log = new LogHelper(sysName);
		this.asHeartBeatServer = asHeartBeatServer;

		this.regAction(new ShutDownAction());
		this.regAction(new SysInfoAction(sysName));
		this.regAction(new GCAction()); 
		this.regAction(new ChangeLogExceptionAction());
		this.regAction(new EchoAction());
		this.regAction(new SysPropAction());
		if(asHeartBeatServer){
			log.debug("asHeartBeatServer enabled");
			this.regAction(new HeartBeatAction());
			this.regAction(new HeartBeatClientsAction());
		}else{
			log.debug("asHeartBeatServer disabled");
		}
		this.regAction(new WatcherAction("help") {
			@Override
			public AnswerMessage run(QueryMessage message) throws Exception {
				String reply = actions().toString();
				AnswerMessage am = answerTemplate(ANSWER_STATUS_OK_RUNNED);
				am.answerBody.add(reply);
				return am;
			}
		});

		log.warn("%s try start watcher using port %s",
				sysName, port);
		svc = new RestartableSvc();
		svc.init();
		svc.start();

	}
	class RestartableSvc extends Thread{
		TThreadedSelectorServer server;
		boolean stopService = false;
		private void init() throws TTransportException{
			if(server != null){
				server.stop();
			}

			InetSocketAddress add = new InetSocketAddress(port);
			TNonblockingServerSocket socket = new TNonblockingServerSocket(add, 10000); 
			TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(socket);
			args.selectorThreads = 2;
			args.transportFactory(new TFastFramedTransport.Factory());
	        args.protocolFactory(new TBinaryProtocol.Factory());
			args.processorFactory(new MessageProcessorFactory());
			args.executorService(Executors.newFixedThreadPool(4));
			server = new TThreadedSelectorServer(args); 
		}
		public void run(){
			while(!stopService){
				try{
					if(server != null){
						synchronized(this){
							server.serve();
						}
					}
				}finally{
					if(!stopService){
						try {
							init();
						} catch (TTransportException e) {
							log.error(e, "when init watcher's RestartableSvc");
							e.printStackTrace();
						}
						try {
							sleep(400);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		public synchronized void stopService() {
			stopService = false;
			if(server != null){
				server.stop();
			}
		}
	}
	/**
	 * block the thread and wait for service
	 */
	public Set<String> actions(){
		return this.actions.keySet();
	}

	public void regAction(WatcherAction act) {
		if(this.actions.containsKey(act.cmd.toLowerCase())) {
			log.warn("%s already registed, skip", act.cmd);
			return;
		}
		this.actions.put(act.cmd.toLowerCase().trim(), act);
	}
	private ShutDownAction getShutDownAction() {
		return (ShutDownAction) this.getAction(ShutDownAction.CMD);
	}

	protected WatcherAction getAction(String actionName){
		return this.actions.get(actionName.toLowerCase().trim());
	}


	public ShutdownHook regStopHook() {
		return this.getShutDownAction().regStopHook();
	}


	/**
	 * block this thread, never exist!
	 * may be used when need block 
	 */
	public void block() {
		log.warn("WatherLogName blocking start");
		while(true) {
			try {
				Thread.sleep(60*60*1000);
			} catch (InterruptedException e) {
				log.error(e, "sleep error");
			}
		}
	}
	/**
	 * reg and start the watcher
	 * @param port
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws TTransportException 
	 */
	public static SystemWatcher regWatcher(int port, String sysName, boolean asHeartBeatServer) throws IOException, TTransportException {
		SystemWatcher stp = new SystemWatcher(port, sysName, asHeartBeatServer);
		return stp;
	}
	public int getPort() {
		return this.port;
	}

	public void close(){
		svc.stopService();
	}
	public class MessageProcessorFactory extends TProcessorFactory{
		public MessageProcessorFactory() {
			super(null);
		}

		public TProcessor getProcessor(TTransport trans) {
			String rmtIp = null;
			int port = 0;
			if(trans instanceof TSocket){
				TSocket ts = (TSocket) trans;
				SocketAddress sa = ts.getSocket().getRemoteSocketAddress();

				if(sa instanceof InetSocketAddress){
					InetSocketAddress ia = (InetSocketAddress)sa;
					rmtIp = ia.getAddress().getHostAddress();
					port = ia.getPort();
				}
			}
			ActionDealor deal = new ActionDealor(rmtIp, port);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			TProcessor processor = new MessageService.Processor(deal); 


			return processor;
		}
	}
}