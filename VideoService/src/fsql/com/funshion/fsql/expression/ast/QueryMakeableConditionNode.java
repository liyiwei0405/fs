package com.funshion.fsql.expression.ast;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.funshion.fsql.expression.FsqlInterpreter;
import com.funshion.fsql.expression.TokenType;

public abstract class QueryMakeableConditionNode extends ConditionNode implements QueryMakable{

	public QueryMakeableConditionNode(int position) {
		super(position);
	}

	public Occur occur(){
		if(this.myConjunct.type == TokenType.AND){
			return Occur.MUST;
		}else if(this.myConjunct.type == TokenType.OR){
			return Occur.SHOULD;
		}else if(this.myConjunct.type == TokenType.NOT){
			return Occur.MUST_NOT;
		}else{
			throw new RuntimeException("unknown ocur type for " + this);
		}
	}
	
	public Query colletQuery(
			FsqlInterpreter interpreter) throws Exception {
		BooleanQuery bq = new BooleanQuery();
		QueryMakeableConditionNode nodeToMake = this;
		while(nodeToMake != null){
			Query sub = nodeToMake.toQueryElement(interpreter);
			bq.add(sub, nodeToMake.occur());
			nodeToMake = (QueryMakeableConditionNode) nodeToMake.nextCondition;
		}
		return bq;
	}
}
