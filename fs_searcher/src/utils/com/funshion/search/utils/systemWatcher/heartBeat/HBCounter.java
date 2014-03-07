package com.funshion.search.utils.systemWatcher.heartBeat;

import java.text.SimpleDateFormat;

public class HBCounter {
	public final ISNHBInfo businessClient;
	public final int maxItvMsBetweenHB;
	private int beatCount;
	private long lastBeat;
	private long maxBeatItv = 0;
	private boolean isValid = true;
	public final long startTime;
	//maxItvMsBetweenHB usually beat-interval * 2
	public HBCounter(ISNHBInfo beatSession, int maxItvMsBetweenHB, long startTime){
		this.businessClient = beatSession;
		this.maxItvMsBetweenHB = maxItvMsBetweenHB;
		beatCount = 1;
		this.startTime = startTime;
		lastBeat = startTime;
	}
	
	public HBCounter(ISNHBInfo beatSession, int maxItvMsBetweenHB){
		this(beatSession, maxItvMsBetweenHB, System.currentTimeMillis());
	}
	
	private synchronized void checkValid(){
		if(isValid){
			if(System.currentTimeMillis() - lastBeat > this.maxItvMsBetweenHB){
				this.isValid = false;
			}
		}
	}
	public boolean beat(){
		return this.beat(System.currentTimeMillis());
	}
	/**
	 * if successfully beat, return true, else return false
	 * @return
	 */
	public synchronized boolean beat(long beatAt){
		checkValid();
		if(!isValid){
			return false;
		}else{
			beatCount ++;
			long itv = beatAt - this.lastBeat;
			this.maxBeatItv = Math.max(this.maxBeatItv, itv);
			lastBeat = beatAt;
			return true;
		}
	}
	
	public String toString(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		StringBuilder sb = new StringBuilder();
		sb.append("SESS: ");
		sb.append(this.businessClient);
		sb.append(", valid:");
		sb.append(this.isValid);
		
		sb.append(", from:");
		sb.append(sdf.format(startTime));
		sb.append(", last beat:");
		sb.append(sdf.format(this.lastBeat));
		sb.append(", beatCount:");
		sb.append(beatCount);
		sb.append(", maxIteval(sec):");
		sb.append(this.maxBeatItv /1000.0);
		return sb.toString();
	}

	public int getBeatCount() {
		return beatCount;
	}
	public boolean isValid() {
		checkValid();
		return isValid;
	}
}
