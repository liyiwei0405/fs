package com.funshion.gamma.atdd;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.LogHelper;

public abstract class AbstractThriftServerInner {
	public static final LogHelper log = new LogHelper("tServer");
	public final int port;
	public final int clientTimeout;
	protected TThreadPoolServer server;
	protected TServerSocket socket;
	private boolean atService = false;
	public AbstractThriftServerInner(int port, int clientTimeout) throws Exception{
		this.port = port;
		this.clientTimeout = clientTimeout;
	}
	
	public abstract TProcessorFactory getProcessorFacotry();
	public void startService() throws TTransportException{
		InetSocketAddress add = new InetSocketAddress(port);
		socket = new TServerSocket(add, clientTimeout); 
		TThreadPoolServer.Args args = new TThreadPoolServer.Args(socket);
		args.executorService(Executors.newFixedThreadPool(64));
		args.processorFactory(getProcessorFacotry());
		args.transportFactory(new TFastFramedTransport.Factory());
        args.protocolFactory(new TBinaryProtocol.Factory());
        
		server = new TThreadPoolServer(args); 
		log.warn("%s startTServerSocket using port %s", log.logName, port);
		atService = true;
		server.serve();
		atService = false;
	}
	public void close(){
		TThreadPoolServer server = this.server;
		if(server != null){
			server.stop();
			this.server = null;
		}
		TServerSocket socket = this.socket;
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
