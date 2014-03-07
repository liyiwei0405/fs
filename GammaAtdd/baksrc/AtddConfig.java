package com.funshion.gamma.atdd;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
//import java.lang.reflect.Constructor;

@SuppressWarnings("rawtypes")
public class AtddConfig {
	
	
	
	public final String section ;
	public final Class<QueryableClient> clientClass;
	public final InputGennor inputGennor;
	public final ResultComparator resultComparator;
	public final ThriftClientInfo atddClient, serverClient;
	@SuppressWarnings("unchecked")
	public AtddConfig(String section) throws Exception{
		this.section = section;

		ConfigReader cr = new ConfigReader(new File("./config/atdd.conf"), section);
		
		String clientClassName = cr.getValue("clientClass");
		this.clientClass = (Class<QueryableClient>) Class.forName(clientClassName);
		

		String inputGennorName = cr.getValue("inputGennor");

		Class inputGennorClass = Class.forName(inputGennorName);
		inputGennor = (InputGennor) inputGennorClass.newInstance();

		String resultComparatorName = cr.getValue("resultComparator", "");
		if(resultComparatorName.length() == 0){
			resultComparator = new ResultCompareDefault();
		}else{
			Class resultComparatorClass = Class.forName(resultComparatorName);
			resultComparator = (ResultComparator) resultComparatorClass.newInstance();
		}

		String serverHost = cr.getValue("serverHost");
		int serverPort = cr.getInt("serverPort", 0);
		if(serverHost == null || serverPort == 0){
			throw new Exception("serverHost or serverPort not set properly");
		}
		serverClient = new ThriftClientInfo(serverHost, serverPort, clientClass);
		
		String atddHost = cr.getValue("atddHost");
		int atddPort = cr.getInt("atddPort");
		if(atddHost == null || atddPort == 0){
			throw new Exception("atddHost or atddPort not set properly");
		}
		atddClient = new ThriftClientInfo(atddHost, atddPort, clientClass);
		System.out.println("config load ready! for " + this.section);
	}

	
	
	public void runConfig() {
		int runnedQuery = 0, exceptionQuery = 0;
		while(inputGennor.hasNext()){
			runnedQuery ++;
			try {
				query();
			} catch (Exception e) {
				exceptionQuery ++;
				e.printStackTrace();
				try {
					Consoler.readString("press Enter to continue...");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
			if(runnedQuery % 100 == 0){
				System.out.println("run query " + runnedQuery + ", exception " + exceptionQuery + ", fail Exe" + ((int)(exceptionQuery * 100000.0) / runnedQuery)/ 1000.0 + "%");
			}
		}
		System.out.println("finally ...");
		if(runnedQuery % 100 == 0){
			System.out.println("run query " + runnedQuery + ", exception " + exceptionQuery + ", fail Exe" + ((int)(exceptionQuery * 100000.0) / runnedQuery)/ 1000.0 + "%");
		}
	}
	
	public void query() throws Exception{
		Object oAtdd = atddClient.getClient().client.queryAtdd(this.inputGennor.nextAtddQuery());
		Object oServer = this.serverClient.getClient().client.queryServer(this.inputGennor.nextServerQuery());
		
		this.resultComparator.compare(inputGennor, oServer, oAtdd);
	}
	public String configInfo(){

		String format = "AtddConfig for section %s\n" +
				" serverHost = %s, serverPort = %s\n" +
				" atddHost= %s, atddPort = %s\n" +
				" inputGennor = %s\n" +
				" resultComparator = %s";
		
		return String.format(format, 
				section,
				this.serverClient.host, this.serverClient.port,
				this.atddClient.host, this.atddClient.port,
				inputGennor,
				resultComparator);
	}
}
