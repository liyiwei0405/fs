package com.funshion.videoService.search;

import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;

import com.funshion.luc.defines.FullTextQueryMaker;
import com.funshion.search.media.search.mediaTitleRewriter.CnNumRewrite;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixSpecialWordTrimer;
import com.funshion.search.segment.RMMSegment;


public class VideoletFullTextQueryMaker extends FullTextQueryMaker{

	@Override
	public Query makeTitleQuery(String field, String toSearch) {

		String[] trimed = null;

		BooleanQuery bqWords = new BooleanQuery();
		trimed = MediaTitleSuffixSpecialWordTrimer.instance.trim(toSearch);
		if(trimed != null){
			toSearch = trimed[0];
		}

		toSearch = CnNumRewrite.cnNumRewrite(toSearch);
		Set<String>set = RMMSegment.instance.noCoverSegment(toSearch).toQuerySet();

		for(String str : set){
			if(str.length() == 0){
				continue;
			}
			MaxPayloadFunction mpf =  new MaxPayloadFunction();
			PayloadTermQuery tq = new PayloadTermQuery(
					new Term(field, str), mpf);

			bqWords.add(tq, Occur.SHOULD);
		}
		return bqWords;
	}

	@Override
	public Query makeTitleFullQuery(String field, String toSearch) throws Exception {
		throw new Exception("not support complete matching");
	}

}
