package com.funshion.search;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;

public abstract class FsSearchThriftServer {
	public static final LogHelper log = new LogHelper("thriftServer");
	public final int port;
	public final int clientTimeout;
	protected TThreadPoolServer server;
	protected TServerSocket socket;
	private boolean atService = false;
	public FsSearchThriftServer(ConfigReader cr) throws Exception{
		this(cr.getInt("svcPort", 0), cr.getInt("clientTimeout", 5000));
	}
	public FsSearchThriftServer(int port, int clientTimeout) throws Exception{
		this.port = port;
		this.clientTimeout = clientTimeout;
	}
	public abstract TProcessorFactory getProcessorFacotry();
	public void startService() throws TTransportException{
		InetSocketAddress add = new InetSocketAddress(port);
		socket = new TServerSocket(add, clientTimeout); 
		TThreadPoolServer.Args args = new TThreadPoolServer.Args(socket);
		args.processorFactory(getProcessorFacotry());
		args.transportFactory(new TFastFramedTransport.Factory());
        args.protocolFactory(new TBinaryProtocol.Factory());
        
		args.executorService(Executors.newFixedThreadPool(128));
		server = new TThreadPoolServer(args); 
		log.warn("%s start Server using port %s", log.logName, port);
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
