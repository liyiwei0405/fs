package com.funshion.search.media.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;

import com.funshion.search.DatumFile;
import com.funshion.search.IndexableRecord;
import com.funshion.search.IndexableRecord.IRIterator;
import com.funshion.search.utils.LogHelper;

public class MediaIndexer {
	public static final LogHelper log = new LogHelper("indexer");

	final Directory dir;
	final IndexWriter iw;
	final BytesRef brVideoId = new BytesRef();
	final BytesRef brVideoOrUgc = new BytesRef();
	public MediaIndexer(Directory dir) throws IOException{
		this.dir = dir;
		IndexWriterConfig ic = new IndexWriterConfig(Version.LUCENE_42, new FSMediaChineseAnalyzer());
		iw = new IndexWriter(dir, ic);
	}
	protected void floatField(Document doc, String filedName, IndexableRecord rec){
		String value = rec.valueOf(filedName);
		Field field = new FloatField(filedName, Float.parseFloat(value), Store.YES);
		doc.add(field);
	}
	protected void intField(Document doc, String filedName, IndexableRecord rec){
		String value = rec.valueOf(filedName);
		Field field = new IntField(filedName, Integer.parseInt(value), Store.YES);
		doc.add(field);
	}

	protected void strField(Document doc, String filedName, IndexableRecord rec){
		String value = rec.valueOf(filedName);
		Field field = new StringField(filedName, value, Store.YES);
		doc.add(field);
	}

	protected void fttField(Document doc, String filedName, IndexableRecord rec){
		String value = rec.valueOf(filedName);
		Field field = new TextField(filedName, value, Store.YES);
		doc.add(field);
	}

	protected void indexRecord(IndexableRecord rec, boolean shouldUpdate) throws IOException{
		if(false){//FIXME
			String isDel = rec.valueOf(FieldDefine.FIELD_NAME_DELETED);
			boolean toDel = "1".equals(isDel);

			if(toDel){
				if(!shouldUpdate){
					return;
				}
				String videoIdStr = rec.valueOf(FieldDefine.FIELD_NAME_UNIC_ID);
				int videoId = Integer.parseInt(videoIdStr);
				BytesRef brDelTerm = new BytesRef();
				NumericUtils.intToPrefixCoded(videoId, 0, brDelTerm);
				Term delTerm = new Term(FieldDefine.FIELD_NAME_UNIC_ID, brDelTerm);
				iw.deleteDocuments(delTerm);
				log.warn("deleted doc by term %s", delTerm);
				return;
			}
		}
		Document doc = new Document();
		//FIXME this.intField(doc, FieldDefine.FIELD_NAME_UNIC_ID, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_CN, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_EN, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_OT, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_SN, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TATICS, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_ISPLAY, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_ORDERING, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_COVER_PIC_ID, rec);
		this.strField(doc, FieldDefine.FIELD_NAME_ISSUE, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_0, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_1, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_2, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_3, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_4, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_5, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_6, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_7, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_8, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TA_9, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_COPYRIGHT, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_PLAY_NUM, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_PLAY_AFTER_NUM, rec);
		this.floatField(doc, FieldDefine.FIELD_NAME_KARMA, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_VOTENUM, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_WANT_SEE_NUM, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_PROGRAM_TYPE, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_DISPLAY_TYPE, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_COUNTRY, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_TAG_4_EDITOR, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_RELEASE_INFO, rec);
		this.strField(doc, FieldDefine.FIELD_NAME_IMAGE_FILE_PATH, rec);
		this.strField(doc, FieldDefine.FIELD_NAME_WEB_PLAY, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_RELEASE_DATE, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_IS_HD, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_RELATED_PREIDS, rec);
		//FIXME this.intField(doc, FieldDefine.FIELD_NAME_RELATED_RESERVED1, rec);
		//FIXME this.intField(doc, FieldDefine.FIELD_NAME_RELATED_RESERVED2, rec);

		StringBuilder sb = new StringBuilder();
		makeField(rec.valueOf(FieldDefine.FIELD_NAME_NAME_CN), sb);
		makeField(rec.valueOf(FieldDefine.FIELD_NAME_NAME_EN), sb);
		makeField(rec.valueOf(FieldDefine.FIELD_NAME_NAME_OT), sb);
		makeField(rec.valueOf(FieldDefine.FIELD_NAME_NAME_SN), sb);
		Field field = new TextField(FieldDefine.FIELD_NAME_NAMES, sb.toString(), Store.NO);
		doc.add(field);
		iw.addDocument(doc);
	}
	private void makeField(String tokens, StringBuilder sb){
		if(sb.length() > 0){
			sb.append('\n');
		}
		tokens = tokens.trim();
		if(tokens.length() == 0){
			tokens = " ";
		}
		sb.append(tokens);
		
	}
	public void index(DatumFile file, boolean isUpdate) throws IOException{
		if(isUpdate){
			throw new IOException("update for mediaSearch not supported currently");
		}
		IRIterator itr = new IRIterator(file.file);
		try{
			while(itr.hasNext()){
				IndexableRecord rec = itr.next();
				try{
					indexRecord(rec, isUpdate);
				}catch(Exception e){
					e.printStackTrace();
					log.error(e, "when index record %s", rec);
				}
			}
			iw.commit();
		}finally{
			itr.close();
		}
	}

}
