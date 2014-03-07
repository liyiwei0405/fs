package com.funshion.gamma.atdd;

public abstract class InputGennor {

	public abstract boolean hasNext();
	private QueryEntry currentAtddQuery, currentServerQuery;
	protected abstract QueryEntry genAtddQuery();
	protected abstract QueryEntry genServerQuery();
	
	public synchronized QueryEntry nextAtddQuery(){
		currentAtddQuery = this.genAtddQuery();
		return this.currentAtddQuery;
	}
	public synchronized QueryEntry nextServerQuery(){
		currentServerQuery = this.genServerQuery();
		return this.currentServerQuery;
	}
}
