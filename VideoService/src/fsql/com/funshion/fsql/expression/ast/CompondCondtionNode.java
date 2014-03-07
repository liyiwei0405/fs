package com.funshion.fsql.expression.ast;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.funshion.fsql.expression.FsqlInterpreter;

public class CompondCondtionNode extends QueryMakeableConditionNode{
	final ConditionNode thisCond;
	public CompondCondtionNode(int position, ConditionNode condNodeSub) {
		super(position);
		thisCond = condNodeSub;
	}

	@Override
	public String toString(){
		return this.myConjunct +  "(" + thisCond + ")" + super.appendString();
	}

	@Override
	public Query toQueryElement(FsqlInterpreter interpreter) throws Exception {
		BooleanQuery bq = new BooleanQuery();
		QueryMakeableConditionNode node = (QueryMakeableConditionNode) this.thisCond;
		while(node != null){
			Query sub;
			if(node instanceof CompondCondtionNode){
				sub = ((CompondCondtionNode)node).toQueryElement(interpreter);
			}else{
				sub = node.toQueryElement(interpreter);
			}
			bq.add(sub, node.occur());
			node = (QueryMakeableConditionNode) node.nextCondition;
		}
		return bq;
	}
}
