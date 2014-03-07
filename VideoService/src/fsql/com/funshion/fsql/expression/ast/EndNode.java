package com.funshion.fsql.expression.ast;

import org.apache.lucene.search.Query;

import com.funshion.fsql.expression.FsqlInterpreter;

public class EndNode extends QueryMakeableConditionNode{

	public static final EndNode instance = new EndNode();

	private EndNode() {
		super(-1);
	}


	@Override
	public String toString(){
		return "";
	}


	@Override
	public Query toQueryElement(FsqlInterpreter interpreter) throws Exception {
		return null;
	}

}
