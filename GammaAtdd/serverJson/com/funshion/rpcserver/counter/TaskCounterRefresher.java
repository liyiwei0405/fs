package com.funshion.rpcserver.counter;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.thrift.server.TThreadPoolServer;

import com.funshion.search.utils.LogHelper;

public class TaskCounterRefresher extends Thread{
	public final TaskCounterRefresher instance = new TaskCounterRefresher();
	
	private final LogHelper log = new LogHelper("TC");
	protected HashMap<String, MethodTaskCounter>map = new HashMap<String, MethodTaskCounter>();
	protected HashMap<String, AtomicLong>vars = new HashMap<String, AtomicLong>();
	private TThreadPoolServer.Args serverArgs;
	private TaskCounterRefresher(){
	}
	private long startTime;
	public synchronized void init(TThreadPoolServer.Args serverArgs){
		if(startTime == 0){
			startTime = System.currentTimeMillis();
			this.serverArgs = serverArgs;
			this.start();
		}
	}
	public SysInfo getSysInfo(){
		return new SysInfo(this.startTime, this.serverArgs);
	}
	public MethodTaskCounter registerCounter(String name, int ...checkItvs){
		MethodTaskCounter ret = map.get(name);
		if(ret != null){
			log.debug("register %s's counter denied, becouse already registered",  name);
			return ret;
		}
		ret = new MethodTaskCounter(name, checkItvs);
		map.put(name, ret);
		return ret;
	}
	public MethodTaskCounter getRegisteredTask(String name){
		return map.get(name);
	}
	private MethodTaskCounter besueExists(String name){
		MethodTaskCounter c = map.get(name);
		if(c == null){
			c = registerCounter(name);
		}
		return c;
	}
	public long incVar(String name){
		AtomicLong var = vars.get(name);
		if(var == null){
			var = new AtomicLong(0);
		}
		return var.incrementAndGet();
	}
	public void countOkServed(String name){
		MethodTaskCounter c = this.besueExists(name);
		c.incOkserviced();
	}
	public void countServed(String name){
		MethodTaskCounter c = this.besueExists(name);
		c.incSericed();
	}
	public void countFail(String name){
		MethodTaskCounter c = this.besueExists(name);
		c.incFailServiced();
	}
	public void run(){
		int precision = 400;
		int itv = precision / 10;
		while(true){
			try {
				Thread.sleep(itv);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long now = System.currentTimeMillis();;
			long mod = now % 1000;
			if(mod < precision){
				reset();
				try {
					sleep(precision + 1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	void reset(){
		long nowSeconds = System.currentTimeMillis() / 1000;
		for(MethodTaskCounter c : map.values()){
			c.reset(nowSeconds);
		}
	}
}
