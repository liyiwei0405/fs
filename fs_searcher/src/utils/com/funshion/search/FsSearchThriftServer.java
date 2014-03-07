package com.funshion.search;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;

public abstract class FsSearchThriftServer {
	public static final LogHelper log = new LogHelper("sts");
	public final int port;
	public final int clientTimeout;
	protected TThreadedSelectorServer server;
	protected TNonblockingServerSocket socket;
	private boolean atService = false;
	public FsSearchThriftServer(ConfigReader cr) throws Exception{
		port = cr.getInt("svcPort", 0);
		clientTimeout = cr.getInt("clientTimeout", 5000);
	}
	
	public abstract TProcessorFactory getProcessorFacotry();
	public void startService() throws TTransportException{
		InetSocketAddress add = new InetSocketAddress(port);
		socket = new TNonblockingServerSocket(add, clientTimeout); 
		TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(socket);
		args.selectorThreads = 4;
		args.processorFactory(getProcessorFacotry());
		args.transportFactory(new TFastFramedTransport.Factory());
        args.protocolFactory(new TBinaryProtocol.Factory());
        
		args.executorService(Executors.newFixedThreadPool(128));
		server = new TThreadedSelectorServer(args); 
		log.warn("%s start watchTNonblockingServerer using port %s", log.logName, port);
		atService = true;
		server.serve();
		atService = false;
	}
	public void close(){
		TThreadedSelectorServer server = this.server;
		if(server != null){
			server.stop();
			this.server = null;
		}
		TNonblockingServerSocket socket = this.socket;
		if(socket != null){
			socket.close();
			this.socket = null;
		}
		atService = false;
	}
	public void finlize(){
		close();
	}

	public boolean isAtService() {
		return atService;
	}

}
