package com.funshion.search.utils.systemWatcher.heartBeat;

public class ISNHeartBeat {

	public final int ver;
	public final long session;
	public final String businessType;
	public final int groupId;
	public final int groupSize;
	public final int groupIdx;
	public final long modifyFlagInSeconds;//in seconds
	public final int svcPort;
	public ISNHeartBeat(BeatType bt, long session){
		this(bt.ver, session, bt.businessType, bt.groupId, bt.groupSize, bt.groupIdx, bt.getModifyFlagInSeconds(), bt.svcPort);
	}
	public ISNHeartBeat(int ver, long session, String businessType, int groupId, int groupSize, int groupIdx, long modifyFlag,
			int port){
		this.ver = ver;
		this.session = session;
		this.businessType = businessType.trim();
		this.groupId = groupId;
		this.groupSize = groupSize;
		this.groupIdx = groupIdx;
		this.modifyFlagInSeconds = modifyFlag;
		this.svcPort = port;
	}

	public String toString(){
		String fmt = "%s:%s:%s:%s:%s:%s:%s:%s";
		return String.format(fmt, ver,
				session,
				businessType,
				groupId,
				groupSize,
				groupIdx,
				modifyFlagInSeconds,
				svcPort);

	}
	public static ISNHeartBeat parse(String str){
		String tokens[] = str.split("\\:");
		return new ISNHeartBeat(
				Integer.parseInt(tokens[0]),
				Long.parseLong(tokens[1]), 
				tokens[2], 
				Integer.parseInt(tokens[3]), 
				Integer.parseInt(tokens[4]), 
				Integer.parseInt(tokens[5]), 
				Long.parseLong(tokens[6]),
				Integer.parseInt(tokens[7]));
	}
}
