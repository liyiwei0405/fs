package com.funshion.rpcserver;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class TaskCounter {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final String name;
	public final int watchItv;
	private long maxUsedTimeNanoSec = 0, minUsedTimeNanoSec = 100000;
	private long okUsedTimeNanoSec = 0, failUsedTimeNanoSec = 0;
	protected transient AtomicLong _serviced = new AtomicLong(0), _failed  = new AtomicLong(0),  _Ok  = new AtomicLong(0);
	protected long fromTime;

	public TaskCounter(String name, final int watchItv){
		this.watchItv = watchItv;
		this.name = name + "_" + watchItv;
		this.fromTime = System.currentTimeMillis();
	}

	public long incServiced(){
		return _serviced.addAndGet(1);
	}

	public long incFailServiced(long usedNanoSec){
		this.failUsedTimeNanoSec += usedNanoSec;
		if(usedNanoSec > this.maxUsedTimeNanoSec){
			this.maxUsedTimeNanoSec = usedNanoSec;
		}
		if(usedNanoSec < this.minUsedTimeNanoSec){
			this.minUsedTimeNanoSec = usedNanoSec;
		}
		return _failed.addAndGet(1);
	}

	public long incOkServiced(long usedNanoSec){
		this.okUsedTimeNanoSec += usedNanoSec;
		if(usedNanoSec > this.maxUsedTimeNanoSec){
			this.maxUsedTimeNanoSec = usedNanoSec;
		}
		if(usedNanoSec < this.minUsedTimeNanoSec){
			this.minUsedTimeNanoSec = usedNanoSec;
		}
		return  _Ok.addAndGet(1);
	}

	public Map<String, Object> addToMap(){
		Map<String, String> numMap = new HashMap<String, String>();
		numMap.put("_serviced", String.valueOf(_serviced));
		numMap.put("_failed  ", String.valueOf(_failed));
		numMap.put("_Ok", String.valueOf(_Ok));

		Map<String, String> timeMap = new HashMap<String, String>();
		timeMap.put("_failed_avgUsedTimeMs", this._failed.longValue() == 0 ? "0" : String.valueOf(this.failUsedTimeNanoSec / this._failed.longValue() / 10000 / 100.0));
		timeMap.put("_Ok_avgUsedTimeMs", this._Ok.longValue() == 0 ? "0" : String.valueOf(this.okUsedTimeNanoSec / this._Ok.longValue() / 10000 / 100.0));
		timeMap.put("maxUsedTimeMs", String.valueOf(this.maxUsedTimeNanoSec / 10000 / 100.0));
		timeMap.put("minUsedTimeMs", String.valueOf(this.minUsedTimeNanoSec / 10000 / 100.0));

		Map<String, Object>map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("watchItv", String.valueOf(watchItv));
		map.put("fromTime", sdf.format(fromTime));
		map.put("reqNum", numMap);
		map.put("reqUsedTime", timeMap);
		map.put("timeSpentSeconds", (System.currentTimeMillis() - this.fromTime) / 1000.0);

		return map;
	}

	public void reset(){
		_serviced.set(0);
		_failed.set(0);
		_Ok.set(0);
		maxUsedTimeNanoSec = minUsedTimeNanoSec = okUsedTimeNanoSec = failUsedTimeNanoSec = 0;
		fromTime = System.currentTimeMillis();
	}

	@Override
	public String toString(){
		return this.name + "," + this.watchItv + "," + this._serviced + this._failed + this._Ok + this.fromTime;
	}
}
