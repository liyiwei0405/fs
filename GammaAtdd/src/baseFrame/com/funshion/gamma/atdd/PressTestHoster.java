package com.funshion.gamma.atdd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.funshion.gamma.atdd.CommonThriftClientInfo.ClientInfo;
import com.funshion.search.utils.Charsets;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Misc;

public class PressTestHoster{

	public class PressTestThread implements Runnable{
		public final int id;
		private ClientInfo cInfo;
		public PressTestThread(int tid) throws Exception{
			id = tid;
		}
		private ClientInfo getClientInfo() throws Exception{
			return serverClient.getNewClient();
		}
		@Override
		public void run(){
			try {
				long st = System.currentTimeMillis();
				GammaTestCase tCase = getRandomTestCase();

				Object newRet;
				try{
					if(shortConnection){
						ClientInfo cShort = null;
						try{
							cShort =  getClientInfo();
							newRet = serverClient.query(tCase.queryParas(), cShort);
						}finally{
							if(cShort != null){
								cShort.close();
							}
						}
					}else{
						if(cInfo == null){
							cInfo = getClientInfo();
						}
						newRet = serverClient.query(tCase.queryParas(), cInfo);
					}
					if(useCmp){
						//FIXME support compareMask?
						resultComparator.compare(tCase.queryParas(), newRet, tCase.expectResult(),
								null);
					}
					statics.addOkTest(System.currentTimeMillis() - st);
				}catch(Exception e){
					log.error(e, "checkFail!");
					statics.addCompareFail(e);
				}
			} catch (Exception e) {
				log.error(e, "execute Exception");
				//				e.printStackTrace();
				statics.addException(e);
			}

			if(statics.finished){
				queue.add(this);
			}else{
				pool.execute(this);
			}
		}
	}

	final int maxTraceTime = 4999;
	TestStatistics statics;
	LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	ExecutorService pool;
	final long checkItv ;
	final boolean useCmp;
	int startNum = 1;
	static final LogHelper log = new LogHelper("pth");
	final int maxThreadNumber;
	final boolean shortConnection;
	public final ResultComparator resultComparator;
	List<GammaTestCase> testCases;
	final MethodAtddCommonThriftClient serverClient;
	final File saveResultDir;
	public final String cfgSection;
	final ConfigReader cr;
	final boolean roundModel;
	final int timeoutMs;
	public PressTestHoster(List<GammaTestCase> testCases, MethodAtddCommonThriftClient serverClient,
			final ResultComparator resultComparator, ConfigReader cr) throws Exception {
		this.testCases = testCases;
		this.serverClient = serverClient;
		this.resultComparator = resultComparator;
		ResultComparatorBase.disablePrintResult();
		this.cr = cr;
		this.useCmp = cr.getInt("useCmp", 1) == 1;
		this.cfgSection = cr.sectionName;
		saveResultDir = new File("./pressTestResult/" + cfgSection.replace('[', ' ').replace(']', ' ').trim() + "/");
		this.checkItv = 60 * 1000L * cr.getInt("swapThreadsInMinutes", 10);
		this.saveResultDir.mkdirs();
		maxThreadNumber = cr.getInt("maxThreadNumber", 256);
		this.startNum = cr.getInt("startNum", 1);
		roundModel = cr.getInt("roundModel", 1) == 1;
		timeoutMs = cr.getInt("timeoutMs", 1000);
		serverClient.setConnectTimeout(timeoutMs);
		serverClient.setReadTimeout(timeoutMs);
		shortConnection = cr.getInt("shortConnection", 1) == 1;
		for(int x = 0; x < maxThreadNumber + 32; x ++){
			queue.add(new PressTestThread(x + 1));
		}
		new Thread(){
			@Override
			public void run(){
				long lastLog = 0;
				while(true){
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(System.currentTimeMillis() - lastLog > 3000){
						log.fatal("queue size %s, nowNum = %s",  
								queue.size(), startNum);

					}
				}
			}
		}.start();
	}
	private GammaTestCase getRandomTestCase() {
		int randIdx = Misc.randInt(0, testCases.size());
		return testCases.get(randIdx);
	}

	void newPool(int num) throws Exception{
		if(pool != null){
			pool.shutdown();
		}
		pool = Executors.newFixedThreadPool(num);		
		statics = new TestStatistics(num, maxTraceTime, "");
		while(true){
			Runnable r = this.queue.poll();
			if(r == null){
				break;
			}
			pool.execute(r);
		}
	}
	public void startTest() throws Exception {
		newPool(startNum);
		while(roundModel){
			if(statics.checkFinish(this.checkItv)){
				try {
					swapSysInfo(this.statics, startNum);
					Thread.sleep(10 * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				startNum *= 2;
				if(startNum > maxThreadNumber){
					startNum = 8;
				}
				log.fatal("now thread number set to %s", startNum);
				newPool(startNum);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void swapSysInfo(TestStatistics statistic, int tNum) throws IOException {
		File toSave = new File(this.saveResultDir, 
				tNum + "_threads_" + new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS").format(System.currentTimeMillis()) + ".rslt");
		LineWriter lw = new LineWriter(toSave, false, Charsets.UTF8_CS);
		lw.write(getSysInfo(statistic));
		lw.close();
	}

	public String getSysInfo(int maxTimeLine) {
		return getSysInfo(this.statics, maxTimeLine);
	}
	private String getSysInfo(TestStatistics statistic) {
		return getSysInfo(statistic, Integer.MAX_VALUE);
	}
	public String getSysInfo(TestStatistics statistic, int maxTimeLine) {
		StringBuilder sb = new StringBuilder();
		sb.append("test cfgSec " + this.cfgSection + ", at " + this.serverClient);
		sb.append("\n\n");
		statistic.setEnd();
		sb.append(statistic.getStrInfo(maxTimeLine));

		return sb.toString();
	}
}
