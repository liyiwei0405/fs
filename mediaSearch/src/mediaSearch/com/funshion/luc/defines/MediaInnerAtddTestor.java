package com.funshion.luc.defines;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import com.funshion.retrieve.media.QueryParseTool;
import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.retrieve.media.thrift.LimitRetrieve;
import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.search.media.chgWatcher.FieldDefine;
import com.funshion.search.utils.LogHelper;

public class MediaInnerAtddTestor implements AtddTestor{

	public static final LogHelper log = new LogHelper("atdd");
	@Override
	public void AtddTest(AbstractSearcher searcherIns) throws Exception {
		SSearcher searcher = (SSearcher) searcherIns;
		long allMatchStartMs = System.currentTimeMillis();
		TopDocs docs = searcher.search(new MatchAllDocsQuery(), 
				Sort.INDEXORDER, 1000000);
		log.info("MatchAllDocsQuery use %sms got %s records", (System.nanoTime() - allMatchStartMs)/ 1000/ 100 /10.0, docs.totalHits);
		List<RetrieveStruct> rqs = testcases();

		for(RetrieveStruct qs : rqs){
			MediaRetrieveResult mr = searcher.query(qs);
			log.info("query total result %s, use %sms for %s", mr.total, mr.usedTime, qs);

		}
	}

	public ArrayList<RetrieveStruct> testcases() throws Exception{
		ArrayList<RetrieveStruct>lst = new ArrayList<RetrieveStruct>();
		String testWords [] = new String[]{
				"的","大话西游之月光宝盒","武林外传","笑傲江湖", "天龙八部", ""
		};

		for(String x : testWords){
			RetrieveStruct qs = new RetrieveStruct();
			LimitRetrieve lb = new LimitRetrieve(0, 1000);
			PortalCompoundCondition conds = new PortalCompoundCondition();
			conds.addCondition(ConjunctType.OR, new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
					OperType.SEARCH_TITLE_FULL,
					x));
			conds.addCondition(ConjunctType.OR, new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
					OperType.SEARCH_TITLE,
					x));
			qs.conditions = QueryParseTool.toList(conds);
			qs.limits = lb;
			lst.add(qs);
		}
		return lst;
	}


}
