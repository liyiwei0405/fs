package com.funshion.search.videolet.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.funshion.search.segment.RMMSegment;
import com.funshion.search.segment.TokenHandler;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.videolet.thrift.FieldFilter;
import com.funshion.search.videolet.thrift.FieldWeight;
import com.funshion.search.videolet.thrift.LimitBy;
import com.funshion.search.videolet.thrift.QueryStruct;
import com.funshion.search.videolet.thrift.RangeQuery;
import com.funshion.search.videolet.thrift.SortBy;
import com.funshion.search.videolet.thrift.VideoletResult;
import com.funshion.search.videolet.thrift.VideoletSearchResult;

public class VideoletSearcher {
	
	final IndexSearcher sIns2;
	public final LogHelper log = new LogHelper("ss");
	public VideoletSearcher(IndexReader ir) throws IOException{
		sIns2 = new IndexSearcher(ir);
	}

	public static SortField getSortField(String key, boolean reverse){
		if(key.equals("@r")){
			return SortField.FIELD_SCORE;
		}else if(key.equals(VideoletIndexer.INDEX_MODIFY_DATE)){
			return new SortField(key, SortField.Type.INT, reverse);
		}else if(key.equals(VideoletIndexer.INDEX_PLAY_NUM)){
			return new SortField(key, SortField.Type.INT, reverse);
		}else if(key.equals(VideoletIndexer.INDEX_CREATE_DATE)){
			return new SortField(key, SortField.Type.INT, reverse);
		}else {
			return null;
		}
	}

	protected TopDocs searchMay(String toSearch, int topN, Sort sort, float titleWeight, float tagsWeight, int fidFlt) throws IOException{
		TokenHandler handler = RMMSegment.instance.segment(toSearch);

		BooleanQuery bq = new BooleanQuery();
		while(handler.hasNext()){
			String str = handler.next().trim().toLowerCase();
			if(str.length() == 0){
				continue;
			}
			TermQuery tqTitle = new TermQuery(new Term(VideoletIndexer.INDEX_TITLE, str));
			if(titleWeight > 1){
				tqTitle.setBoost(titleWeight);
			}

			TermQuery tqTags = new TermQuery(new Term(VideoletIndexer.INDEX_TAGS, str));
			if(tagsWeight > 1){
				tqTags.setBoost(titleWeight);
			}
			bq.add(tqTitle, Occur.SHOULD);
			bq.add(tqTags, Occur.SHOULD);
		}
		if(fidFlt != -1){
			BooleanQuery bq2 = new BooleanQuery();
			BytesRef br = new BytesRef();
			NumericUtils.intToPrefixCoded(fidFlt, 0, br);
			TermQuery typeFilter = new TermQuery(new Term(VideoletIndexer.INDEX_VIDEO_OR_UGC, br));
			bq2.add(typeFilter, Occur.MUST);
			bq2.add(bq, Occur.MUST);
			bq = bq2;
		}
		log.debug("query is topN %s, %s", topN, bq);
		if(sort == null){
			return this.sIns2.search(bq, topN);
		}else{
			return this.sIns2.search(bq, topN, sort);
		}
		
	}

	public VideoletSearchResult query(QueryStruct qs) throws Exception {
		log.debug("raw query is %s", qs);
		long prepareStartTime = System.nanoTime();

		List<SortBy> sbs = qs.sortFields;
		Sort sort = null;
		int resultOffset = 0, resultLimit = 0;
		int typeFlt = -1;//FIXME not support yet
		float titleWeight = 20f;
		float tagsWeight = 1f;
		if(sbs != null){
			if(sbs.size() > 0){
				ArrayList<SortField> sfs = new ArrayList<SortField>();
				for(SortBy sby : sbs){
					SortField sf = getSortField(sby.field, !sby.asc);
					if(sf == null){
						log.error("unknown sort setting %s:%s", sby);
						continue;
					}
					sfs.add(sf);
				}
				if(sfs.size() > 0){
					SortField[] sf = new SortField[sfs.size()];
					sfs.toArray(sf);
					sort = new Sort(sf);
				}
				//public static final int SPH_SORT_EXTENDED = 4;
			}
		}
		List<FieldWeight> fws = qs.weights;

		if(fws != null){
			if(fws.size() > 0){
				for(FieldWeight fw : fws){
					if(VideoletIndexer.INDEX_TITLE.equals(fw.fieldname)){
						titleWeight = (float) fw.weight;
					}else if(VideoletIndexer.INDEX_TAGS.equals(fw.fieldname)){
						tagsWeight = (float) fw.weight;
					}else{
						log.error("unknown sort field setting %s", fw);
					}
				}
			}
		}
		List<FieldFilter> flts = qs.filters;
		if(flts != null){
			for(FieldFilter ff : flts){
				if(VideoletIndexer.INDEX_VIDEO_OR_UGC.equals(ff.fieldName) && !ff.exclude){
					typeFlt = ff.fieldValue;
				}else{
					log.error("unsupport FieldFilter %s:%s", ff.fieldName, ff.fieldValue);
				}
			}
		}
		LimitBy lb = qs.limits;
		if(lb != null){
			resultLimit = lb.limit;
			resultOffset = lb.offset;
		}
		if(resultLimit < 1){
			resultLimit = 30;
		}
		List<RangeQuery> rqs = qs.rqs;
		if(rqs != null){
			for(RangeQuery rq : rqs){
				log.error("not support rangeQuery %s", rq);
			}
		}

		TopDocs tfds =  searchMay(qs.word, resultOffset + resultLimit, sort, titleWeight, tagsWeight, typeFlt);
		VideoletSearchResult vsr = new VideoletSearchResult();
		vsr.ids = new ArrayList<VideoletResult>();

		int resultEnd = Math.min(tfds.totalHits, resultOffset + resultLimit);

		Document doc;
		for(int x = resultOffset; x < resultEnd; x ++){
			VideoletResult vr = new VideoletResult();
			doc = sIns2.doc(tfds.scoreDocs[x].doc);
			if(doc == null){
				continue;
			}
			vr.videoid = doc.getField(VideoletIndexer.INDEX_VIDEO_ID).numericValue().intValue();
			vr.videotype = doc.getField(VideoletIndexer.INDEX_VIDEO_OR_UGC).numericValue().intValue();
			vsr.addToIds(vr);
		}
		long searchEndTime = System.nanoTime();
		vsr.status = 0;
		vsr.total = tfds.totalHits;
		vsr.usedTime = (searchEndTime - prepareStartTime)/1000 / 1000.0;
		return vsr;
	}
}
