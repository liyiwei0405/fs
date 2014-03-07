package com.funshion.fsql.expression.ast;

import com.funshion.fsql.expression.TokenType;

public class ConjunctNode extends Node{
	public final TokenType type;
	
	public ConjunctNode(int position, TokenType type) {
		super(position);
		this.type = type;
	}


	@Override
	public String toString(){
		if(type == TokenType.AND){
			return " AND ";
		}else if(type == TokenType.OR){
			return " OR ";
		}else if(type == TokenType.NOT){
			return " NOT ";
		}else{
			throw new RuntimeException("error conjunct type " + type);
		}
	}
}
