package com.funshion.ucs.DAO;

public class AreaRow{
	private int areaId;
	private String aType;
	private int tactic;
	private int useTactic;
	
	public AreaRow(int areaId, String aType, int tactic, int useTactic) {
		super();
		this.areaId = areaId;
		this.aType = aType;
		this.tactic = tactic;
		this.useTactic = useTactic;
	}
	
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getaType() {
		return aType;
	}
	public void setaType(String aType) {
		this.aType = aType;
	}
	public int getTactic() {
		return tactic;
	}
	public void setTactic(int tactic) {
		this.tactic = tactic;
	}
	public int getUseTactic() {
		return useTactic;
	}
	public void setUseTactic(int useTactic) {
		this.useTactic = useTactic;
	}
}