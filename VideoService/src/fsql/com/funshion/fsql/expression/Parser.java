package com.funshion.fsql.expression;

import java.util.ArrayList;
import java.util.List;

import com.funshion.fsql.expression.TokenType.TokenClass;
import com.funshion.fsql.expression.ast.CompondCondtionNode;
import com.funshion.fsql.expression.ast.ConditionNode;
import com.funshion.fsql.expression.ast.ConjunctNode;
import com.funshion.fsql.expression.ast.EndNode;
import com.funshion.fsql.expression.ast.FieldNameNode;
import com.funshion.fsql.expression.ast.FiledValueNode;
import com.funshion.fsql.expression.ast.ItemConditionNode;
import com.funshion.fsql.expression.ast.Node;
import com.funshion.fsql.expression.ast.QueryMakeableConditionNode;

public class Parser {
	// Look ahead buffer for reading tokens from the lexer
	private List<ItemConditionNode>var = new ArrayList<ItemConditionNode>();
	QueryMakeableConditionNode conds;
	TokenBuffer lookAheadBuffer;
	final Lexer lexer;
	private int resultOffset = -1;
	private int resultLimit = -1;
	private List<OrderInfo>orders = new ArrayList<OrderInfo>();
	public Parser(Lexer lexer) {
		this.lexer = lexer;
		lookAheadBuffer = new TokenBuffer(lexer, 2);
	}

	private TokenType lookAhead(int i) {
		if (lookAheadBuffer.isEmpty() || i > lookAheadBuffer.size()) {
			return null; // EOF
		}
		Token token = lookAheadBuffer.getToken(i - 1); // 1-based index
		return token.type;
	}
	private Token typeMatch(TokenClass tokenClass){
		Token token = lookAheadBuffer.readToken();
		if (token == null) {
			throw new ParserException("Expecting tokenClass " + tokenClass + " but didn't get a token");
		}
		if (token.type.tokenClass != tokenClass) {
			throw new ParserException("Expecting tokenClass " + tokenClass + " but got '" + token.text + "'", token.getPosition());
		}
		return token;
	}
	private Token match(TokenType tokenType) {
		Token token = lookAheadBuffer.readToken();
		if (token == null) {
			throw new ParserException("Expecting type " + tokenType + " but didn't get a token");
		}
		if (token.type != tokenType) {
			throw new ParserException("Expecting type " + tokenType + " but got " + token.type, token.getPosition());
		}
		return token;
	}

	public void program() {
		conds = statements(null);
		matchOrderByLimit();
	}
	
	void addOrderInfo(Token field, boolean asc){
		this.orders.add(new OrderInfo(field, asc));
	}
	private void matchOrderByLimit() {
		match(TokenType.ORDER);
		match(TokenType.BY);

		while(true){
			TokenType lh = lookAhead(1);

			if(lh == TokenType.LIMIT){
				break;
			}
			if(lh == TokenType.COMMA){
				lookAheadBuffer.readToken();
			}
			Token field = match(TokenType.VARIABLE);
			TokenType orderType = lookAhead(1);
			if(orderType == TokenType.ASC){
				this.orders.add(new OrderInfo(field, true));
				lookAheadBuffer.readToken();
			}else if(orderType == TokenType.DESC){
				this.orders.add(new OrderInfo(field, false));
				lookAheadBuffer.readToken();
			}else if(orderType == TokenType.COMMA){
				this.orders.add(new OrderInfo(field, true));
			}else if(orderType == TokenType.LIMIT || orderType == TokenType.COMMA){
				this.orders.add(new OrderInfo(field, true));
				break;
			}else{
				throw new ParserException("expect asc/desc/limit but got " + orderType + " at " + orderType);
			}
		}
		if(orders.size() == 0){
			throw new ParserException("collect matchOrderByLimit fail! no 'order by'" );
		}
		matchLimit();
	}
	OrderInfo collectOrderBy(){
		Token field = match(TokenType.VARIABLE);
		TokenType orderType = lookAhead(1);
		if(orderType == TokenType.ASC){
			return new OrderInfo(field, true);
		}else if(orderType == TokenType.DESC){
			return new OrderInfo(field, false);
		}else if(orderType == TokenType.LIMIT){
			return new OrderInfo(field, true);
		}else{
			return null;
		}
	}

	private void matchLimit() {
		match(TokenType.LIMIT);
		Token limit = lookAheadBuffer.readToken();
		TokenType next = lookAhead(1);
		if(next == null){
			resultOffset = 0;
			if(limit.type == TokenType.NUMBER){
				resultLimit = Integer.parseInt(limit.text);
			}else if(limit.type != TokenType.VALUEUNSET){
				throw new ParserException("expect @num or @?, but got " + limit + " at " + limit.getPosition());
			}
		}else if(next == TokenType.COMMA){
			match(TokenType.COMMA);
			Token tLimit = lookAheadBuffer.readToken();
			if(limit.type == TokenType.NUMBER){
				resultOffset = Integer.parseInt(limit.text);
			}else if(limit.type != TokenType.VALUEUNSET){
				throw new ParserException("expect @num or @?, but got " + limit + " at " + limit.getPosition());
			}
			if(tLimit.type == TokenType.NUMBER){
				resultLimit = Integer.parseInt(tLimit.text);
			}else if(tLimit.type != TokenType.VALUEUNSET){
				throw new ParserException("expect @num or @?, but got " + tLimit + " at " + limit.getPosition());
			}
			if(lookAhead(1) != null){
				throw new ParserException("expect no more token, but got " + lookAhead(1) + " at " + lookAheadBuffer.getToken(0).getPosition());
			}
		}else{
			throw new ParserException("expect asc/desc/limit but got " + next + " at " + lookAheadBuffer.getToken(0).getPosition());
		}
	}
	private QueryMakeableConditionNode statements(ConjunctNode conjunctNode) {
		TokenType type = lookAhead(1);
		QueryMakeableConditionNode condNode;
		if(type.tokenClass == TokenClass.CONJUNCT){
			if(conjunctNode == null){
				Token t = this.lookAheadBuffer.readToken();
				return statements(new ConjunctNode(t.getPosition(), t.type));
			}else{
				throw new ParserException("Error ConjunctNode after ConjunctNode: " + this.lookAheadBuffer.getToken(1));
			}
		}else if(type == TokenType.LPAREN){
			this.lookAheadBuffer.readToken();
			ConditionNode condNodeSub = statements(null); 
			int pos = match(TokenType.RPAREN).getPosition();
			condNode = new CompondCondtionNode(pos, condNodeSub);
		}else if (type == TokenType.VARIABLE){
			if(lookAhead(2) == TokenType.LPAREN) {
				//				Node funcCall = functionCall();
				throw new ParserException("not support function!");
			}else{
				Token variable = match(TokenType.VARIABLE);
				FieldNameNode fieldName = 
						new FieldNameNode(variable.getPosition(), variable.text);
				Token relation = this.typeMatch(TokenClass.RELATION);
				Token valueToken = lookAheadBuffer.readToken();
				FiledValueNode valueNode;
				boolean isVar = false;
				if(valueToken.type == TokenType.STRING_LITERAL){
					valueNode = new FiledValueNode(valueToken.getPosition(), valueToken.text);
				}else if(valueToken.type == TokenType.NUMBER){
					valueNode = new FiledValueNode(valueToken.getPosition(), valueToken.text);
				}else if(valueToken.type == TokenType.VALUEUNSET){
					valueNode = new FiledValueNode(valueToken.getPosition(), TokenType.VALUEUNSET);
					isVar = true;
				}else {
					throw new ParserException("expect value or '?', but get " + valueToken + " @" + valueToken.getPosition());
				}
				ItemConditionNode condNodeX = new ItemConditionNode(valueToken.getPosition(), fieldName, relation, valueNode);
				if(isVar){
					this.var.add(condNodeX);
				}
				condNode = condNodeX;
			} 
		}else if (type == TokenType.END_STATEMENT || type == TokenType.ORDER) {
			return EndNode.instance;
		} else {
			throw new ParserException("Unknown token type " + type);
		}
		condNode.myConjunct = conjunctNode;
		Node next = checkConjunct();

		if(next != null && next != EndNode.instance){//sub in '()'
			this.lookAheadBuffer.readToken();
			ConditionNode nextNode = statements((ConjunctNode) next);
			if(nextNode == EndNode.instance){
				throw new ParserException("unexpect End-Of-Query-Block");
			}
			condNode.nextCondition = nextNode;
		}
		if(condNode.myConjunct == null){
			if(condNode.nextCondition != null && condNode.nextCondition.myConjunct.type == TokenType.OR){
				condNode.myConjunct = new ConjunctNode(condNode.getPosition(), TokenType.OR);
			}else{
				condNode.myConjunct = new ConjunctNode(condNode.getPosition(), TokenType.AND);
			}
		}
		return condNode;
	}

	private Node checkConjunct() {
		if(this.lookAheadBuffer.isEmpty() || this.lookAheadBuffer.getToken(0).type == TokenType.ORDER){
			return EndNode.instance;
		}
		Token t = this.lookAheadBuffer.getToken(0);
		if(t.type.tokenClass == TokenClass.CONJUNCT){
			return new ConjunctNode(t.getPosition(), t.type);
		}else if(t.type == TokenType.RPAREN){
			return null;//error?
		}else{
			throw new ParserException("expect Connjunct or ')' but get " + this.lookAheadBuffer.getToken(0) + " at " + lookAheadBuffer.getToken(0).getPosition());
		}
	}

	public List<ItemConditionNode> getVar() {
		return var;
	}

	public List<OrderInfo> getOrders() {
		return orders;
	}

	public int getResultOffset() {
		return resultOffset;
	}

	public void setResultOffset(int resultOffset) {
		this.resultOffset = resultOffset;
	}

	public int getResultLimit() {
		return resultLimit;
	}

	public void setResultLimit(int resultLimit) {
		this.resultLimit = resultLimit;
	}

}
