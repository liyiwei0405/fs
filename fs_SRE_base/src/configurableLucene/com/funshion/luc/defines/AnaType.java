package com.funshion.luc.defines;

import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
/**
 * a* a * field; e* enum * field<br>
 * @author liying
 *
 */
public enum AnaType{
	
	aInt("aInt"), aLong("aLong"), aFloat("aFloat"), aStr("aStr"), aText("aText"), eStr("eStr");
	
	public final String depict;
	private AnaType(String str){
		this.depict = str;
	}
	
	public static AnaType fromString(String str){
		for(AnaType a : AnaType.values()){
			if(a.depict.equalsIgnoreCase(str)){
				return a;
			}
		}
		throw new RuntimeException("unknow depict for AnaType :" + str);
	}
	
	public boolean isNumType(){
		if(this == aInt || this == aLong || this == aFloat){
			return true;
		}
		return false;
	}
	
	public Type toSortFieldType(){
		if(this == aInt){
			return SortField.Type.INT;
		}else if(this == aLong){
			return SortField.Type.LONG;
		}else if(this == aFloat){
			return SortField.Type.FLOAT;
		}else if(this == aStr){
			return SortField.Type.STRING;
		}else if(this == eStr){
			return SortField.Type.STRING;
		}else{
			throw new RuntimeException("un-support sort fieldType " + this);
		}
	}
}