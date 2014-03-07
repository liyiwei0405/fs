package com.funshion.rpcserver;

import java.io.File;
import java.net.InetSocketAddress;

import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;


public class CounterDaemon {
	static TaskCounterRefresher instance = TaskCounterRefresher.instance;
	/**
	 * @param args
	 * @throws TTransportException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		InetSocketAddress add = new InetSocketAddress(4321);
		TServerSocket socket = new TServerSocket(add, 5000); 
		TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(socket);

		instance.init(80, tArgs, new File[]{});
		int[] itvs = new int[]{500, 1000, 1500};
		instance.registerCounter("getUcsObject", itvs);
		instance.registerCounter("getAreaTacticId", itvs);
		
		instance.registerVars_long("map' s size");
		instance.registerVars_string("map' s name", "abc");

		//随机inc
		new Thread(){
			@Override
			public void run(){
				try {
					while(true){
						int mRand = (int) Math.floor(Math.random() * 3);
						if(mRand == 1){
							mRand = (int) Math.floor(Math.random() * 3);
						}
						String name = Math.random() > 0.5 ? "getUcsObject" : "getAreaTacticId";
						try {
							if(mRand == 0){
								instance.incOkServed(name, (long) (Math.random() * 100000));
								instance.incServed(name);
							}else if(mRand == 1){
								instance.incFail(name, (long) (Math.random() * 100000));
								instance.incServed(name);
							}else{
								instance.incVars_long("map' s size", (int)(Math.random() * 10));
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally{
							Thread.sleep(2000);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
	}
}
