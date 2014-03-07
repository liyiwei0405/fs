package com.funshion.videoService.search;

import com.funshion.search.utils.LogHelper;

public class RequestStatistics extends Thread {
	private final LogHelper log;
	private int requestNum = 0;
	private long requestTimeSpent = 0;
	public RequestStatistics(String name){
		log = new LogHelper("reqstat-" + name);
		this.start();
	}
	public void run(){
		while(true){
			try {
				sleep(5 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.warn("requestNumber: %d, totalTime: %sms, averageRequestTimeSpent: %sms", 
					requestNum, requestTimeSpent/10000 /100.0, requestNum==0 ? "0" : 
				requestTimeSpent/requestNum/10000 /100.0);
			requestNum = 0;
			requestTimeSpent = 0;
		}
	}
	
	public void requestNumIncrease(){
		this.requestNum ++ ;
	}
	
	public void requestTimeSpentIncreaseInNanoTime(long timeSpent){
		this.requestTimeSpent += timeSpent;
	}
	
	public static final RequestStatistics reqGetVideoBaseListByIds = new RequestStatistics("getVideoBaseListByIds");
	public static final RequestStatistics reqGetVideoListByIds = new RequestStatistics("getVideoListByIds");
	public static final RequestStatistics reqRetrieveVideoletBaseInfo = new RequestStatistics("retrieveVideoletBaseInfo");
	public static final RequestStatistics reqRetrieveVideolet = new RequestStatistics("retrieveVideolet");
}