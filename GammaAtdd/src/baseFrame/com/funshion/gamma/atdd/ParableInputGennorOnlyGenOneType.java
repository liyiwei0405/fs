package com.funshion.gamma.atdd;

public class ParableInputGennorOnlyGenOneType extends ParableInputGennor{
	public final boolean onlyRCS;
	public ParableInputGennorOnlyGenOneType(boolean onlyRCS){
		this.onlyRCS = onlyRCS;
	}
	ParableInputGennor gen;
	public void setGennor(ParableInputGennor gen){
		this.gen = gen;
	}
	@Override
	public boolean hasNext() {
		return gen.hasNext();
	}
	protected QueryParas gen() {
		if(this.onlyRCS){
			return gen.nextRcsQuery();
		}else{
			return gen.nextAvsQuery();
		}
	}
	
	@Override
	protected QueryParas genAvsQuery() {
		return gen();
	}
	@Override
	protected QueryParas genRcsQuery() {
		return gen();
	}
	
	
}
