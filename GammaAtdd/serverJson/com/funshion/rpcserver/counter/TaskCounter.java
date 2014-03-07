package com.funshion.rpcserver.counter;

import java.text.SimpleDateFormat;
import java.util.Map;

public class TaskCounter {
	public final String name;
	public final int watchItv;
	protected transient int _serviced, _failed, _200;
	protected long fromTime;
	public TaskCounter(String name, final int watchItv){
		this.watchItv = watchItv;
		this.name = name + "_" + watchItv;
		fromTime = System.currentTimeMillis();
	}
	public int incSericed(){
		return ++ _serviced;
	}
	public int incFailServiced(){
		return ++ _failed;
	}
	
	public int inc200(){
		return ++ _200;
	}
	
	public void addToMap(Map<String, Integer>map){
		map.put(name + "_serviced", _serviced);
		map.put(name + "_failed  ", _failed);
		map.put(name + "_200", _200);
	}
	public void reset(){
		_serviced = _failed = _200 = 0;
		fromTime = 0;
	}
	
	public String fromTime(SimpleDateFormat sdf){
		return sdf.format(fromTime);
	}
}
