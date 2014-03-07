package com.funshion.search.utils.systemWatcher.heartBeat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.systemWatcher.MessageClient;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.innerAction.HeartBeatAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class HeartBeatClient extends Thread{

	public final String ip;
	public final int port;
	public final int beatItvMs;
	private boolean isBeating = false;
	private MessageClient client;
	final LogHelper log;

	private int beatCount;
	private int actionOk;
	private int actionFail;
	private int beatFail;
	private int connectTimes;
	private Map<BeatType, Long> btypes = Collections.synchronizedMap(new HashMap<BeatType, Long>());

	public HeartBeatClient(String ip, int port, int beatItvMs){
		this(ip, port, beatItvMs, null);
	}
	public HeartBeatClient(String ip, int port, int beatItvMs, Collection<BeatType> btypes){
		this.beatItvMs = beatItvMs;
		this.ip = ip;
		this.port = port;
		log = new LogHelper("hb-" + ip + ":" + port);
		if(btypes != null){
			for(BeatType bt : btypes){
				addBeatType(bt);
			}
		}
	}

	public boolean addBeatType(BeatType toAdd){
		if(null == toAdd){
			return false;
		}
		if(this.btypes.containsKey(toAdd)){
			return false;
		}
		this.btypes.put(toAdd, 0l);
		return true;
	}

	public void removeBeatType(BeatType toAdd){
		this.btypes.remove(toAdd);
	}
	private ArrayList<java.util.Map.Entry<BeatType, Long>>elst(){
		ArrayList<java.util.Map.Entry<BeatType, Long>>elst = new ArrayList<java.util.Map.Entry<BeatType, Long>>();
		Iterator<java.util.Map.Entry<BeatType, Long>>  itr = btypes.entrySet().iterator();
		while(itr.hasNext()){
			java.util.Map.Entry<BeatType, Long>e = itr.next();
			elst.add(e);
		}
		return elst;
	}
	public void run(){
		while(true){
			try{
				if(isBeating){
					beatCount ++;
					if(client == null){
						connectTimes += 1;
						client = new MessageClient(ip, port, beatItvMs / 2);
					}

					QueryMessage qm = MessageClient.emptyQuery(HeartBeatAction.CMD);
					ArrayList<java.util.Map.Entry<BeatType, Long>>elst = elst();
					
					for(java.util.Map.Entry<BeatType, Long> e : elst){
						ISNHeartBeat beat = new ISNHeartBeat(e.getKey(), e.getValue());
						qm.addToMessageBody(beat.toString());
					}
					AnswerMessage am = client.queryMsg(qm);
					if(WatcherAction.isStatusOK(am)){
						actionOk ++;
						log.debug("Ok heartbeat %s", am);
						for(int x = 0; x < elst.size(); x ++){
							final java.util.Map.Entry<BeatType, Long>e = elst.get(x);
							if(x >= am.answerBody.size()){
								log.error("invalid index for am: %s, am %s", x, am.answerBody);
								break;
							}
							BeatEcho be = BeatEcho.parse(am.answerBody.get(x));
							if(be.session != elst.get(x).getValue()){
								if(be.businessType.equalsIgnoreCase(e.getKey().businessType)
										&&be.businessPort == e.getKey().svcPort){
									Long oldSession = btypes.get(e.getKey());
									if(oldSession != null ) {
										btypes.put(e.getKey(), be.session);
										log.warn("session updated to %s from %s, for %s", oldSession, be.session, e);
									}else{
										log.error("find no old session %s", e);
									}
								}else{
									log.error("echo return mismatch(session also mismatched) for idx %s, send is %s, but receive : %s",
											x, e, be);
								}
							}else{
								if(be.businessType.equalsIgnoreCase(e.getKey().businessType)
										&& be.businessPort == e.getKey().svcPort){
									log.debug("get good reply %s", be);
								}else{
									log.error("echo return mismatch(session mismatched) for idx %s, send is %s, but receive : %s" ,
											x, e, be);
								}
							}
						}
					}else{
						actionFail ++;
						log.error("got error! try beat but got invalid heartbeat %s", am);
					}
				}else{
					log.debug("not beating, for %s", this.btypes);
				}
			}catch(Exception e){
				beatFail ++;
				log.error(e, "when beating");
				if(client != null){//rudely close the client connection, becouse heart beat usually not too frequently
					client.close();
				}
				client = null;
			}finally{
				try {
					sleep(beatItvMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * total beat count
	 * @return
	 */
	public int getBeatCount() {
		return beatCount;
	}

	/**
	 * beat times get ok return from server
	 * @return
	 */
	public int getActionOk() {
		return actionOk;
	}


	/**
	 * action fail means contact ok, but server return fail status
	 * @return
	 */
	public int getActionFail() {
		return actionFail;
	}

	/**
	 * beat fail means any exception during beating
	 * @return
	 */
	public int getBeatFail() {
		return beatFail;
	}

	public int getConnectTimes() {
		return connectTimes;
	}

	public boolean isBeating() {
		return isBeating;
	}

	public void setBeatStatus(boolean beat) {
		log.warn("setting to %s", isBeating ? "BLOCK beat status" : "resume beat(now is" + this.isBeating + ")");
		this.isBeating = beat;
	}

}
