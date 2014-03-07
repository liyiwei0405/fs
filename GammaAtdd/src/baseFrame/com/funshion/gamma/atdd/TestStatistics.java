package com.funshion.gamma.atdd;

import java.text.SimpleDateFormat;

public class TestStatistics{
	public final String name;
	private final long st;
	private long ed = 0;

	private int oktest = 0;
	private int compFail = 0;
	private int exeFail = 0;

	public final int[]exeTimes;
	public final int threads;
	boolean finished = false;
	public TestStatistics(int threads, int maxTraceTime, String name){
		this.threads = threads;
		st = System.currentTimeMillis();
		exeTimes = new int[maxTraceTime + 1];
		this.name = name;
	}
	private int doAll(){
		return this.oktest + compFail + exeFail;
	}
	public long usedMs(){
		return ed == 0 ? System.currentTimeMillis() - st : ed - st;
	}
	public double rps(){
		return ((int) (doAll() * 1000L * 100 / usedMs())) / 100.0;
	}
	public boolean checkFinish(long checkItv) {
		if(System.currentTimeMillis() - st > checkItv){
			ed = System.currentTimeMillis();
			finished = true;
			return true;
		}
		return false;
	}

	public void addException(Exception e){//record Exception
		exeFail ++;
	}
	public void addCompareFail(Exception e){//record CompareFail
		compFail ++;
	}
	public void addOkTest(long usedMs){//record okResult
		oktest ++;
		if(usedMs > exeTimes.length - 1){
			usedMs = exeTimes.length - 1;
		}
		exeTimes[(int) usedMs] ++;
	}
	public int getOktest() {
		return oktest;
	}
	public int getCompFail() {
		return compFail;
	}
	public int getExeFail() {
		return exeFail;
	}
	public long getEndTime() {
		return ed;
	}
	public void setEnd() {
		this.ed = System.currentTimeMillis();
	}
	public long getStartTime() {
		return st;
	}
	public String getStrInfo(int maxTimeLine) {
		TestStatistics statistic = this;
		StringBuilder sb = new StringBuilder();
		sb.append("thread num:" + statistic.threads + "\n\n");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		sb.append("FROM:" + sdf.format(statistic.getStartTime()) + "\n");
		sb.append("  to:" + sdf.format(System.currentTimeMillis()) + "\n");
		sb.append("usetime:" + statistic.usedMs() / 1000.0 + " seconds\n");
		sb.append("rps:" + statistic.rps() + " req/sec\n");
		sb.append("oktest " );
		sb.append(statistic.getOktest());
		sb.append("\t");

		sb.append("compFail " );
		sb.append(statistic.getCompFail());
		sb.append("\t");

		sb.append("exeFail " );
		sb.append(statistic.getExeFail());
		sb.append("\n");

		sb.append("ms\tcount\tPx%s\n");
		int exeTimes[] = new int[this.exeTimes.length];
		int sum = 0;
		for(int x = 0; x < statistic.exeTimes.length; x ++){
			exeTimes[x] = statistic.exeTimes[x];
			sum += exeTimes[x];
		}
		if(sum == 0){
			sb.append("exetimes: 0" );
		}else{
			int partSum = 0;
			for(int x = 0; x < Math.min(exeTimes.length, maxTimeLine); x ++){
				partSum += exeTimes[x];
				long factor = 10000L * partSum / sum;
				sb.append(x);
				sb.append('\t');
				sb.append(exeTimes[x]);
				sb.append('\t');
				sb.append(factor / 100.0);
				sb.append("% \n");
			}
		}
		return sb.toString();
	}
}