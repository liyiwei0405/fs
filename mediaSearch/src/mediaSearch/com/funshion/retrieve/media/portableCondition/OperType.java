package com.funshion.retrieve.media.portableCondition;

public enum OperType {
	COMPOUND("()"),
	EQUAL("%eq%"),
	RANGE("%rg%"),
//	SEARCH_AND("%sa%"),
//	SEARCH_OR("%so%"),
	SEARCH_TITLE("%st%"),
	SEARCH_TITLE_FULL("%stf%"),
	LIKE("%lk%");

	private final String value;

	private OperType(String value) {
		this.value = value;
	}

	public static OperType findByValue(String str) {
		if(str.equals(EQUAL.value)){
			return EQUAL;
		}else if(str.equals(RANGE.value)){
			return RANGE;
		}else if(str.equals(LIKE.value)){
			return LIKE;
		}else if(str.equals(SEARCH_TITLE.value)){
			return SEARCH_TITLE;
		}else if(str.equals(SEARCH_TITLE_FULL.value)){
			return SEARCH_TITLE_FULL;
		}
		return null;
	}

	public String toString(){
		return value;
	}
}
