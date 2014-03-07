package com.funshion.gamma.atdd.healthWatcher;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.funshion.gamma.atdd.CommonThriftClientInfo.ClientInfo;
import com.funshion.gamma.atdd.MethodAtddCommonThriftClient;
import com.funshion.gamma.atdd.TestStatistics;
import com.funshion.search.utils.LogHelper;

class RpcServerInstance{

	public class MethodInfo implements Runnable{
		public class TestCaseRunnor{
			public final int maxTime = 1000;
			boolean isRun = false;
			public TestCaseRunnor(){
				swapStatics();
			}

			public void testNext(MethodAtddCommonThriftClient client) throws Exception{
				NamedGammaTestCase testCase = testQueue.take();
				isRun = true;
				if(testCase != null){
					try{
						test(testCase, client);
					}finally{
						testQueue.putBack(testCase);
					}
				}
			}

			public void test(NamedGammaTestCase tCase, MethodAtddCommonThriftClient client) throws Exception {
				long st = System.currentTimeMillis();
				Object newRet;
				int retriedTimes = 0;
				Exception lastException = null;
				StringBuilder exps = new StringBuilder();
				for(retriedTimes = 0; retriedTimes < rpcInfo.getFailRetryTimes(); retriedTimes ++){
					ClientInfo clientInfo = null;
					try{
						clientInfo = client.getNewClient();
						newRet = client.query(tCase.queryParas(), null);
						if(rpcInfo.resultComparator != null){
							try{
								rpcInfo.resultComparator.compare(tCase, newRet);
							}catch(Exception e){
								getStatics().addCompareFail(e);
								throw e;
							}
						}
						getStatics().addOkTest(System.currentTimeMillis() - st);
						return;
					}catch(Exception e){
						lastException = e;
						if(e instanceof java.lang.reflect.InvocationTargetException){
							exps.append(++ retriedTimes + ": Socket Read timed out\n");
						}else{
							exps.append(++ retriedTimes + ": " + e.getMessage() + "\n");
						}
						-- retriedTimes;
						log.error(e, "FailHealthCheck!!! allow retry times : %s, now is %s", rpcInfo.getFailRetryTimes(), retriedTimes + 1);
						getStatics().addException(e);

						StackTraceElement[] frames = e.getStackTrace();
						StringBuilder sb = new StringBuilder();
						for(StackTraceElement f : frames){
							sb.append(f.toString() + "\n");
						}
						log.info(sb.toString());
					}finally{
						if(clientInfo != null){
							clientInfo.close();
						}
						Thread.sleep(rpcInfo.getFailRetryInterval());
					}
				}
				if(retriedTimes >=  rpcInfo.getFailRetryTimes()){
					try {
						schedule.sendBufferableMail(
								"Fail " +  rpcInfo.getFailRetryTimes() + " times, exceptions:\n" + exps + " last one as below:\n", lastException, 
								locationInfo, rpcInfo, rpcInfo.mailTo,
								method, tCase);
					} catch (Exception e) {
						log.fatal(e, "when send testMail");
						e.printStackTrace();
					}
				}
			}

			public TestStatistics getStatics() {
				return statics;
			}
			private TestStatistics statics;

			public TestStatistics swapStatics() {
				TestStatistics statics = this.statics;
				this.statics = new TestStatistics(1, 5000, rpcInfo.serviceNameInConfigCenter);
				return statics;
			}
		}

		final Method method;
		final MethodAtddCommonThriftClient client;
		final TestCaseQueue testQueue;
		final TestCaseRunnor testRunnor;

		public MethodInfo(Method method) throws Exception{
			testQueue = new TestCaseQueue(rpcInfo.serviceNameInConfigCenter);
			this.client = new MethodAtddCommonThriftClient(locationInfo.getIp(), locationInfo.getPort(), method,
					getSvcName(method));
			client.setReadTimeout(rpcInfo.getClientReadTimeoutMs());
			client.setConnectTimeout(rpcInfo.getClientConnectTimeoutMs());
			this.method = method;
			testRunnor = new TestCaseRunnor();
		}

		private String getSvcName(Method method2) {
			final String iFace = "$Iface";
			String name = method2.getDeclaringClass().getName();
			if(name.endsWith(iFace)){
				return name.substring(0, name.length() - iFace.length());
			}else{
				throw new RuntimeException("method not declared in interface end with " + iFace + ", its " + name);
			}
		}

		@Override
		public void run(){

			while(isRunning){
				try {
					testRunnor.testNext(client);
				} catch (Exception e) {
					log.error(e, "when testNext");
				}
				try {
					Thread.sleep(checkItv);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			log.warn("check stop %s", this);
		}
		@Override
		public boolean equals(Object o){
			if(o instanceof MethodInfo){
				MethodInfo other = (MethodInfo) o;
				return client.host.equals(other.client.host) && (client.port == other.client.port);
			}
			return false;
		}
		@Override
		public int hashCode(){
			return client.host.hashCode() * 100000 + client.port;
		}
		@Override
		public String toString(){
			return String.format("serviceInfo: %s, method: ", locationInfo,  method);
		}
	}

	private long checkItv = 1000;
	final ServiceInstanceLocationInfo locationInfo;
	private Map<Method, MethodInfo> methods = new HashMap<Method, MethodInfo>();
	public final LogHelper log;
	private boolean isRunning = true;
	final RpcServiceInfo rpcInfo;
	final MailSendSchedules schedule;

	public RpcServerInstance(ServiceInstanceLocationInfo locationInfo,
			RpcServiceInfo rpcInfo) throws Exception{
		this.locationInfo = locationInfo;
		log = new LogHelper(rpcInfo.serviceNameInConfigCenter + "@" + locationInfo.shortName());
		this.rpcInfo = rpcInfo;
		schedule = new MailSendSchedules(rpcInfo, locationInfo);
	}

	public void setCheckItv(long itvMs){
		this.checkItv = itvMs;
	}

	public void mergeServiceTestCases(Map<Method, List<NamedGammaTestCase>> testCaseMap) throws Exception{
		for(Iterator<Method> itr = testCaseMap.keySet().iterator();itr.hasNext();){
			Method m = itr.next();
			this.mergeMethodTestCases(m, testCaseMap.get(m));
		}
	}

	public void mergeMethodTestCases(Method m, List<NamedGammaTestCase> testCases) throws Exception{
		MethodInfo mi = methods.get(m);
		if(mi == null){
			log.warn("create new methodClient for %s with method %s", locationInfo, m);
			mi = new MethodInfo(m);
			new Thread(mi).start();
			methods.put(m, mi);
		}
		mi.testQueue.mergeNewTestCases(testCases);
	}
	@Override
	public int hashCode(){
		return locationInfo.hashCode();
	}
	@Override
	public boolean equals(Object o){
		if(o instanceof RpcServerInstance){
			RpcServerInstance other = (RpcServerInstance) o;
			return this.locationInfo.equals(other.locationInfo);
		}
		return false;
	}

	public void stopCheck() {
		this.isRunning = false;

	}

	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public String toString(){
		return "RpcServerInstance: " + this.locationInfo;
	}

	public Map<Method, MethodInfo> getMethodMap(){
		return this.methods;
	}
}
