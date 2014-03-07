package com.funshion.retrieve.media.portableCondition;

import java.util.ArrayList;
import java.util.List;

import com.funshion.retrieve.media.QueryParseTool;
import com.funshion.retrieve.media.thrift.Token;
import com.funshion.retrieve.media.thrift.TokenType;
/**
 * QCGList 解析：
 * <br>1. 逐个操作符解析
 * <br>2. 如果碰到tStart, 开始BooleanQuery
 * <br>3. 如果碰到tEnd，接受booleanQuery
 * <br>4. 递归解析直至完成
 * @author liying
 *
 */
public class PortalCompoundCondition extends PortalFSCondition{
	
	private List<Object> query = new ArrayList<Object>();
	private List<ConjunctType> queryType = new ArrayList<ConjunctType>();
	public PortalCompoundCondition(){
		super(null);
	}
	public synchronized void addCondition(ConjunctType conType, PortalFSCondition cond){
		this.query.add(cond);
		this.queryType.add(conType);
	}

	public void appendToList(List<Token>list, final ConjunctType connType) throws Exception{
		list.add(new Token(TokenType.tLstSTART, OperType.COMPOUND.toString()));
		list.add(new Token(TokenType.tCONTENT, connType.toString()));
		for(int x = 0; x < query.size(); x ++){
			Object o = this.query.get(x);
			if(o instanceof PortalCompoundCondition){
				PortalCompoundCondition o2 = (PortalCompoundCondition)o;
				if(o2.query.size() == 0){
					throw new Exception("booleanList must contains Query!");
				}
				o2.appendToList(list, this.queryType.get(x));
			}else{
				((PortalFSCondition)o).appendToList(list, this.queryType.get(x));
			}
		}
		list.add(new Token(TokenType.tLstEnd, ""));
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(' ');
		sb.append('(');
		for(int x = 0; x < this.query.size(); x ++){
			Object o = this.query.get(x);
			sb.append(this.queryType.get(x));
			sb.append(o);
		}
		sb.append(')');
		return sb.toString();
	}
	public List<Object> getQuery() {
		return query;
	}
	public ConjunctType getQueryType(int x) {
		return queryType.get(x);
	}
	
	public List<Token> toTokenList() throws Exception{
		return QueryParseTool.toList(this);
	}
	
	public PortalCompoundCondition clone(){
		PortalCompoundCondition conds = new PortalCompoundCondition();
		conds.query.addAll(query);
		conds.queryType.addAll(queryType);
		return conds;
	}
}
