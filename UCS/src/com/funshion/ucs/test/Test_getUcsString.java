package com.funshion.ucs.test;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.funshion.ucs.thrift.UCS.Client;
import com.funshion.ucs.thrift.UcsStringResult;

public class Test_getUcsString {
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
		TSocket javaSkt = new TSocket("127.0.0.1", 8102);
		Client javaClient = getClient(javaSkt, "127.0.0.1", 8102);

		TSocket nodeSkt = new TSocket("192.168.16.21", 8102);
		Client nodeClient = getClient(nodeSkt, "192.168.16.21", 8102);

		try{
			for(int i = 0 ; i < TestCase.conditionList.size(); i ++){
				System.out.println("test "+ (i+1));
				
				UcsStringResult javaRst = javaClient.getUcsString(TestCase.conditionList.get(i));
				String javaString = Decryption.decrypt(javaRst.ucsString);
				UcsStringResult nodeRst = nodeClient.getUcsString(TestCase.conditionList.get(i));
				String nodeString = Decryption.decrypt(nodeRst.ucsString);

				if(javaRst.retCode != nodeRst.retCode || ! javaString.equals(nodeString)){
					throw new Exception("NOT EQUAL! java:" + javaRst + ", node:" + nodeRst);
				}else{
					System.out.println("OK. javaRst:" + javaRst);
				}
			}
		}finally{
			javaSkt.close();
			nodeSkt.close();
		}
	}

}
