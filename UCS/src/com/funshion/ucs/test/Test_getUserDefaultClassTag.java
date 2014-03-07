package com.funshion.ucs.test;

import java.util.ArrayList;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.funshion.ucs.thrift.UCS.Client;
import com.funshion.ucs.thrift.UserClassResult;

public class Test_getUserDefaultClassTag {
	
	private static Client getClient(TSocket skt, String ip, int port) throws Exception{
		System.out.println("connecting " + ip + ":" + port + " ...");
		skt.setTimeout(2000);
		skt.open();
		System.out.println("connected to " + ip + ":" + port + " ...");

		TFramedTransport trans = new TFramedTransport(skt);
		TBinaryProtocol prot = new TBinaryProtocol(trans);

		return new Client(prot);
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<String> clientType = new ArrayList<String>();
		clientType.add("ipad");
		clientType.add("iphone");
		clientType.add("apad");
		clientType.add("aphone");
		clientType.add("winpad");
		clientType.add("winphone");
		clientType.add("pc");
		clientType.add("mweb");

		TSocket javaSkt = new TSocket("127.0.0.1", 8102);
		Client javaClient = getClient(javaSkt, "127.0.0.1", 8102);

		TSocket nodeSkt = new TSocket("192.168.16.21", 8102);
		Client nodeClient = getClient(nodeSkt, "192.168.16.21", 8102);
		try{
			for(int i = 0; i < clientType.size(); i ++){
				UserClassResult javaRst = javaClient.getUserDefaultClassTag(clientType.get(i));
				UserClassResult nodeRst = nodeClient.getUserDefaultClassTag(clientType.get(i));
				if(javaRst.retCode != nodeRst.retCode){
					throw new Exception("retCode NOT EQUAL! java:" + javaRst + ", node:" + nodeRst);
				}else if(! javaRst.userClass.equals(nodeRst.userClass)){
					throw new Exception("userClass NOT EQUAL! java:" + javaRst + ", node:" + nodeRst);
				}else{
					System.out.println("OK. areaTactic:" + javaRst.userClass);
				}
			}
		}finally{
			javaSkt.close();
			nodeSkt.close();
		}
	}
}
