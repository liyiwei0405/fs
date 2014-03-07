package com.funshion.search.utils.systemWatcher.heartBeat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.funshion.search.utils.LogHelper;

public class HeartBeatChecker {
	/**
	 * get ISNBusiness clients get from this map
	 */
	protected Map<String, Set<ISNHBInfo>> businessGroupMap =
			Collections.synchronizedMap(new HashMap<String,  Set<ISNHBInfo>>());
	/**
	 * client's heart beat will refresh HBCounter
	 */
	private Map<ISNHBInfo, HBCounter>beatCounterMap =
			Collections.synchronizedMap(new HashMap<ISNHBInfo, HBCounter>());
	private AtomicLong sessionGen = new AtomicLong(0);
	LogHelper log = new LogHelper("HBCheckor");
	public static final HeartBeatChecker instance = new HeartBeatChecker();
	private HeartBeatChecker(){	
		queueCleanThread.start();
	}
	/**
	 * get all clients still alive
	 * @return
	 */
	public List<ISNHBInfo> getClients(){
		ArrayList<ISNHBInfo>ret = new ArrayList<ISNHBInfo>();
		Collection<Set<ISNHBInfo>> clients = this.businessGroupMap.values();
		for(Set<ISNHBInfo> set : clients){
			ret.addAll(set);
		}
		return ret;
	}
	
	/**
	 * get the clients registered to @para businessType
	 * @param businessType
	 * @return a list contains the registered clients, if there is no clients registered, return empty list
	 */
	public List<ISNHBInfo> getClients(String businessType){
		Set<ISNHBInfo> clients = this.businessGroupMap.get(businessType);
		LinkedList<ISNHBInfo> clientsArray = new LinkedList<ISNHBInfo>();
		if(clients != null){
			clientsArray.addAll(clients);
		}
		return clientsArray;
	}

	private long newSession(ISNHBInfo beat){
		long ret = this.sessionGen.addAndGet(1);
		return ret;
	}
	/**
	 * register client.
	 * @param beat
	 */
	private void registerBusinessClient(ISNHBInfo beat){
		Set<ISNHBInfo> clientSet;
		clientSet = this.businessGroupMap.get(beat.businessType);
		if(clientSet == null){
			clientSet = Collections.synchronizedSet(new HashSet<ISNHBInfo>());
			synchronized(this){
				businessGroupMap.put(beat.businessType, clientSet);
			}
		}
		clientSet.add(beat);
	}
	public long heartBeat(ISNHBInfo beat, long session){
		HBCounter counter = beatCounterMap.get(beat);
		if(counter == null){
			
			long newSession = newSession(beat);
			counter = new HBCounter(beat, getHeartBeatCheckTimeoutPolicy(beat.businessType));
			synchronized(this){
				beatCounterMap.put(beat, counter);
				registerBusinessClient(beat);
			}
			return newSession;
		}else{
			counter.beat();
			return session;
		}
	}
	/**
	 * get heart beat timeout policy for @para businessType
	 * @TODO should configurable
	 * @param businessType
	 * @return
	 */
	private int getHeartBeatCheckTimeoutPolicy(String businessType) {
		return 5000;
	}
	Thread queueCleanThread = new Thread(){
		public void run(){
			while(true){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.debug("checking time thread checking");
				ArrayList<HBCounter>lstToCheck = new ArrayList<HBCounter>();
				Collection<HBCounter> values = beatCounterMap.values();
				lstToCheck.addAll(values);
				log.debug("HeartBeatCheckor now status is %s", lstToCheck);
				for(HBCounter hb : lstToCheck){
					if(!hb.isValid()){
						synchronized(this){
							beatCounterMap.remove(hb.businessClient);
							log.warn("remove invalid session %s", hb);
							Set<ISNHBInfo>  clientSet = businessGroupMap.get(hb.businessClient.businessType);
							if(clientSet != null){
								clientSet.remove(hb.businessClient);
							}
							log.warn("remove invalid client %s", hb.businessClient);
						}
					}
				}
			}
		}
	};
}
