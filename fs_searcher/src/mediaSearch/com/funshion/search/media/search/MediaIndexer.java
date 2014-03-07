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
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import com.funshion.search.DatumFile;
import com.funshion.search.IndexableRecord;
import com.funshion.search.IndexableRecord.IRIterator;
import com.funshion.search.media.search.mediaTitleRewriter.CnNumRewrite;
import com.funshion.search.media.search.mediaTitleRewriter.F2JConvert;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixSpecialWordTrimer;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixNumFormatResult;
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
		ic.setMaxBufferedDocs(100000);
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

	protected void enumField(Document doc, String filedName, IndexableRecord rec){
		String value = rec.valueOf(filedName);
//		if(value.length() == 0){
//			value = FieldDefine.INNER_EMP_VALUE;
//		}
		Field field = new EnumTextField(filedName, value, Store.YES);
		doc.add(field);
	}
	protected void fttField(Document doc, String filedName, IndexableRecord rec){
		String value = rec.valueOf(filedName);
		Field field = new TextField(filedName, value, Store.YES);
		doc.add(field);
	}
	public static final String attX = "0 1 2 3 4 5 6 7 8 9";
	protected void indexRecord(IndexableRecord rec, boolean shouldUpdate) throws IOException{
		Document doc = new Document();
		this.intField(doc, FieldDefine.FIELD_NAME_UNIC_ID, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_CN, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_EN, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_OT, rec);
		this.fttField(doc, FieldDefine.FIELD_NAME_NAME_SN, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_TATICS, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_ISPLAY, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_ORDERING, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_COVER_PIC_ID, rec);
		this.strField(doc, FieldDefine.FIELD_NAME_ISSUE, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_COPYRIGHT, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_PLAY_NUM, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_PLAY_AFTER_NUM, rec);
		this.floatField(doc, FieldDefine.FIELD_NAME_KARMA, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_VOTENUM, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_WANT_SEE_NUM, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_PROGRAM_TYPE, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_DISPLAY_TYPE, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_COUNTRY, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_TAG_4_EDITOR, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_RELEASE_INFO, rec);
		this.strField(doc, FieldDefine.FIELD_NAME_IMAGE_FILE_PATH, rec);
		this.strField(doc, FieldDefine.FIELD_NAME_WEB_PLAY, rec);
		this.intField(doc, FieldDefine.FIELD_NAME_RELEASE_DATE, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_IS_HD, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_RELATED_PREIDS, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_RELATED_VIDEOLET, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_MEDIA_CLASSID, rec);
		this.enumField(doc, FieldDefine.FIELD_NAME_AREA_TACTIC, rec);

		String value = rec.valueOf(FieldDefine.FIELD_NAME_CHAR_RELATED_VIDEOLET);
		boolean hasVideolet = value.length() > 0;
		Field fieldHasVideolet = new IntField(FieldDefine.FIELD_NAME_HAS_VIDEOLET, hasVideolet ? 1 : 0, Store.YES);
		doc.add(fieldHasVideolet);
	
		boolean att[] = new boolean[]{
				Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_0)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_1)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_2)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_3)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_4)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_5)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_6)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_7)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_8)) == 0,
						Integer.parseInt(rec.valueOf(FieldDefine.FIELD_NAME_TA_9)) == 0,
		};
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < att.length; x ++){
			if(att[x]){
				if(sb.length() > 0){
					sb.append('\t');
				}
				sb.append(x);
			}
		}
		String attValues = sb.toString();

		Field fieldTa_0_9 = new TextField(FieldDefine.FIELD_NAME_TA_0_9, attValues, Store.YES);
		doc.add(fieldTa_0_9);

		String ot = rec.valueOf(FieldDefine.FIELD_NAME_NAME_OT);
		String cn = rec.valueOf(FieldDefine.FIELD_NAME_NAME_CN);
		String orgCn = cn;
		final String []trimed = MediaTitleSuffixSpecialWordTrimer.instance.trim(cn);
		MediaTitleSuffixNumFormatResult mr = MediaTitleSuffixNumFormatResult.rewriteTitle(trimed == null ? orgCn : trimed[0]);

		if(trimed != null){
			String alia = MediaTitleSuffixSpecialWordTrimer.instance.getAlia(trimed[1]);
			ot = ot + "\t" + trimed[0] + " " + alia;
			//			for(String v : alias){
			//				ot = ot + "\t" + trimed[0] + " " + v;
			//			}
			//			ot = ot + "\t" + trimed[0];
		}
		if(mr != null){
			cn = cn + "\t" + CnNumRewrite.cnNumRewrite(mr.norm());
			ot = ot + "\t" + CnNumRewrite.cnNumRewrite(mr.getNewNameCn()); 
		}else{
			String cnNew = CnNumRewrite.cnNumTryRewrite(cn);
			if(cnNew != null){
				cn = cn + "\t" + cnNew;
			}
			String otNew = CnNumRewrite.cnNumTryRewrite(ot);
			if(otNew != null){
				ot = ot + "\t" + otNew;
			}
		}
		StringBuilder sbuf = new StringBuilder();
		appendTitle(cn, sbuf);
		appendTitle(rec.valueOf(FieldDefine.FIELD_NAME_NAME_EN), sbuf);
		appendTitle(ot, sbuf);
		appendTitle(rec.valueOf(FieldDefine.FIELD_NAME_NAME_SN), sbuf);
		String strBuf = sbuf.toString();
		Field field = new TextField(FieldDefine.FIELD_NAME_NAMES, strBuf, Store.NO);
		doc.add(field);

		iw.addDocument(doc);
	}
	private void appendTitle(String tokens, StringBuilder sb){
		if(sb.length() > 0){
			sb.append('\n');
		}
		tokens = tokens.trim();
		if(tokens.length() == 0){
			tokens = " ";
		}
		sb.append(tokens);
		String []xx = tokens.split("\t");
		for(String v : xx){
			v = v.trim();
			if(v.length() == 0){
				continue;
			}
			String cvtFj = F2JConvert.instance.conver(v);
			if(!v.equals(cvtFj)){
				sb.append('\t');
				sb.append(cvtFj);
			}
		}

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
			iw.forceMerge(1);
			iw.commit();
		}finally{
			itr.close();
		}
		log.info("index has successfully done！from file %s,  to index %s", file.file, this.iw.getDirectory());
	}

	public void close() throws IOException{
		iw.close();
	}
}
