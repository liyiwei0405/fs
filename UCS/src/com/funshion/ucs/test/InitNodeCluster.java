package com.funshion.ucs.test;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.ucs.thrift.UCS.Client;
import com.funshion.ucs.thrift.UcsCondition;

public class InitNodeCluster {

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
		ConfigReader cr = new ConfigReader(ConfUtils.getConfFile("ucs.conf"), "client");

		//				TSocket javaSkt = new TSocket(cr.getValue("ip"), cr.getInt("port"));
		//				Client javaClient = getClient(javaSkt, cr.getValue("ip"), cr.getInt("port"));
		TSocket nodeSkt = new TSocket("192.168.16.21", 8102);
		Client nodeClient = getClient(nodeSkt, "192.168.16.21", 8102);

		//				System.out.println(javaClient.getUcsObject(new UcsCondition("pc","1.2.3.1","","",0,"192.168.16.212",0)));
		System.out.println(nodeClient.getUcsObject(new UcsCondition("pc","1.2.3.1","","",0,"192.168.16.212",0))); 
	}

}
