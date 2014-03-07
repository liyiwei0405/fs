package com.funshion.luc.defines;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.funshion.retrieve.media.thrift.MediaSearchService;

public class SSQueryIfaceProcessorFactory extends TProcessorFactory{
	public static final SSQueryIfaceProcessorFactory instance = new SSQueryIfaceProcessorFactory();
	private SSQueryIfaceProcessorFactory() {
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
		
		SSQueryIface deal = new SSQueryIface(rmtIp, port);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		TProcessor processor = new MediaSearchService.Processor(deal); 

		return processor;
	}


}