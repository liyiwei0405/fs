package com.funshion.gamma.atdd;

public abstract class ParableInputGennor {

	public abstract boolean hasNext();
	
	private QueryParas currentAtddQuery, currentServerQuery;
	protected abstract QueryParas genAvsQuery();
	protected abstract QueryParas genRcsQuery();
	
	public QueryParas nextAvsQuery(){
		currentAtddQuery = this.genAvsQuery();
		return this.currentAtddQuery;
	}
	public QueryParas nextRcsQuery(){
		currentServerQuery = this.genRcsQuery();
		return this.currentServerQuery;
	}
}
