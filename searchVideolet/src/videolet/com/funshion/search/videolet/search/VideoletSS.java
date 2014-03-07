package com.funshion.search.videolet.search;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.BusinessType;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;

public class VideoletSS {

	public static final LogHelper log = new LogHelper(BusinessType.VideoletSearch);
	public final int port;
	public final int clientTimeout;
	THsHaServer server;
	TNonblockingServerSocket socket;
	public VideoletSS(ConfigReader cr) throws Exception{
		port = cr.getInt("svcPort", 0);
		clientTimeout = cr.getInt("clientTimeout", 20000);
	}
	public void startService() throws TTransportException{
		InetSocketAddress add = new InetSocketAddress(port);
		socket = new TNonblockingServerSocket(add, clientTimeout); 
		THsHaServer.Args args = new THsHaServer.Args(socket);
		args.processorFactory(VideoletQueryIfaceProcessorFactory.instance);
		
		args.executorService(Executors.newFixedThreadPool(200));
		server = new THsHaServer(args); 
		log.warn("%s start watchTNonblockingServerer using port %s",
				log.logName, port);
		server.serve();
	}
	public void close(){
		TNonblockingServer server = this.server;
		if(server != null){
			server.stop();
			this.server = null;
		}
		TNonblockingServerSocket socket = this.socket;
		if(socket != null){
			socket.close();
			this.socket = null;
		}
	}
	public void finlize(){
		close();
	}
}
