package com.funshion.gamma.atdd.serialize;

import java.util.HashMap;

public class DeserializedFieldInfo{
	public final String name;
	public final Object value;
	private boolean ignoreCompare = false;
	private boolean ignoreListOrder = false;
	private boolean ignoreStrCase = false;
	public DeserializedFieldInfo(String name, Object value){
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString(){
		return "name = '" + name + "', value = '" + value + "', valueType: '" + (value == null ? "unknown": value.getClass().getName()) + "'";
	}

	private HashMap<Object, DeserializedFieldInfo>maskMap = new HashMap<Object, DeserializedFieldInfo>();
	public void setIgnoreCompare() {
		ignoreCompare = true;
	}
	
	public void regMask(String fieldName, DeserializedFieldInfo fi){
		maskMap.put(fieldName, fi);
	}
	public void regMaskIndex(int index, DeserializedFieldInfo fi){
		maskMap.put(index, fi);
	}
	public DeserializedFieldInfo getMask(int x) {
		return maskMap.get(x);
	}
	public DeserializedFieldInfo getMask(String fieldName) {
		return maskMap.get(fieldName);
	}

	public boolean isIgnoreCompare() {
		return ignoreCompare;
	}

	public boolean isIgnoreStrCase() {
		return ignoreStrCase;
	}

	public void setIgnoreStrCase(boolean ignoreStrCase) {
		this.ignoreStrCase = ignoreStrCase;
	}

	public boolean isIgnoreListOrder() {
		return ignoreListOrder;
	}

	public void setIgnoreListOrder(boolean ignoreListOrder) {
		this.ignoreListOrder = ignoreListOrder;
	}

}