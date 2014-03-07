package com.funshion.search.utils.systemWatcher.heartBeat;

import java.text.SimpleDateFormat;

public class BeatType {
	public final int ver;
	public final String businessType;
	public final int groupId;
	public final int groupIdx;
	public final int groupSize;
	private long modifyFlagInSeconds;
	public final int svcPort;
	public BeatType(String businessType, int groupId, int groupSize, int groupIdx, int svcPort){
		this.ver = 0;
		this.businessType = businessType;
		this.groupId = groupId;
		this.groupIdx = groupIdx;
		this.groupSize = groupSize;
		this.svcPort = svcPort;
	}
	public long getModifyFlagInSeconds() {
		return modifyFlagInSeconds;
	}
	public void setModifyFlagInSeconds(long modifyFlagInSeconds) {
		this.modifyFlagInSeconds = modifyFlagInSeconds;
	}
	
	public String toString(){
		return String.format(
				"BeatType{ver:%s, businessType:%s, groupId:%s, groupIdx:%s, groupSize:%s, modifyFlagInSeconds:%s, svcPort:%s}",
				ver,
				businessType,
				groupId,
				groupIdx,
				groupSize,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(modifyFlagInSeconds * 1000),
				svcPort
				);
	}
}
