package com.funshion.videoService.search;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.funshion.luc.defines.IndexChannelImp;
import com.funshion.videoService.thrift.VideoService;
import com.funshion.videoService.thrift.VideoService.Processor;


public class VideoServiceProcessorFactory extends TProcessorFactory{
	IndexChannelImp proxy;
	public VideoServiceProcessorFactory(IndexChannelImp proxy) {
		super(null);
		this.proxy = proxy;
	}
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		VideoServiceIfaceImp deal = new VideoServiceIfaceImp(rmtIp, port, proxy);
		
		Processor processor = new VideoService.Processor(deal); 

		return processor;
	}


}