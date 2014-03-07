package com.funshion.search.utils.systemWatcher.heartBeat;
public class ISNHBInfo{
	public final String rmtIp;
	public final int rmtPort;
	public final String businessType;
	public ISNHBInfo(String rmtIp, int rmtPort, String businessType){
		this.rmtIp = rmtIp;
		this.rmtPort = rmtPort;
		this.businessType = businessType;
	}
	public boolean equals(Object o){
		if(o == null || ! (o instanceof ISNHBInfo)){
			return false;
		}
		ISNHBInfo other = (ISNHBInfo) o;
		return other.rmtIp.equals(rmtIp) && other.rmtPort == rmtPort && other.businessType.equals(businessType);
	}
	public int hashCode(){
		return rmtPort;
	}

	public String toString(){
		return String.format("%s:%s bType:%s", this.rmtIp, this.rmtPort, this.businessType);
	}
}