package com.funshion.fsql.expression.ast;

public abstract class ConditionNode extends Node {
	public ConjunctNode myConjunct;
	public ConditionNode nextCondition;
	
	public ConditionNode(int position) {
		super(position);
	}

	public String appendString(){
		if(this.nextCondition != null){
			return " " + nextCondition.toString();
		}else{
			return "";
		}
	}
}
