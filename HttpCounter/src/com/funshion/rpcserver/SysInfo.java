package com.funshion.rpcserver;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.thrift.server.TThreadPoolServer;

public class SysInfo {
	private final long freeMemoryMB = (Runtime.getRuntime().freeMemory()) / 1024 / 1024;
	private final long totalMemoryMB = Runtime.getRuntime().totalMemory() / 1024 / 1024;
	private final long maxMemoryMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;
	private final long stillCanUseMemoryMB = getMaxMemoryMB() - totalMemoryMB + freeMemoryMB;
	private final long sysUptimeSeconds;
	private final String sysBootAt;
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private final long linkedInSocketActiveCount, linkedInSocketActiveWaiting,
	hasServed;

	public SysInfo(long sysBootAt, TThreadPoolServer.Args serverArgs){
		this.sysUptimeSeconds = (System.currentTimeMillis() - sysBootAt) / 1000;
		this.sysBootAt = sdf.format(sysBootAt);
		if(serverArgs.executorService instanceof ThreadPoolExecutor){
			this.linkedInSocketActiveCount = ((ThreadPoolExecutor)serverArgs.executorService).getActiveCount();
			this.linkedInSocketActiveWaiting = ((ThreadPoolExecutor)serverArgs.executorService).getPoolSize();
			this.hasServed  = ((ThreadPoolExecutor)serverArgs.executorService).getTaskCount();
		}else{
			this.linkedInSocketActiveCount = -1;
			this.linkedInSocketActiveWaiting = -1;
			hasServed = 0;
		}
	}

	public long getFreeMemoryMB() {
		return freeMemoryMB;
	}
	public long getTotalMemoryMB() {
		return totalMemoryMB;
	}
	public long getMaxMemoryMB() {
		return maxMemoryMB;
	}
	public long getStillCanUseMemoryMB() {
		return stillCanUseMemoryMB;
	}

	public String getSysBootAt() {
		return sysBootAt;
	}
	
	public long getSysUptimeSeconds() {
		return sysUptimeSeconds;
	}

	public long getLinkedInSocketActiveCount() {
		return linkedInSocketActiveCount;
	}

	public long getLinkedInSocketActiveWaiting() {
		return linkedInSocketActiveWaiting;
	}

	public long getHasServed() {
		return hasServed;
	}

}
