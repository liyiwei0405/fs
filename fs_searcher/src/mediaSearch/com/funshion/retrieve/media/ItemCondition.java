package com.funshion.retrieve.media;

import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.search.analyzers.CharacterFormat;
import com.funshion.search.media.search.FieldDefine;
import com.funshion.search.media.search.mediaTitleRewriter.CnNumRewrite;
import com.funshion.search.media.search.mediaTitleRewriter.F2JConvert;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixNumFormatResult;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixSpecialWordTrimer;
import com.funshion.search.segment.RMMSegment;

public class ItemCondition{
	public static Occur getOccur(ConjunctType connType){
		return connType.toOccurType();
	}

	public StoreType getType(){
		return StoreType.getType(itmCond.field.charAt(0));
	}
	final PortalItemCondition itmCond;
	public ItemCondition(PortalItemCondition cond) throws Exception{
		this.itmCond = cond;
		typeCheck();
	}

	public static boolean accOperType(OperType type){
		if(type == OperType.EQUAL || 
				type == OperType.LIKE ||
				type == OperType.SEARCH_AND ||
				type == OperType.SEARCH_OR ||
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

		StoreType st = getType();

		if(st == StoreType.INT){
			try{
				Integer.parseInt(itmCond.operValue);
			}catch(Exception e){
				throw new TypeOperationCheckFailException(
						"INT type store but can not parse value '" + itmCond.operValue + "'");
			}
			if(itmCond.operType == OperType.LIKE || itmCond.operType == OperType.SEARCH_AND
					|| itmCond.operType == OperType.SEARCH_OR
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
		StoreType storeType = this.getType();
		if(storeType == StoreType.INT){
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

		}else if(storeType == StoreType.TXT){

			String toSearch = itmCond.operValue.toLowerCase();
			toSearch = F2JConvert.instance.conver(toSearch);
			toSearch =  CharacterFormat.rewriteString(toSearch);
			if(itmCond.operType == OperType.EQUAL){
				Term term = new Term(itmCond.field, itmCond.operValue);
				Query q = new TermQuery(term);
				bqPar.add(q, occ);
				return q;
			}else if(itmCond.operType == OperType.LIKE){
				Query q = new TermQuery(new Term(itmCond.field, toSearch));
				bqPar.add(q, occ);
				return q;
			}else if(itmCond.operType == OperType.SEARCH_AND){
				throw new Exception("not support operator " + OperType.SEARCH_AND);
			}else if(itmCond.operType == OperType.SEARCH_OR){
				throw new Exception("not support operator " + OperType.SEARCH_OR);
			}else if(itmCond.operType == OperType.SEARCH_TITLE){
				final String full = toSearch;
				String[] trimed = null;
				MediaTitleSuffixNumFormatResult mr = null;

				BooleanQuery bqWords = new BooleanQuery();

				trimed = MediaTitleSuffixSpecialWordTrimer.instance.trim(toSearch);
				if(trimed != null){//检测后缀，去掉后缀
					toSearch = trimed[0];
				}
				mr = MediaTitleSuffixNumFormatResult.rewriteTitle(trimed == null ? full : trimed[0]);
				if(mr != null){//重写剧集后缀，如果重写成功则将指针指向重写后的内容
					toSearch = mr.getNewNameCn();
				}

				toSearch = CnNumRewrite.cnNumRewrite(toSearch);//数字形式归一
				Set<String>set = RMMSegment.instance.noCoverSegment(toSearch).toQuerySet();

				for(String str : set){
					if(str.length() == 0){
						continue;
					}
					MaxPayloadFunction mpf =  new MaxPayloadFunction();
					PayloadTermQuery tq = new PayloadTermQuery(
							new Term(itmCond.field, str), mpf);

					bqWords.add(tq, Occur.SHOULD);
				}

				bqPar.add(bqWords, occ);
				return bqWords;

			}else if(itmCond.operType == OperType.SEARCH_TITLE_FULL){

				final String full = toSearch;

				BooleanQuery bqxTmp = new BooleanQuery();

				if(full.length() > 0){
					Query qTmp = null;
					MaxPayloadFunction mpf =  new MaxPayloadFunction();
					qTmp = new PayloadTermQuery(
							new Term(itmCond.field, full + FieldDefine.FULL_NAME_END), mpf);
					bqxTmp.add(qTmp, Occur.SHOULD);
					
					String[] trimed = MediaTitleSuffixSpecialWordTrimer.instance.trim(toSearch);
					MediaTitleSuffixNumFormatResult mr = MediaTitleSuffixNumFormatResult.rewriteTitle(trimed == null ? full : trimed[0]);

					if(trimed != null){
						MaxPayloadFunction mpf2 = new MaxPayloadFunction();
						String alias = MediaTitleSuffixSpecialWordTrimer.instance.getAlia(trimed[1]);
						qTmp = new PayloadTermQuery(new Term(itmCond.field, trimed[0] + " " + alias + FieldDefine.FULL_NAME_END), mpf2);
						bqxTmp.add(qTmp, Occur.SHOULD);
						
						qTmp = new PayloadTermQuery(new Term(itmCond.field, trimed[0] + FieldDefine.FULL_NAME_END), mpf2);
						bqxTmp.add(qTmp, Occur.SHOULD);
						
						String rew = CnNumRewrite.cnNumTryRewrite(trimed[0]);
						if(rew!= null){
							qTmp = new PayloadTermQuery(new Term(itmCond.field, rew+ " " + alias + FieldDefine.FULL_NAME_END), mpf2);
							bqxTmp.add(qTmp, Occur.SHOULD);
//							qTmp = tq2;
						}
					}


					if(mr != null){
						MaxPayloadFunction mpf2 =  new MaxPayloadFunction();
						qTmp = new PayloadTermQuery(new Term(itmCond.field, mr.norm() + FieldDefine.FULL_NAME_END), mpf2);
						bqxTmp.add(qTmp, Occur.SHOULD);
						
						qTmp = new PayloadTermQuery(new Term(itmCond.field, mr.getNewNameCn() + FieldDefine.FULL_NAME_END), mpf2);
						bqxTmp.add(qTmp, Occur.SHOULD);
//						qTmp = tq2;
						String rew = CnNumRewrite.cnNumTryRewrite(mr.norm());
						if(rew!= null){
							qTmp = new PayloadTermQuery(new Term(itmCond.field, rew + FieldDefine.FULL_NAME_END), mpf2);
							bqxTmp.add(qTmp, Occur.SHOULD);
//							qTmp = tq2;
						}
					}
				}
				bqPar.add(bqxTmp, occ);
				return bqxTmp;

			}else{
				throw new TypeOperationCheckFailException("when compile to BooleanQuery, TXT store not support OperType " + itmCond.operType);
			}
		}else{
			throw new Exception("not support " + getType());
		}
	}

	
}

