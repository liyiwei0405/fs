package com.funshion.search.media.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.FSMediaIndexSearcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SimlarityProvider;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

import com.funshion.retrieve.media.QueryParseTool;
import com.funshion.retrieve.media.StoreType;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.thrift.LimitRetrieve;
import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.MediaRetrieveResultRecord;
import com.funshion.retrieve.media.thrift.MedieRetrieveResultItem;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.retrieve.media.thrift.SortRetrieve;
import com.funshion.search.utils.LogHelper;

public class MediaSearcher {

	protected final IndexSearcher sIns;
	public final LogHelper log = new LogHelper("mediaSearcher");
	public MediaSearcher(IndexReader ir) throws IOException{
		sIns = new FSMediaIndexSearcher(ir);
		sIns.setSimilarity(new SimlarityProvider());
	}

	public TopDocs search(RetrieveStruct qs, int topN) throws Exception{
		long time0 = System.nanoTime();
		PortalCompoundCondition conds = QueryParseTool.parseList(qs.conditions);
		Query bqx = QueryParseTool.buildQuery(conds);
		long time1 = System.nanoTime();
		Sort sort;
		if(qs.sortFields == null || qs.sortFields.size() == 0){
			sort = new Sort(SortField.FIELD_SCORE, 
					new SortField(FieldDefine.FIELD_NAME_RELEASE_DATE, Type.INT, true), 
					new SortField(FieldDefine.FIELD_NAME_PLAY_NUM, Type.INT, true));
		}else{
			SortField[]ss = new SortField[qs.sortFields.size()];
			for(int x = 0; x < ss.length; x ++){
				SortRetrieve sr = qs.sortFields.get(x);

				ss[x] = new SortField(
						sr.field,
						StoreType.getType(sr.field.charAt(0)) == StoreType.INT ? Type.INT : Type.STRING,
								!sr.asc
						);
			}
			sort = new Sort(ss);
		}
		if(log.logger.isDebugEnabled()){
			log.debug("query %s with sort %s, topN %s", bqx, sort, topN);
		}
		long time2 = System.nanoTime();

		TopDocs td = search(bqx, sort, topN);
		long time3 = System.nanoTime();

		if(log.logger.isDebugEnabled()){
			log.debug("search make-query use %s", (time1 - time0) / 10000/100.0);
			log.debug("search make-sort use %s", (time2 - time1) / 10000/100.0);
			log.debug("search search use %s", (time3 - time2) / 10000/100.0);
		}
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
					switch(c){
					case FieldDefine.FIELD_NAME_CHAR_UNIC_ID:
					case FieldDefine.FIELD_NAME_CHAR_TATICS:
//					case FieldDefine.FIELD_NAME_CHAR_ISPLAY:
					case FieldDefine.FIELD_NAME_CHAR_ORDERING:
					case FieldDefine.FIELD_NAME_CHAR_COVER_PIC_ID:
					case FieldDefine.FIELD_NAME_CHAR_TA_0:
					case FieldDefine.FIELD_NAME_CHAR_TA_1:
					case FieldDefine.FIELD_NAME_CHAR_TA_2:
					case FieldDefine.FIELD_NAME_CHAR_TA_3:
					case FieldDefine.FIELD_NAME_CHAR_TA_4:
					case FieldDefine.FIELD_NAME_CHAR_TA_5:
					case FieldDefine.FIELD_NAME_CHAR_TA_6:
					case FieldDefine.FIELD_NAME_CHAR_TA_7:
					case FieldDefine.FIELD_NAME_CHAR_TA_8:
					case FieldDefine.FIELD_NAME_CHAR_TA_9:
					case FieldDefine.FIELD_NAME_CHAR_COPYRIGHT:
					case FieldDefine.FIELD_NAME_CHAR_PLAY_NUM:
					case FieldDefine.FIELD_NAME_CHAR_PLAY_AFTER_NUM:
					case FieldDefine.FIELD_NAME_CHAR_KARMA:
					case FieldDefine.FIELD_NAME_CHAR_VOTENUM:
					case FieldDefine.FIELD_NAME_CHAR_WANT_SEE_NUM:
					case FieldDefine.FIELD_NAME_CHAR_PROGRAM_TYPE:
					case FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET:
						if(f.numericValue() == null){
							log.error("ERROR numericValue field:" + f.name());
							continue;
						}
						MedieRetrieveResultItem itmId = new MedieRetrieveResultItem(name,
								f.numericValue().toString());
						vr.addToItems(itmId);
						break;
					default:
						if(f.stringValue() == null){
							log.error("ERROR numericValue field:" + f.name());
							continue;
						}
						itmId = new MedieRetrieveResultItem(name,
								f.stringValue());
						vr.addToItems(itmId);
						break;	
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
