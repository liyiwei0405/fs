package com.funshion.gamma.atdd.healthWatcher;
public class ServiceInstanceLocationInfo{
	private String ip;
	private int port;
	private boolean ready;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof ServiceInstanceLocationInfo){
			ServiceInstanceLocationInfo other = (ServiceInstanceLocationInfo) o;
			if(other.ip.equals(ip)){
				if(other.port == port){
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public int hashCode(){
		return (ip.hashCode() << 16) + port;
	}

	@Override
	public String toString(){
		return ip + ":" + port + ", ready = " + ready;
	}

	public String shortName(){
		return ip + ":" + port;
	}
}