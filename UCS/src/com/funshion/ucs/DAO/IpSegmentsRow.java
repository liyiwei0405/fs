package com.funshion.ucs.DAO;

public class IpSegmentsRow{
	private long ipStart;
	private long ipEnd;
	private int userClassId;
	public IpSegmentsRow(long ipStart, long ipEnd, int userClassId) {
		
		super();
		this.ipStart = ipStart;
		this.ipEnd = ipEnd;
		this.userClassId = userClassId;
	}
	
	public long getIpStart() {
		return ipStart;
	}
	public void setIpStart(long ipStart) {
		this.ipStart = ipStart;
	}
	public long getIpEnd() {
		return ipEnd;
	}
	public void setIpEnd(long ipEnd) {
		this.ipEnd = ipEnd;
	}
	public int getUserClassId() {
		return userClassId;
	}
	public void setUserClassId(int userClassId) {
		this.userClassId = userClassId;
	}
	
}