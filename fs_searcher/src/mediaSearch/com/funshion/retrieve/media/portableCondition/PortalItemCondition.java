package com.funshion.retrieve.media.portableCondition;

import java.util.List;

import com.funshion.retrieve.media.thrift.Token;
import com.funshion.retrieve.media.thrift.TokenType;

public class PortalItemCondition extends PortalFSCondition{
	public final OperType operType;
	public final String operValue;
	public PortalItemCondition(String field, OperType operType,
			String operValue) throws Exception{
		super(field);
		this.operType = operType;
		this.operValue = operValue;
	}

	public void appendToList(List<Token>list, final ConjunctType connType){
		list.add(new Token(TokenType.tSTART, this.operType.toString()));
		list.add(new Token(TokenType.tCONTENT, connType.toString()));
		list.add(new Token(TokenType.tCONTENT, super.field.toString()));
		list.add(new Token(TokenType.tCONTENT, operValue.toString()));
		list.add(new Token(TokenType.tEND, ""));
	}

	@Override
	public String toString(){
		return String.format(" %s%s'%s'", 
				this.field, 
				this.operType, 
				this.operValue);
	}
}

