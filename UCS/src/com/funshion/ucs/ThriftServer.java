package com.funshion.ucs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.ucs.thrift.UCS;

public class ThriftServer {
	private final int port;
	private final int clientTimeout;
	private final int SELECTOR_THREADS;
	private final int NEW_FIXED_THREAD;
	private TThreadedSelectorServer server;
	private TNonblockingServerSocket socket;
	private boolean atService = false;
	private final LogHelper log = new LogHelper("thrift");

	public ThriftServer(ConfigReader cr) throws Exception{
		port = cr.getInt("servicePort", 0);
		clientTimeout = cr.getInt("clientTimeout", 5000);
		SELECTOR_THREADS = cr.getInt("SELECTOR_THREADS", 4);
		NEW_FIXED_THREAD = cr.getInt("NEW_FIXED_THREAD", 64);
	}

	public void startService() throws TTransportException, IOException{
		@SuppressWarnings({ "rawtypes", "unchecked" })
		TProcessor processor = new UCS.Processor(new UCSImpl()); 

		InetSocketAddress add = new InetSocketAddress(port);
		socket = new TNonblockingServerSocket(add, clientTimeout); 
		TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(socket);
		args.selectorThreads = SELECTOR_THREADS;
		args.processorFactory(new TProcessorFactory(processor));
		args.transportFactory(new TFastFramedTransport.Factory());
		args.protocolFactory(new TBinaryProtocol.Factory());
		args.acceptQueueSizePerThread(64);
		args.executorService(Executors.newFixedThreadPool(NEW_FIXED_THREAD));
		server = new TThreadedSelectorServer(args); 
		log.warn("start TNonblockingServer using port %s", port);
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
