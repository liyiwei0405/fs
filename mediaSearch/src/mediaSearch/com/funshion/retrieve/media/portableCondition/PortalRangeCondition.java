package com.funshion.retrieve.media.portableCondition;

import java.util.List;

import com.funshion.retrieve.media.thrift.Token;
import com.funshion.retrieve.media.thrift.TokenType;

public class PortalRangeCondition extends PortalFSCondition{
	public static final String incFlag = "bINC";
	public static final String excFlag = "bEXC";
	public static String flag(boolean b){
		return b ? incFlag : excFlag;
	}
	public static boolean flag(String str){
		if(incFlag.equals(str)){
			return true;
		}else if(incFlag.equals(str)){
			return true;
		}else{
			throw new RuntimeException("unknown inc/exc flag '" + str + "'");
		}
	}
	public final OperType operType;
	public final String min;
	public final String max;
	public boolean includeMin;
	public boolean includeMax;
	public PortalRangeCondition(String field,
			String min,
			String max,
			boolean includeMin,
			boolean includeMax) throws Exception{
		super(field);
		this.operType = OperType.RANGE;
		this.min = min;
		this.max = max;
		this.includeMin = includeMin;
		this.includeMax = includeMax;
	}

	public void appendToList(List<Token>list, final ConjunctType connType){
		list.add(new Token(TokenType.tSTART, this.operType.toString()));
		list.add(new Token(TokenType.tCONTENT, connType.toString()));
		list.add(new Token(TokenType.tCONTENT, field.toString()));
		list.add(new Token(TokenType.tCONTENT, min));
		list.add(new Token(TokenType.tCONTENT, max));
		list.add(new Token(TokenType.tCONTENT, flag(this.includeMin)));
		list.add(new Token(TokenType.tCONTENT, flag(this.includeMax)));
		list.add(new Token(TokenType.tEND, ""));
	}


	public String toString(){
		return String.format(" %s%s'%s'", 
				this.field, 
				this.operType, 
				this.operValue());
	}
	public String operValue(){
		String fmt = "%s%s, %s%s";
		return String.format(fmt, 
				flag(this.includeMin),
				this.min,
				flag(this.includeMax),
				this.max
				);
	}
}
