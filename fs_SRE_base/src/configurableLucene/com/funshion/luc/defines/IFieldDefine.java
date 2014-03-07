package com.funshion.luc.defines;

public class IFieldDefine {
	public final String fieldName;
	public final AnaType aType;
	public final boolean store;
	public final boolean index;
	public IFieldDefine(String fieldName, AnaType aType, boolean store, boolean index){
		this.fieldName = fieldName.intern();
		this.aType = aType;
		this.store = store;
		this.index = index;
	}
	
	public boolean scoreSim(){
		return aType == AnaType.aText;
	}
	
	public String toString(){
		return String.format("%s: %s, store %s, index %s", 
				fieldName, this.aType, store, index);
	}
}
