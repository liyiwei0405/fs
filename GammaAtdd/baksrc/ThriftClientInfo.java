//package com.funshion.gamma.atdd;
//
//import org.apache.thrift.protocol.TBinaryProtocol;
//import org.apache.thrift.protocol.TProtocol;
//import org.apache.thrift.transport.TFramedTransport;
//import org.apache.thrift.transport.TSocket;
//import org.apache.thrift.transport.TTransport;
//import org.apache.thrift.transport.TTransportException;
//
//class ThriftClientInfo{
//		class ClientInfo{
//			TTransport socket;
//			TProtocol protocol;
//			TFramedTransport trans;
//			QueryableClient client;
//			public ClientInfo() throws Exception{
//				getProtocol();
//				client = clientClass.newInstance();
//				client.setProtocol(this.protocol);
//			}
//			private TProtocol getProtocol() throws TTransportException{
//				socket = new TSocket(host, port, 3000); 
//				socket.open(); 
//				trans = new TFramedTransport(socket);
//				protocol = new TBinaryProtocol(trans); 
//				return protocol;
//			}
//			
//		}
//		public final String host;
//		public final int port;
//		public final Class<QueryableClient> clientClass;
//		ClientInfo clientInfo;
//		ThriftClientInfo(String host, int port,
//				Class<QueryableClient> clientClass){
//			this.host = host;
//			this.port = port;
//			this.clientClass = clientClass;
//		}
//		
//		public synchronized ClientInfo getClient() throws Exception{
//			if(this.clientInfo == null){
//				this.clientInfo = new ClientInfo();
//			}
//			return clientInfo;
//		}
//		
//	}