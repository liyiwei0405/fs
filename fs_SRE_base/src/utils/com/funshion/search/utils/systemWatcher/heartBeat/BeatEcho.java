package com.funshion.search.utils.systemWatcher.heartBeat;

public class BeatEcho {

	public final String businessType;
	public final int businessPort;
	public final long session;
	
	public BeatEcho(String businessType, int businessPort, long session) {
		this.businessType = businessType;
		this.businessPort = businessPort;
		this.session = session;
	}
	public String toString(){
		return businessType + ":" + this.businessPort + ":" + this.session;
	}
	public static BeatEcho parse(String str)throws Exception{
		String tokens[] = str.split(":");
		return new BeatEcho(tokens[0], Integer.parseInt(tokens[1]), Long.parseLong(tokens[2]));
	}
}
