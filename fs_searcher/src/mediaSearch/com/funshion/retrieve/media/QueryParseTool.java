package com.funshion.retrieve.media;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.portableCondition.PortalFSCondition;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.retrieve.media.portableCondition.PortalRangeCondition;
import com.funshion.retrieve.media.thrift.Token;
import com.funshion.retrieve.media.thrift.TokenType;

public class QueryParseTool {

	public static StoreType getType(PortalFSCondition cond){
		return StoreType.getType(cond.field.charAt(0));
	}
	public static Query buildQuery(PortalCompoundCondition cond) throws TypeOperationCheckFailException, Exception{
		return buildQuery(null, cond, Occur.MUST);
	}
	public static Query buildQuery(BooleanQuery bq, PortalFSCondition cond, Occur occ) throws TypeOperationCheckFailException, Exception{
		if(cond instanceof PortalRangeCondition){
			PortalRangeCondition THIS = (PortalRangeCondition) cond;
			NumericRangeQuery<Integer> rq = 
					NumericRangeQuery.newIntRange(THIS.field, 
							THIS.min, THIS.max, THIS.includeMin, THIS.includeMax);
			bq.add(rq, occ);
			return rq;
		}else if(cond instanceof PortalItemCondition){
			ItemCondition ic = new ItemCondition((PortalItemCondition) cond);
			Query icq = ic.buildQuery(bq, occ);
			return icq;
		}else if(cond instanceof PortalCompoundCondition){
			PortalCompoundCondition THIS = (PortalCompoundCondition) cond;

			BooleanQuery bqNow = new BooleanQuery();
			for(int x = 0; x < THIS.getQuery().size(); x ++){
				Object o = THIS.getQuery().get(x);
				ConjunctType ctype = THIS.getQueryType(x);
				if(o instanceof PortalFSCondition){
					buildQuery(bqNow, (PortalFSCondition) o, ctype.toOccurType());
				}else{
					throw new TypeOperationCheckFailException("unknown PortableCondition " + o);
				}
			}
			if(bq == null){
				return bqNow;
			}
			bq.add(bqNow, occ);
			return bq;
		
		}else{
			throw new TypeOperationCheckFailException("unknown PortableCondition " + cond);
		}
	}


	public static PortalCompoundCondition parseList(List<Token>list)throws Exception{
		//check is CompondCondithion
		if(TokenType.tLstSTART != list.get(0).type){
			throw new Exception("not compond condition " + list);
		}
		
		PortalCompoundCondition qlst = new PortalCompoundCondition();
		int ret = parseList(list, 0, qlst);
		if(ret != list.size()){
			throw new Exception("not end match! now " + ret + ", list size " + list.size());
		}
		return qlst;
	}
	private static int parseList(List<Token>list, int pos, PortalCompoundCondition qlst)throws Exception{
		TokenType nowType = list.get(pos).type;
		if(nowType != TokenType.tLstSTART){
			throw new Exception("expect " + TokenType.tLstSTART + " but get " + list.get(pos) + ", at pos " + pos);
		}
		
		pos += 2;
		while(true){
			pos = parse(list, pos, qlst);
			TokenType nextType = list.get(pos).type;
			if(nextType == TokenType.tLstEnd){
				++ pos;
				break;
			}else if(nextType == TokenType.tLstSTART || nextType == TokenType.tSTART){
				continue;
			}else{
				throw new Exception("expect end Token but get " + list.get(pos) + ", at pos " + pos);
			}
		}
		return pos;
	}
	private static int parse(List<Token>list, int pos, PortalCompoundCondition qlst)throws Exception{
		Token t = list.get(pos);
		OperType operType = OperType.findByValue(t.token);
		if(t.type == TokenType.tLstSTART){
			PortalCompoundCondition subLst = new PortalCompoundCondition();
			ConjunctType cType = ConjunctType.findByValue(list.get(1 + pos).token);
			pos = parseList(list, pos, subLst);
			qlst.addCondition(cType, subLst);
			return pos;
		}else if(t.type == TokenType.tSTART){
			PortalFSCondition ic;
			ConjunctType ctype = ConjunctType.findByValue(list.get(++ pos).token);
			if(operType == OperType.RANGE){
				ic = new PortalRangeCondition(
						list.get(++ pos).token,
						Integer.parseInt(list.get(++ pos).token),
						Integer.parseInt(list.get(++ pos).token),
						PortalRangeCondition.flag(list.get(++ pos).token),
						PortalRangeCondition.flag(list.get(++ pos).token));
			}else{
				ic = new PortalItemCondition(
						list.get(++ pos).token,
						operType,
						list.get(++ pos).token
						);
			}
			qlst.addCondition(ctype, ic);
			if(list.get(++ pos).type != TokenType.tEND){
				throw new Exception("expect end  Token but get " + list.get(pos)  + ", at pos " + pos);
			}
			return ++ pos;
		}else{
			throw new Exception("expect tSTART Token but get " + t + ", at pos " + pos);
		}
	}
	
	public static List<Token> toList(PortalCompoundCondition cond) throws Exception{
		List<Token>lst = new ArrayList<Token>();
		cond.appendToList(lst, ConjunctType.AND);
		return lst;
	}
}
