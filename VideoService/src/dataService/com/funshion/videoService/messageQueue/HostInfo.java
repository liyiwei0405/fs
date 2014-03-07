package com.funshion.videoService.messageQueue;

import com.rabbitmq.client.Address;

public class HostInfo{
	String host;
	int port;
	public HostInfo(String host, int port){
		this.host = host;
		this.port = port;
	}
	Address toAddress(){
		return new Address(host, port);
	}
	public String toString(){
		return host + ':' + port;
	}
	static Address[] list2Address(HostInfo[]hosts){
		Address addr[] = new Address[hosts.length];
		for(int x = 0; x < hosts.length; x ++){
			addr[x] = hosts[x].toAddress();
		}
		return addr;
	}
}