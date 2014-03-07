package com.funshion.search.utils.systemWatcher;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.transport.TTransportException;

public class SystemWatcherShutdownTestServer {
	static int port;
	static final String sysName = "SystemWatcherTest";
	static SystemWatcher sw;

	public static void initTest() throws TTransportException, IOException{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
		sw = new SystemWatcher(port, sysName);
	}

	public static void main(String[]args) throws TTransportException, IOException{
		port = Integer.parseInt(args[0]);
		System.out.println("server port " + port);
		initTest();
	}
}
