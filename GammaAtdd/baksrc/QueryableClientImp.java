//package com.funshion.gamma.atdd;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//
//import org.apache.thrift.TServiceClient;
//import org.apache.thrift.protocol.TProtocol;
//
//public abstract class QueryableClientImp extends QueryableClient{
//	protected final org.apache.thrift.TServiceClient atddClient;
//	protected final org.apache.thrift.TServiceClient serverClient;
//	public QueryableClientImp(Class clsAtdd,
//			Class clsServer) throws Exception{
//		Constructor<TServiceClient>  cAtdd = clsAtdd.getConstructor(TProtocol.class);
//		atddClient = cAtdd.newInstance(this.protocol);
//		
//		Constructor<TServiceClient>  cServer = clsServer.getConstructor(TProtocol.class);
//		serverClient = cServer.newInstance(this.protocol);
//	}
//	
//
//}
