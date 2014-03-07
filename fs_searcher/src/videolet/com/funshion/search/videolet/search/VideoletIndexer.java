package com.funshion.search.videolet.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import com.funshion.search.DatumFile;
import com.funshion.search.IndexableRecord;
import com.funshion.search.IndexableRecord.IRIterator;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.videolet.chgWatcher.VideoletRecord;

public class VideoletIndexer {
	public static final LogHelper log = new LogHelper("indexer");
	public static final String INDEX_UNI_ID = "a".intern();
	public static final String INDEX_VIDEO_OR_UGC = "b".intern();
	public static final String INDEX_VIDEO_ID = "c".intern();
	public static final String INDEX_TITLE = "d".intern();
	public static final String INDEX_TAGS = "e".intern();
	public static final String INDEX_TITLE_STRICT = "f".intern();
	public static final String INDEX_TAGS_STRICT = "g".intern();
	public static final String INDEX_PLAY_NUM = "h".intern();
	public static final String INDEX_CREATE_DATE = "i".intern();
	public static final String INDEX_MODIFY_DATE = "j".intern();

	final Directory dir;
	final IndexWriter iw;
	final BytesRef brVideoId = new BytesRef();
	final BytesRef brVideoOrUgc = new BytesRef();
	public VideoletIndexer(Directory dir) throws IOException{
		this.dir = dir;
		IndexWriterConfig ic = new IndexWriterConfig(Version.LUCENE_42, new FSVideoletChineseAnalyzer());
		iw = new IndexWriter(dir, ic);
	}
	
	protected void indexRecord(IndexableRecord rec, boolean shouldUpdate) throws IOException{
		String isDel = rec.valueOf(VideoletRecord.FIELD_IS_DEL);
		boolean toDel = "1".equals(isDel);

		String videoOrUgc = rec.valueOf(VideoletRecord.FIELD_VIDEO_OR_UGC);
		String videoId = rec.valueOf(VideoletRecord.FIELD_VIDEO_ID);
		int  videoOrUgcInt = Integer.parseInt(videoOrUgc);
		int videoIdInt = Integer.parseInt(videoId);
		String uniId = videoOrUgcInt + "@" + videoIdInt;
		Term delTerm = new Term(INDEX_UNI_ID, uniId);
		if(toDel){
			iw.deleteDocuments(delTerm);
			log.warn("deleted doc by term %s", delTerm);
			return;
		}

		String playNum = rec.valueOf(VideoletRecord.FIELD_PLAY_NUM);
		String createDate = rec.valueOf(VideoletRecord.FIELD_CREATE_DATE);
		String modifyDate = rec.valueOf(VideoletRecord.FIELD_MODIFY_DATE);

		Field fUniId = new StringField(INDEX_UNI_ID, uniId, Store.YES);
		Field fVideoOrUgc = new IntField(INDEX_VIDEO_OR_UGC, videoOrUgcInt, Store.YES);
		Field fVideoID = new IntField(INDEX_VIDEO_ID, videoIdInt, Store.YES);
		Field fPlayNum = new IntField(INDEX_PLAY_NUM, Integer.parseInt(playNum), Store.YES);
		Field fModifyDate = new IntField(INDEX_MODIFY_DATE, Integer.parseInt(modifyDate), Store.YES);
		Field fCreateDate = new IntField(INDEX_CREATE_DATE, Integer.parseInt(createDate), Store.YES);

		Document doc = new Document();
		doc.add(fUniId);
		doc.add(fVideoOrUgc);
		doc.add(fVideoID);
		doc.add(fPlayNum);
		doc.add(fModifyDate);
		doc.add(fCreateDate);

		StringBuilder sb = new StringBuilder();
		String tags[] = rec.valueOf(VideoletRecord.FIELD_TAGS);
		for(int x = 1; x < tags.length; x ++){
			tags[x] = tags[x].trim();
			if(tags[x].length() == 0){
				continue;
			}
			if(sb.length() > 0){
				sb.append('\t');
			}
			sb.append(tags[x]);
		}
		String tagsAll = sb.toString();
		Field fTitle = new TextField(INDEX_TITLE, rec.valueOf(VideoletRecord.FIELD_TITLE)[1], Store.NO);
		Field fTags = new TextField(INDEX_TAGS, tagsAll, Store.NO);

		doc.add(fTitle);
		doc.add(fTags);

		Field fTitleStrict = new TextField(INDEX_TITLE_STRICT, rec.valueOf(VideoletRecord.FIELD_TITLE)[1], Store.NO);
		Field fTagsStrict = new TextField(INDEX_TAGS_STRICT, tagsAll, Store.NO);

		doc.add(fTitleStrict);
		doc.add(fTagsStrict);
		if(shouldUpdate){
			iw.updateDocument(delTerm, doc);
			log.warn("updated doc by term %s", delTerm);
		}else{
			iw.addDocument(doc);
		}
	}
	protected void index(DatumFile file, boolean isUpdate) throws IOException{
		IRIterator itr = new IRIterator(file.file);
		try{
			while(itr.hasNext()){
				IndexableRecord rec = itr.next();
				try{
					indexRecord(rec, isUpdate);
				}catch(Exception e){
					log.error(e, "when index record %s", rec);
				}
			}
			iw.commit();
		}finally{
			itr.close();
		}
	}

//	public static void main(String[]args) throws IOException{
//		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
//		File f = new File("testIdx");
//		Directory dir = new MMapDirectory(f, null,  1 << 30);
//
//		DatumDir datums = new DatumDir(new File("./chg/totalXmlFile.chg"));
//
//		long st = System.currentTimeMillis();
//		VideoletIndexer idx = new VideoletIndexer(dir, datums);
//		idx.indexMain();
//
//		long ed = System.currentTimeMillis();
//		System.out.println("index use " + (ed - st));
//	}
}
