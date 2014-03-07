package com.funshion.fsql.expression;

import java.util.HashMap;

/**
 * The type of token
 *
 */
public enum TokenType {
	COMMA("@,", TokenClass.UNKNOWN),
	LPAREN("@(", TokenClass.GROUP), RPAREN("@)", TokenClass.GROUP),
	NOT("@!", TokenClass.CONJUNCT), AND("@&&", TokenClass.CONJUNCT), OR("@||", TokenClass.CONJUNCT), // relation operation operators
	LESS_THEN("@<", TokenClass.RELATION), LESS_EQUAL("@>", TokenClass.RELATION), EQUAL("=", TokenClass.RELATION), 
	GREATER_EQUAL("@>=", TokenClass.RELATION), GREATER_THEN("@<=", TokenClass.RELATION), NOT_EQUAL("@!=", TokenClass.RELATION), 
	SEARCH("@search", TokenClass.RELATION), LIKE("@like", TokenClass.RELATION),// Comparison operators
	IN("@IN", TokenClass.RELATION), 
	
	NUMBER("@num", TokenClass.UNKNOWN), STRING_LITERAL("@str", TokenClass.UNKNOWN), 
	TRUE("@true", TokenClass.UNKNOWN), FALSE("@false", TokenClass.UNKNOWN), // Constant types
	
	VARIABLE("@variable", TokenClass.UNKNOWN), 
//	FUNCTION("@function", TokenClass.UNKNOWN),
	ORDER("order", TokenClass.UNKNOWN),
    BY("by", TokenClass.UNKNOWN),
    LIMIT("limit", TokenClass.UNKNOWN),
    ASC("asc", TokenClass.UNKNOWN),
    DESC("desc", TokenClass.UNKNOWN),
	END_STATEMENT("@end", TokenClass.UNKNOWN), VALUEUNSET("?", TokenClass.NOTSETYET); 

	public final String str;
	public final TokenClass tokenClass;
	private TokenType(String str, TokenClass tc){
		this.str = str;
		this.tokenClass = tc;
	}
	@Override
	public String toString(){
		return str;
	}
	
	public enum TokenClass{
		UNKNOWN, RELATION, CONJUNCT, GROUP, NOTSETYET
	};
	
	
	static final HashMap<String, TokenType>condMap = new HashMap<String, TokenType>();
	static final HashMap<String, TokenType>conjunctMap = new HashMap<String, TokenType>();
	static{
		condMap.put("search", TokenType.SEARCH);
		condMap.put("like", TokenType.LIKE);
		condMap.put("in", TokenType.IN);
		conjunctMap.put("and", TokenType.AND);
		conjunctMap.put("or", TokenType.OR);
		conjunctMap.put("not", TokenType.NOT);
	}
	public static TokenType getConnjuctType(String str){
		return conjunctMap.get(str);
	}
	
	public static TokenType getConditionType(String str){
		return condMap.get(str);
	}
}
