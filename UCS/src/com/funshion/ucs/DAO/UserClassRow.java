package com.funshion.ucs.DAO;

import java.util.List;

public class UserClassRow{
	private int classId;
	private String classTag;
	private String rfmSec;
	private List<String> userIds;
	private List<String> channelIds;
	private String cType;
	private int weight;
	private int useTactic;

	public UserClassRow(int classId, String classTag, String rfmSec,
			List<String> userIds, List<String> channelIds, String cType, int weight,
			int useTactic) {
		super();
		this.classId = classId;
		this.classTag = classTag;
		this.rfmSec = rfmSec;
		this.userIds = userIds;
		this.channelIds = channelIds;
		this.cType = cType;
		this.weight = weight;
		this.useTactic = useTactic;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public String getClassTag() {
		return classTag;
	}
	public void setClassTag(String classTag) {
		this.classTag = classTag;
	}
	public String getRfmSec() {
		return rfmSec;
	}
	public void setRfmSec(String rfmSec) {
		this.rfmSec = rfmSec;
	}
	public List<String> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}
	public List<String> getChannelIds() {
		return channelIds;
	}
	public void setChannelIds(List<String> channelIds) {
		this.channelIds = channelIds;
	}
	public String getcType() {
		return cType;
	}
	public void setcType(String cType) {
		this.cType = cType;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getUseTactic() {
		return useTactic;
	}
	public void setUseTactic(int useTactic) {
		this.useTactic = useTactic;
	}

}