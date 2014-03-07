package com.funshion.search.utils.systemWatcher.heartBeat;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

public class HeartBeatCheckorTest {
	static{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
	}
	static String BusinessTypesNone = "BusinessTypesNone";
	static String BusinessTypes[] = new String[]{ 
		"BusinessType1",
		"BusinessType2",
		"BusinessType3",
		"BusinessType4",
		"BusinessType5",
		"BusinessType6",
		"BusinessType7",
		"BusinessType8"
	};

	HeartBeatCheckor test = HeartBeatCheckor.instance;

	@Test
	public void testTimeout() throws InterruptedException{
		ISNHBInfo clt = new ISNHBInfo("127.0.0.1", 1521, BusinessTypes[0]);
		long newSession = test.heartBeat(clt, 100);
		Thread.sleep(3001);
		assertEquals("lst size not match", test.getClients(clt.businessType).size(), 0);
		
		clt = new ISNHBInfo("127.0.0.1", 1521, BusinessTypes[0]);
		newSession = test.heartBeat(clt, newSession);
		Thread.sleep(1700);
		assertEquals("lst size not match", test.getClients(clt.businessType).size(), 1);
		
		clt = new ISNHBInfo("127.0.0.1", 1521, BusinessTypes[0]);
		newSession = test.heartBeat(clt, newSession);
		assertEquals("lst size not match", test.getClients(clt.businessType).size(), 1);
		
		Thread.sleep(1500);
		assertEquals("lst size not match", test.getClients(clt.businessType).size(), 1);

		Thread.sleep(2000);
		assertEquals("lst size not match", test.getClients(clt.businessType).size(), 0);
	}

	boolean stopGenHB = false;
	class GenHeartBeat extends Thread{
		final String business;
		final int randBase = 1000;
		final long session;
		final int port ;
		final String ip;
		public GenHeartBeat(String ip, String b, int port){
			this.business = b;
			this.ip = ip;
			this.port = port;
			this.session = (long) (Integer.MAX_VALUE * Math.random());
		}
		public void run(){
			long session = this.session;
			while(!stopGenHB){
				ISNHBInfo clt = new ISNHBInfo(ip, port, business);
				session = test.heartBeat(clt, session);
				try {
					sleep(randBase);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Test
	public void testMultiClient() throws InterruptedException{
		Thread ts [] = new Thread[]{
				new GenHeartBeat("192.168.1.2", BusinessTypes[1], 1001),
				new GenHeartBeat("192.168.1.3", BusinessTypes[2], 1002),
				new GenHeartBeat("192.168.1.4", BusinessTypes[1], 1003),
				new GenHeartBeat("192.168.1.5", BusinessTypes[3], 1004),
				new GenHeartBeat("192.168.1.6", BusinessTypes[1], 1001),
				new GenHeartBeat("192.168.1.6", BusinessTypes[1], 1001),
				
				new GenHeartBeat("192.168.1.3", BusinessTypes[2], 1002),
				new GenHeartBeat("192.168.1.5", BusinessTypes[3], 1005),
				new GenHeartBeat("192.168.1.5", BusinessTypes[3], 1005),
		};
		for(Thread t : ts){
			t.start();
		}
		ISNHBInfo clt = new ISNHBInfo("127.0.0.1", 1521, BusinessTypes[0]);
		test.heartBeat(clt, 100);
		assertEquals("lst size not match", test.getClients(clt.businessType).size(), 1);
		Thread.sleep(5000);
		assertEquals("lst size not match", test.getClients(clt.businessType).size(), 0);
		
		assertEquals("BusinessTypes[1] expect 4 client", 3 ,  test.getClients(BusinessTypes[1]).size());
		assertEquals("BusinessTypes[2] expect 1 client", 1 ,  test.getClients(BusinessTypes[2]).size());
		assertEquals("BusinessTypes[3] expect 2 client", 2 ,  test.getClients(BusinessTypes[3]).size());
		assertEquals(BusinessTypesNone + " expect 0 client", 0 ,  test.getClients(BusinessTypesNone).size());
	}
}
