package com.funshion.rpcserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class CounterEntity{
	public Map<String, List<Map<String, Object>>> interfacesCounter;
	public HashMap<String, AtomicLong> vars_long;
	public SysInfo sysInfo;
	public String currentTime;
	
	public CounterEntity(
			Map<String, List<Map<String, Object>>> map,
			HashMap<String, AtomicLong> vars_long,
			SysInfo sysInfo,
			String currentTime) {
		this.interfacesCounter = map;
		this.vars_long = vars_long;
		this.sysInfo = sysInfo;
		this.currentTime = currentTime;
	}
}