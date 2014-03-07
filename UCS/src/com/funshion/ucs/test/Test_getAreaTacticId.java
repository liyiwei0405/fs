package com.funshion.ucs.test;

import java.util.ArrayList;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.funshion.ucs.thrift.AreaTacticResult;
import com.funshion.ucs.thrift.UCS.Client;

public class Test_getAreaTacticId {
	
	private static Client getClient(TSocket skt, String ip, int port) throws Exception{
		System.out.println("connecting " + ip + ":" + port + " ...");
		skt.setTimeout(2000);
		skt.open();
		System.out.println("connected to " + ip + ":" + port + " ...");

		TFramedTransport trans = new TFramedTransport(skt);
		TBinaryProtocol prot = new TBinaryProtocol(trans);

		return new Client(prot);
	}

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

		ArrayList<String> area = new ArrayList<String>();
		area.add("海外");
		area.add("英国");
		area.add("美国");
		area.add("美国");
		area.add("长沙");
		area.add("上海");
		area.add("广州");
		area.add("深圳");

		TSocket javaSkt = new TSocket("127.0.0.1", 8102);
		Client javaClient = getClient(javaSkt, "127.0.0.1", 8102);

		TSocket nodeSkt = new TSocket("192.168.16.21", 8102);
		Client nodeClient = getClient(nodeSkt, "192.168.16.21", 8102);
		try{
			for(int i = 0; i < clientType.size(); i ++){
				AreaTacticResult javaRst = javaClient.getAreaTacticId(clientType.get(i), area.get(i));
				AreaTacticResult nodeRst = nodeClient.getAreaTacticId(clientType.get(i), area.get(i));
				if(javaRst.retCode != nodeRst.retCode || javaRst.areaTactic != nodeRst.areaTactic){
					throw new Exception("NOT EQUAL! java:" + javaRst + ", node:" + nodeRst);
				}else{
					System.out.println("OK. areaTactic:" + javaRst.areaTactic);
				}
			}
		}finally{
			javaSkt.close();
			nodeSkt.close();
		}
	}
}

