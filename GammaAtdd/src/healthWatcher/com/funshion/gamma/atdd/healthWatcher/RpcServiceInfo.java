package com.funshion.gamma.atdd.healthWatcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funshion.gamma.atdd.ResultComparator;
import com.funshion.gamma.atdd.ResultCompareDefault;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.utils.http.HttpCrawler;

public class RpcServiceInfo {
	LogHelper log = new LogHelper("rpcServerInfo");
	
	private List<RpcServerInstance>rpcServerInstances;
	Map<Method, List<NamedGammaTestCase>> testCaseMap;
	public final ControlThread control;
	
	private int reloadItvSecond = 60;
	public final long checkItv;
	public final String serviceNameInConfigCenter;
	public final String fullServiceClassName;
	public final ResultComparator resultComparator;
	
	final Class<?>serverClass;
	final ServiceTestCaseCrawler crawler;
	final MailTo mailTo;
	private int clientConnectTimeoutMs = 100;
	private int clientReadTimeoutMs = 400;
	private int failRetryTimes = 3;
	private int failRetryInterval = 500;

	public RpcServiceInfo(ConfigReader cr) throws Exception{
		log = new LogHelper(cr.sectionName);
		checkItv = cr.getInt("checkItvSecond", 1) * 1000L;
		String resultComparatorName = cr.getValue("resultComparator", "");
		if(resultComparatorName.length() == 0){
			resultComparator = new ResultCompareDefault();
		}else{
			Class<?> resultComparatorClass = Class.forName(resultComparatorName);
			resultComparator = (ResultComparator) resultComparatorClass.newInstance();
		}
		this.fullServiceClassName = cr.getValue("service-name");
		serverClass = Class.forName(this.fullServiceClassName);
		this.serviceNameInConfigCenter = cr.sectionName;
		this.clientConnectTimeoutMs = cr.getInt("clientConnectTimeoutMs", 100);
		this.clientReadTimeoutMs = cr.getInt("clientReadTimeoutMs", 400);
		this.failRetryTimes = cr.getInt("failRetryTimes", 3);
		this.failRetryInterval = cr.getInt("failRetryInterval", 500);
		crawler = new ServiceTestCaseCrawler(fullServiceClassName, 
				MailConfig.instance.getTestCaseFolder());
		control = new ControlThread();
		MailTo mailTo;
		try{
			mailTo = MailTo.get(cr);
		}catch(Exception e){
			mailTo = MailConfig.instance.adminMailAddress;
			log.error(e, "mail config Error when load config %s", cr.sectionName);
		}
		this.mailTo = mailTo;
		log.debug("mailTo info %s", mailTo);

	}

	class ControlThread extends Thread{
		boolean isRunning = true;
		@Override
		public void run(){
			while(true){
				try {
					initEnviroment();
					log.info("init enviroment for %s:%s", fullServiceClassName,serverClass);
					break;
				} catch (Exception e) {
					log.error(e, "while init enviroment for %s:%s", fullServiceClassName, fullServiceClassName);
					e.printStackTrace();
				}
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			int minSleepMs = 4;
			int sleepTimes = reloadItvSecond * 1000 / minSleepMs;
			while(isRunning){
				for(int x = 0; x < sleepTimes && isRunning; x ++){
					try {
						sleep(minSleepMs);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				synchronized(this){
					if(this.isRunning){
						try{
							mergeNewRpcServiceInstance();
						}catch(Exception e){
							log.error(e, "while mergeNewRpcServiceInstance for %s", fullServiceClassName);
						}

						try{
							mergeTestCases();
						}catch(Exception e){
							log.error(e, "while reload testcases for %s", fullServiceClassName);
						}
					}
				}
			}
		}
		private void initEnviroment()throws Exception {
			testCaseMap = crawler.loadServiceTestCasesFromDisk();
			rpcServerInstances = getServerInstances();
			for(RpcServerInstance ins : rpcServerInstances){
				ins.mergeServiceTestCases(testCaseMap);
			}
		}
		
		private void mergeNewRpcServiceInstance() throws Exception {
			List<RpcServerInstance>newInstances = getServerInstances();
			mergeServerInstance(newInstances);
		}

		private void mergeServerInstance(List<RpcServerInstance>newLst) throws Exception{
			List<RpcServerInstance>toAdd = new ArrayList<RpcServerInstance>();
			List<RpcServerInstance>toRemove = new ArrayList<RpcServerInstance>();
			for(RpcServerInstance ins : rpcServerInstances){
				if(!newLst.contains(ins)){
					toRemove.add(ins);
				}
			}
			for(RpcServerInstance ins : newLst){
				if(!rpcServerInstances.contains(ins)){
					toAdd.add(ins);
				}
			}
			for(RpcServerInstance ins : toRemove){
				ins.stopCheck();
				boolean isRem = rpcServerInstances.remove(ins);
				log.warn("remove RpcServer %s for %s : %s ", ins, serviceNameInConfigCenter, isRem);
			}
			for(RpcServerInstance ins : toAdd){
				rpcServerInstances.add(ins);
				ins.mergeServiceTestCases(testCaseMap);
				log.warn("add new  %s for %s", ins, serviceNameInConfigCenter);
			}
		}
		
		private void mergeTestCases() throws Exception {
			testCaseMap = crawler.loadServiceTestCasesFromDisk();
			for(RpcServerInstance ins : rpcServerInstances){
				ins.mergeServiceTestCases(testCaseMap);
			}
		}
		
		public void stopRunFlag() {
			isRunning = false;
			killMethodThreads();
		}
		private void killMethodThreads() {
			synchronized(this){
				for(RpcServerInstance ins : rpcServerInstances){
					ins.stopCheck();
				}
			}
		}
	}

	private List<RpcServerInstance> getServerInstances() throws Exception {
		List<ServiceInstanceLocationInfo> locationInfos =  getServerInstanceLocations();
		List<RpcServerInstance> instances = new Vector<RpcServerInstance>();
		for(ServiceInstanceLocationInfo locationInfo : locationInfos){
			RpcServerInstance inst = new RpcServerInstance(locationInfo, this);
			inst.setCheckItv(checkItv);
			instances.add(inst);
			//FIXME
		}
		return instances;
	}
	public List<ServiceInstanceLocationInfo> getServerInstanceLocations() throws IOException {
		URL url = new URL(MailConfig.instance.getConfigCenterBaseUrl() + this.serviceNameInConfigCenter);
		HttpCrawler crl = new HttpCrawler(url);
		crl.connect();
		List<ServiceInstanceLocationInfo> infos = new ArrayList<ServiceInstanceLocationInfo>();
		try{
			byte [] bs = crl.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(bs);

			ObjectMapper objectMapper = new ObjectMapper();    
			RpcLocationsFromConfigCenter retInfo = objectMapper.readValue(bais, RpcLocationsFromConfigCenter.class);
			if(retInfo.getRetCode() != 200){
				log.warn("got error retCode %s:%s for service name %s ", 
						retInfo.getRetCode(), retInfo.getRetMsg(),
						this.serviceNameInConfigCenter
						);
			}else{
				for(Iterator<ServiceInstanceLocationInfo>itr = retInfo.getResult().getServerInstances().iterator(); 
						itr.hasNext();){
					ServiceInstanceLocationInfo locInfo = itr.next();
					if(locInfo.isReady()){
						infos.add(locInfo);
					}else{
						log.warn("%s on %s not ready, ", this.serviceNameInConfigCenter, locInfo.getIp());
					}
				}
			}
			return infos;
		}finally{
			crl.close();
		}
	}
	
	public List<RpcServerInstance> getInstances(){
		return this.rpcServerInstances;
	}
	
	@Override
	public int hashCode(){
		return this.fullServiceClassName.hashCode() & this.serviceNameInConfigCenter.hashCode();
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof RpcServiceInfo){
			RpcServiceInfo other = (RpcServiceInfo) o;
			return this.fullServiceClassName.equals(other.fullServiceClassName) && 
					this.serviceNameInConfigCenter.equals(other.serviceNameInConfigCenter);
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "RpcServiceInfo: sevice name '" + this.serviceNameInConfigCenter + " with className " + this.fullServiceClassName; 
	}
	public void stop() {
		control.stopRunFlag();

	}
	public void start() {
		control.start();
	}

	public int getClientConnectTimeoutMs() {
		return clientConnectTimeoutMs;
	}

	public void setClientConnectTimeoutMs(int clientTimeoutMs) {
		this.clientConnectTimeoutMs = clientTimeoutMs;
	}


	public int getClientReadTimeoutMs() {
		return clientReadTimeoutMs;
	}


	public void setClientReadTimeoutMs(int clientReadTimeoutMs) {
		this.clientReadTimeoutMs = clientReadTimeoutMs;
	}
	
	public int getFailRetryTimes() {
		return failRetryTimes;
	}

	public void setFailRetryTimes(int failRetryTimes) {
		this.failRetryTimes = failRetryTimes;
	}
	
	public int getFailRetryInterval() {
		return failRetryInterval;
	}

	public void setFailRetryInterval(int failRetryInterval) {
		this.failRetryInterval = failRetryInterval;
	}
	
	public List<RpcServerInstance> getRpcServerInstances(){
		return this.rpcServerInstances;
	}
}
