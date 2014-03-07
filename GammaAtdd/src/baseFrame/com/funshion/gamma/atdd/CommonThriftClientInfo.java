package com.funshion.gamma.atdd;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.funshion.search.utils.LogHelper;

//通用thrift client类
public class CommonThriftClientInfo{
	static final LogHelper clog = new LogHelper("client");
	/**static LinkedBlockingQueue<Integer>queue;
	static{
		queue = new  LinkedBlockingQueue<Integer>();
		for(int x = 10000; x < 60000; x ++){
			queue.add(x);
		}
	}**/
	public class ClientInfo{
		private TTransport socket;
		final TProtocol protocol;
		final TFramedTransport trans;
		public final org.apache.thrift.TServiceClient client;
		final InetSocketAddress bindAddress;
		Integer portLocal;
		final Socket skt;
		private ClientInfo(InetSocketAddress bindAddress) throws Exception{
			long st = System.currentTimeMillis();
			//			portLocal = queue.take();
			//			this.bindAddress = new InetSocketAddress("192.168.16.31", portLocal);
			this.bindAddress = bindAddress;
			skt = new Socket();
			try{
				if(bindAddress != null){
					skt.bind(bindAddress);
				}
				skt.setSoLinger(false, 0);
				skt.setTcpNoDelay(true);
				InetSocketAddress sa = new InetSocketAddress(InetAddress.getByName(host), port);
				//连接
				skt.connect(sa, getConnectTimeout());
				skt.setSoTimeout(getReadTimeoutMs());
				socket = new TSocket(skt); 

			}catch(Exception e){
				throw new Exception(
						String.format("fail connect to %s:%s, timeout %s, usedMs %s, root cause: %s", 
								host, port, connectTimeout,
								System.currentTimeMillis() - st, e.getMessage()), e);
			}
			clog.debug("ok connect to %s:%s, timeoutMs %s, real used %s", host, port, connectTimeout,
					System.currentTimeMillis() - st);
			trans = new TFramedTransport(socket);
			protocol = new TBinaryProtocol(trans);
			Constructor<?> cons = serviceClientClass.getConstructor(org.apache.thrift.protocol.TProtocol.class);
			client = (TServiceClient) cons.newInstance(protocol);
		}

		public void close(){
			if(this.socket != null){
				this.socket.close();
				this.socket = null;
			}
			//			synchronized(this){
			//				if(portLocal != 0){
			//					queue.add(portLocal);	
			//					portLocal = 0;
			//				}
			//			}
		}
		@Override
		public void finalize(){
			if(socket != null){
				clog.debug("%s:%s finalize! to close socket %s", host, port, socket);
				close();
			}
		}
	}
	public final String host;
	public final int port;
	public final String serviceName;
	public final Class<?> serviceClientClass;
	private int readTimeoutMs = 1000;
	private int connectTimeout = 1000;
	public CommonThriftClientInfo(String host, int port,
			String serviceName) throws Exception{
		this(host, port, serviceName, 1000, 1000);
	}
	public CommonThriftClientInfo(String host, int port,
			String serviceName, int connectTimeout, int readTimeout) throws Exception{
		this.host = host;
		this.port = port;
		this.readTimeoutMs = readTimeout;
		this.connectTimeout = connectTimeout;
		this.serviceName = serviceName;
		if(host == null || port == 0){
			throw new Exception("serverHost or serverPort not set properly");
		}
		String clientName = serviceName + "$Client";
		serviceClientClass = Class.forName(clientName);
	}

	@Override
	public String toString(){
		return String.format("client connect to %s:%s\n\tfor service %s", host, port, serviceName);
	}
	public ClientInfo getNewClient() throws Exception{
		return getNewClient(null);
	}
	public ClientInfo getNewClient(InetSocketAddress bindAddress) throws Exception{
		return new ClientInfo(bindAddress);
	}
	public ClientInfo getNewClientWithFail(int maxRetryTimes) throws Exception{
		Exception exc = null;
		int x = 0;
		for(; x < maxRetryTimes; x ++){

			try {
				return getNewClient();
				
			} catch (Exception e) {
				exc = e;
				clog.warn(e, "try connect fail, allow retry times : %s, now is %s",  maxRetryTimes, x + 1);
			}
		}

		throw new Exception(
				"connect fail with fail allow " + maxRetryTimes + ", now is " + x + ", last exception info:" + 
				(exc == null ? new Exception("MAY ERROR! NULL exception pointer") : exc)
		);

	}
	public int getReadTimeoutMs() {
		return readTimeoutMs;
	}
	public void setReadTimeout(int timeoutMs) {
		this.readTimeoutMs = timeoutMs;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
}

