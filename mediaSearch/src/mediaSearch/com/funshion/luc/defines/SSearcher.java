package com.funshion.luc.defines;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

import com.funshion.retrieve.media.QueryParseTool;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.thrift.LimitRetrieve;
import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.MediaRetrieveResultRecord;
import com.funshion.retrieve.media.thrift.MedieRetrieveResultItem;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.retrieve.media.thrift.SortRetrieve;

public class SSearcher extends AbstractSearcher{

	public SSearcher(IndexReader ir) throws IOException{
		super(ir);
	}
	public static PortalCompoundCondition toCondition(RetrieveStruct qs) throws Exception{
		return QueryParseTool.parseList(qs.conditions);
	}
	public static Sort getSort(List<SortRetrieve>list){
		SortField[]ss = new SortField[list.size()];
		for(int x = 0; x < ss.length; x ++){
			SortRetrieve sr = list.get(x);
			if(sr.field.equalsIgnoreCase(ITableDefine.RELEVENCE)){
				ss[x] = SortField.FIELD_SCORE;
			}else{
				//				System.out.println(sr.field);
				SortField.Type sf = ITableDefine.instance.getAnaType(sr.field.charAt(0)).toSortFieldType();

				if(sf == null){
					ss[x] = SortField.FIELD_SCORE;
				}else{
					ss[x] = new SortField(
							sr.field,
							sf,
							!sr.asc);
				}
			}
		}
		return new Sort(ss);
	}
	public static Sort toSort(RetrieveStruct qs) throws Exception{
		Sort sort;
		if(qs.sortFields == null || qs.sortFields.size() == 0){
			sort = ITableDefine.instance.getDefaultSort();
		}else{
			sort = getSort(qs.sortFields);
		}
		return sort;
	}
	public TopDocs search(RetrieveStruct qs, int topN) throws Exception{
		long time0 = System.nanoTime();
		PortalCompoundCondition conds = QueryParseTool.parseList(qs.conditions);
		Query bqx = QueryParseTool.buildQuery(conds);
		long time1 = System.nanoTime();
		Sort sort = toSort(qs);
		
		if(log.logger.isDebugEnabled()){
			log.debug("query %s with sort %s, topN %s", bqx, sort, topN);
		}
		if(log.logger.isDebugEnabled()){
			log.debug("search make-query use %s", (time1 - time0) / 10000/100.0);
		}
		TopDocs td = super.search(bqx, sort, topN);

		return td;
	}

	public TopFieldDocs search(Query q, Sort s, int topN) throws IOException{
		log.info("query is topN %s, %s, by %s", topN, q, sIns);
		return this.sIns.search(q, topN, s);
	}
	public MediaRetrieveResult query(RetrieveStruct qs) throws Exception {
		log.debug("raw query is %s", qs);
		long prepareStartTime = System.nanoTime();
		int resultOffset = 0, resultLimit = 0;
		LimitRetrieve lb = qs.limits;
		if(lb != null){
			resultLimit = lb.limit;
			resultOffset = lb.offset;
		}
		if(resultLimit < 1){
			resultLimit = 30;
		}


		TopDocs tfds = search(qs, resultOffset + resultLimit);
		double realS = (System.nanoTime() - prepareStartTime)/1000 / 1000.0;
		MediaRetrieveResult vsr = new MediaRetrieveResult();
		vsr.retCode = 200;
		vsr.retMsg = "OK";
		vsr.total = tfds.totalHits;
		vsr.ids = new ArrayList<MediaRetrieveResultRecord>();

		int resultEnd = Math.min(tfds.totalHits, resultOffset + resultLimit);

		Document doc;
		for(int x = resultOffset; x < resultEnd; x ++){
			MediaRetrieveResultRecord vr = new MediaRetrieveResultRecord();
			doc = sIns.doc(tfds.scoreDocs[x].doc);
			//			System.out.println(tfds.scoreDocs[x].score);

			if(doc == null){
				continue;
			}
			List<IndexableField> fields = doc.getFields();
			for(IndexableField f : fields){

				String name = f.name();
				if(name != null && name.length() == 1){
					char c = name.charAt(0);
					if(ITableDefine.instance.isNumField(c)){
						if(f.numericValue() == null){
							log.error("ERROR numericValue field:" + f.name());
							continue;
						}
						MedieRetrieveResultItem itmId = new MedieRetrieveResultItem(name,
								f.numericValue().toString());
						vr.addToItems(itmId);
					}else{
						MedieRetrieveResultItem itmId = new MedieRetrieveResultItem(name,
								f.stringValue());
						vr.addToItems(itmId);
					}
				}else{
					log.error("field length must be 1, error field %s", f);
				}
			}
			vsr.addToIds(vr);
		}
		long searchEndTime = System.nanoTime();
		vsr.usedTime = (searchEndTime - prepareStartTime)/1000 / 1000.0;
		if(log.logger.isDebugEnabled()){
			log.debug("total search %s", vsr.usedTime);
			log.debug("real search %s", realS);
		}
		return vsr;
	}

}
