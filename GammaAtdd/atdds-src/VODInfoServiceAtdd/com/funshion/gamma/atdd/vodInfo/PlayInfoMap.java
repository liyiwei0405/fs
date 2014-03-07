package com.funshion.gamma.atdd.vodInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.funshion.gamma.atdd.vodInfo.thrift.PlayInfoRec;
import com.funshion.gamma.atdd.vodInfo.thrift.SerialInfoRec;

public class PlayInfoMap {
	public static class PlayInfoRecExt{
		PlayInfoRec rec;
		int serNo;
		public PlayInfoRecExt(PlayInfoRec rec, int serNo){
			this.rec = rec;
			this.serNo = serNo;
		}
	}
	
	public static PlayInfoMap nowInstance(){
		return nowInstance;
	}
	private static PlayInfoMap nowInstance;
	static{

		try {
			nowInstance = Load.load();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("error when boot " + e);
			System.exit(-1);
		}
		new Thread(){
			public void run(){
				while(true){
					try {
						sleep(10 *60 * 1000);
						nowInstance = Load.load();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("error when boot " + e);
						System.exit(-1);
					}
				}
			}
		}.start();
	}
	HashMap<Integer, SerialInfoRec> mapBySerialId = new HashMap<Integer, SerialInfoRec>();
	HashMap<Integer, PlayInfoRec> mapByPlayInfoId = new HashMap<Integer, PlayInfoRec>();
	HashMap<String, PlayInfoRec> mapByHashId = new HashMap<String, PlayInfoRec>();
	HashMap<Integer, List<PlayInfoRec>> mapByMediaId = new HashMap<Integer, List<PlayInfoRec>>();
	HashMap<Integer, List<SerialInfoRec>> mapSerByMediaId = new HashMap<Integer, List<SerialInfoRec>>();
	
	public void addPlayInfoRecord(PlayInfoRec rec) {
		List<SerialInfoRec> sers = mapSerByMediaId.get(rec.mediaID);
		if(sers == null || sers.size() == 0){
			return;
		}
		mapByPlayInfoId.put(rec.playInfoID, rec);
		mapByHashId.put(rec.hashID, rec);
		List<PlayInfoRec> lst = mapByMediaId.get(rec.mediaID);
		if(lst == null){
			lst = new LinkedList<PlayInfoRec>();
			mapByMediaId.put(rec.mediaID, lst);
		}
		lst.add(rec);
		
	}

	public void optimizePlayInfo(){
		Iterator<List<PlayInfoRec>> itr = mapByMediaId.values().iterator();
		while(itr.hasNext()){
			List<PlayInfoRec> rLst = itr.next();
			sort(rLst);
		}
		Comparator<SerialInfoRec>cmp = new Comparator<SerialInfoRec>(){

			@Override
			public int compare(SerialInfoRec o1, SerialInfoRec o2) {
				if(o1.serialNo == o2.serialNo){
					return o1.serialID - o2.serialID;
				}
				return o1.serialNo - o2.serialNo;
			}
			
		};
		Iterator<List<SerialInfoRec>> itr2 = mapSerByMediaId.values().iterator();
		while(itr2.hasNext()){
			List<SerialInfoRec> l = itr2.next();
			Collections.sort(l, cmp);
					
		}

	}

	private void sort(List<PlayInfoRec> rLst) {
		final List<PlayInfoRecExt> lst = new ArrayList<PlayInfoRecExt>();
		for(PlayInfoRec rec : rLst){
			SerialInfoRec recSer = this.mapBySerialId.get(rec.serialID);
			int serNo = 0;
			if(recSer != null){
				serNo = recSer.serialNo;
				lst.add(new PlayInfoRecExt(rec, serNo));
			}
			
		}
		Collections.sort(lst, new Comparator<PlayInfoRecExt>(){

			@Override
			public int compare(PlayInfoRecExt o1, PlayInfoRecExt o2) {
				if(o1.serNo == o2.serNo){
					int diff = o1.rec.serialID - o2.rec.serialID;
					if(diff == 0){
						return o1.rec.playInfoID - o2.rec.playInfoID;
					}
					return diff;
				}
				return o1.serNo - o2.serNo;
			}

		});
		rLst.clear();
		for(PlayInfoRecExt rec : lst){
			rLst.add(rec.rec);
		}
	}

	public void addSerialRecord(SerialInfoRec sRec) {
		mapBySerialId.put(sRec.serialID, sRec);
	}

	public void putMediaSer(int mediaId, SerialInfoRec sRec) {
		List<SerialInfoRec> recs = mapSerByMediaId.get(mediaId);
		if(recs == null){
			recs = new LinkedList<SerialInfoRec>();
			mapSerByMediaId.put(mediaId, recs);
		}
		recs.add(sRec);
	}


}
