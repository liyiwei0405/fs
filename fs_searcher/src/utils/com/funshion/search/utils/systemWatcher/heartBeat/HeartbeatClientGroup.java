package com.funshion.search.utils.systemWatcher.heartBeat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;

public class HeartbeatClientGroup {
	public final String businessType;
	ArrayList<HeartBeatClient>clients = new ArrayList<HeartBeatClient>();
	public HeartbeatClientGroup(String tokens[], String businessType, File cfgFile) throws IOException{
		this.businessType = businessType;

		for(String value : tokens){
			value = value.trim();
			if(value.length() == 0)
				continue;

			ConfigReader newReader = new ConfigReader(cfgFile, value);
			try{
				HBInfo inf = getBeatConfig(newReader);
				HeartBeatClient clt = new HeartBeatClient(inf.sserverIp, inf.sseverPort, inf.timeout);
				LogHelper.log.info("add new HeartBeat server %s:%s for %s with timeout %s", inf.sserverIp, inf.sseverPort, businessType, inf.timeout);
				clt.addBeatType(inf.beatType);
				clt.start();
				clients.add(clt);
			}catch(Exception ex){
				LogHelper.log.warn("bad heartBeat define config! skip hbserver config line %s", ex);
			}

		}
	}
	public synchronized void setBeatStatus(boolean beat){
		for(HeartBeatClient clt : clients){
			clt.setBeatStatus(beat);
		}
	}
	public List<HeartBeatClient> getClients(){
		ArrayList<HeartBeatClient>clients = new ArrayList<HeartBeatClient>();
		clients.addAll(this.clients);
		return clients;
	}

	class HBInfo{
		final public int timeout;
		final String sserverIp;
		final int sseverPort;
		final BeatType beatType;
		HBInfo(String ip, int port, BeatType beatType, int timeout){
			this.sserverIp = ip;
			this.sseverPort = port;
			this.beatType = beatType;
			this.timeout = timeout;
		}
	}
	private HBInfo getBeatConfig(ConfigReader cr) throws Exception{
		String ip = cr.getValue("sserverIp");
		int port = cr.getInt("sseverPort", -1);
		int groupid = cr.getInt("groupid", -1);
		int groupSize = cr.getInt("groupsize", -1);
		int groupidx = cr.getInt("groupidx", -1);
		int svcport = cr.getInt("svcport", -1);
		if(ip == null){
			throw new Exception("ip error config for " + cr.section + " of config file " + cr.configFile);
		}

		if(port == -1){
			throw new Exception("port error config for " + cr.section + " of config file " + cr.configFile);
		}

		if(groupid < 0){
			throw new Exception("groupid error config for " + cr.section + " of config file " + cr.configFile);
		}

		if(groupSize < 1){
			throw new Exception("groupSize error config for " + cr.section + " of config file " + cr.configFile);
		}

		if(groupidx < 0 || groupidx >= groupSize){
			throw new Exception("groupidx error config for " + cr.section + " of config file " + cr.configFile);
		}

		if(svcport <= -1){
			throw new Exception("svcport error config for " + cr.section + " of config file " + cr.configFile);
		}

		int timeout = cr.getInt("timeout", 2000);
		BeatType bt = new BeatType(businessType, groupid, groupSize, groupidx, svcport);
		HBInfo ret = new HBInfo(ip, port, bt, timeout);
		return ret;
	}

	public String toString(){
		return "HeartbeatClientGroup for " + this.businessType + " : " + this.clients.toString();
	}

}
