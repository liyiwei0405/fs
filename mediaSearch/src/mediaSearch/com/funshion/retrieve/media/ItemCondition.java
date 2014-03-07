package com.funshion.retrieve.media;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.funshion.luc.defines.AnaType;
import com.funshion.luc.defines.FullTextQueryMaker;
import com.funshion.luc.defines.ITableDefine;
import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.search.analyzers.CharacterFormat;
import com.funshion.search.media.search.mediaTitleRewriter.F2JConvert;


public class ItemCondition{
	public static Occur getOccur(ConjunctType connType){
		return connType.toOccurType();
	}

	final PortalItemCondition itmCond;
	final AnaType aType;
	public ItemCondition(PortalItemCondition cond) throws Exception{
		this.itmCond = cond;
		aType = ITableDefine.instance.getAnaType(itmCond.field.charAt(0));
		typeCheck();
	}

	public static boolean accOperType(OperType type){
		if(type == OperType.EQUAL || 
				type == OperType.LIKE ||
				type == OperType.SEARCH_TITLE ||
				type == OperType.SEARCH_TITLE_FULL){
			return true;
		}
		return false;
	}



	public void typeCheck() throws Exception{

		if(itmCond.field == null || itmCond.field.length() != 1){
			throw new TypeOperationCheckFailException(
					itmCond.field == null? "null field" : "'" + itmCond.field + "'");
		}

		if(aType == AnaType.aInt){
			try{
				Integer.parseInt(itmCond.operValue);
			}catch(Exception e){
				throw new TypeOperationCheckFailException(
						"INT type store but can not parse value '" + itmCond.operValue + "'");
			}
			if(itmCond.operType == OperType.LIKE
					|| itmCond.operType == OperType.SEARCH_TITLE
					|| itmCond.operType == OperType.SEARCH_TITLE_FULL){
				throw new TypeOperationCheckFailException(
						"INT type store NOT support '" + itmCond.operType + "'");
			}
		}else if(aType == AnaType.aFloat){
			try{
				Float.parseFloat(itmCond.operValue);
			}catch(Exception e){
				throw new TypeOperationCheckFailException(
						"INT type store but can not parse value '" + itmCond.operValue + "'");
			}
			if(itmCond.operType == OperType.LIKE
					|| itmCond.operType == OperType.SEARCH_TITLE
					|| itmCond.operType == OperType.SEARCH_TITLE_FULL){
				throw new TypeOperationCheckFailException(
						"INT type store NOT support '" + itmCond.operType + "'");
			}
		}else if(aType == AnaType.aLong){
			try{
				Long.parseLong(itmCond.operValue);
			}catch(Exception e){
				throw new TypeOperationCheckFailException(
						"INT type store but can not parse value '" + itmCond.operValue + "'");
			}
			if(itmCond.operType == OperType.LIKE
					|| itmCond.operType == OperType.SEARCH_TITLE
					|| itmCond.operType == OperType.SEARCH_TITLE_FULL){
				throw new TypeOperationCheckFailException(
						"INT type store NOT support '" + itmCond.operType + "'");
			}
		}
		if(!accOperType(itmCond.operType)){
			throw new TypeOperationCheckFailException("not support OperType " + itmCond.operType);
		}
	}

	public Query buildQuery(BooleanQuery bqPar, Occur occ) throws TypeOperationCheckFailException, Exception {
		if(aType.isNumType()){
			BytesRef br = new BytesRef();
			NumericUtils.intToPrefixCoded(Integer.parseInt(itmCond.operValue), 0, br);
			Term term = new Term(itmCond.field, br);

			if(itmCond.operType == OperType.EQUAL){
				Query q = new TermQuery(term);
				bqPar.add(q, occ);
				return q;
			}else{
				throw new TypeOperationCheckFailException("when compile to BooleanQuery, INT store not support OperType " + itmCond.operType);
			}

		}else{ 
			if(aType == AnaType.aText){
				String toSearch = itmCond.operValue;
				toSearch = F2JConvert.instance.convert(toSearch);
				toSearch = CharacterFormat.rewriteString(toSearch);
				 if(itmCond.operType == OperType.LIKE){
					Query q = new TermQuery(new Term(itmCond.field, toSearch));
					bqPar.add(q, occ);
					return q;
				}else if(itmCond.operType == OperType.SEARCH_TITLE){
					FullTextQueryMaker ins = ITableDefine.instance.getFullTextQueryMaker();
					Query q = ins.makeTitleQuery(itmCond.field.charAt(0), toSearch);
					bqPar.add(q, occ);
					return q;
				}else if(itmCond.operType == OperType.SEARCH_TITLE_FULL){
					FullTextQueryMaker ins = ITableDefine.instance.getFullTextQueryMaker();
					Query q = ins.makeTitleFullQuery(itmCond.field.charAt(0), toSearch);
					bqPar.add(q, occ);
					return q;
				}else{
					throw new TypeOperationCheckFailException("when compile to BooleanQuery, aText " + itmCond.field.charAt(0) + " not support OperType " + itmCond.operType);
				}
			}else if(aType == AnaType.eStr){
				String toSearch = itmCond.operValue.toLowerCase().trim();
				if(itmCond.operType == OperType.EQUAL){
					Term term = new Term(itmCond.field, toSearch);
					Query q = new TermQuery(term);
					bqPar.add(q, occ);
					return q;
				}else{
					throw new TypeOperationCheckFailException("when compile to BooleanQuery, TXT store not support OperType " + itmCond.operType);
				}
			}else{
				throw new Exception("not support " + aType + " for "+ this.itmCond);
			}
		}
	}


}

