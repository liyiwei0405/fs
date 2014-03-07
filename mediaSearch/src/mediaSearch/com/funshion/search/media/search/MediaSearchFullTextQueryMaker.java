package com.funshion.search.media.search;

import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;

import com.funshion.luc.defines.FullTextQueryMaker;
import com.funshion.search.media.chgWatcher.FieldDefine;
import com.funshion.search.media.search.mediaTitleRewriter.CnNumRewrite;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixNumFormatResult;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixSpecialWordTrimer;
import com.funshion.search.segment.RMMSegment;


public class MediaSearchFullTextQueryMaker extends FullTextQueryMaker{

	@Override
	public Query makeTitleQuery(char field, String toSearch) {

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
					new Term(Character.toString(field), str), mpf);

			bqWords.add(tq, Occur.SHOULD);
		}
		return bqWords;

	}

	@Override
	public Query makeTitleFullQuery(char field, String toSearch) {

		final String full = toSearch;

		BooleanQuery bqxTmp = new BooleanQuery();

		if(full.length() > 0){
			Query qTmp = null;
			MaxPayloadFunction mpf =  new MaxPayloadFunction();
			qTmp = new PayloadTermQuery(
					new Term(Character.toString(field), full + FieldDefine.FULL_NAME_END), mpf);
			bqxTmp.add(qTmp, Occur.SHOULD);

			String fullRew = CnNumRewrite.cnNumTryRewrite(full);
			if(fullRew != null){
				qTmp = new PayloadTermQuery(
						new Term(Character.toString(field), fullRew + FieldDefine.FULL_NAME_END), mpf);
				bqxTmp.add(qTmp, Occur.SHOULD);
			}
			
			String[] trimed = MediaTitleSuffixSpecialWordTrimer.instance.trim(toSearch);
			MediaTitleSuffixNumFormatResult mr = MediaTitleSuffixNumFormatResult.rewriteTitle(trimed == null ? full : trimed[0]);

			if(trimed != null){
				MaxPayloadFunction mpf2 = new MaxPayloadFunction();
				String alias = MediaTitleSuffixSpecialWordTrimer.instance.getAlia(trimed[1]);
				qTmp = new PayloadTermQuery(new Term(Character.toString(field), trimed[0] + " " + alias + FieldDefine.FULL_NAME_END), mpf2);
				bqxTmp.add(qTmp, Occur.SHOULD);

				qTmp = new PayloadTermQuery(new Term(Character.toString(field), trimed[0] + FieldDefine.FULL_NAME_END), mpf2);
				bqxTmp.add(qTmp, Occur.SHOULD);

				String rew = CnNumRewrite.cnNumTryRewrite(trimed[0]);
				if(rew!= null){
					qTmp = new PayloadTermQuery(new Term(Character.toString(field), rew+ " " + alias + FieldDefine.FULL_NAME_END), mpf2);
					bqxTmp.add(qTmp, Occur.SHOULD);
					//							qTmp = tq2;
				}
			}


			if(mr != null){
				MaxPayloadFunction mpf2 =  new MaxPayloadFunction();
				qTmp = new PayloadTermQuery(new Term(Character.toString(field), mr.norm() + FieldDefine.FULL_NAME_END), mpf2);
				bqxTmp.add(qTmp, Occur.SHOULD);

				qTmp = new PayloadTermQuery(new Term(Character.toString(field), mr.getNewNameCn() + FieldDefine.FULL_NAME_END), mpf2);
				bqxTmp.add(qTmp, Occur.SHOULD);
				//						qTmp = tq2;
				String rew = CnNumRewrite.cnNumTryRewrite(mr.norm());
				if(rew!= null){
					qTmp = new PayloadTermQuery(new Term(Character.toString(field), rew + FieldDefine.FULL_NAME_END), mpf2);
					bqxTmp.add(qTmp, Occur.SHOULD);
					//							qTmp = tq2;
				}
			}
		}
		return bqxTmp;

	}

}
