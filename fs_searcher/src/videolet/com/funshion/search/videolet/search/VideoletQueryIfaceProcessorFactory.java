package com.funshion.search.videolet.search;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.funshion.search.videolet.thrift.QueryVideolet;


public class VideoletQueryIfaceProcessorFactory extends TProcessorFactory{
	public static final VideoletQueryIfaceProcessorFactory instance = new VideoletQueryIfaceProcessorFactory();
	private VideoletQueryIfaceProcessorFactory() {
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
		
		VideoletQueryIface deal = new VideoletQueryIface(rmtIp, port);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		TProcessor processor = new QueryVideolet.Processor(deal); 

		return processor;
	}


}