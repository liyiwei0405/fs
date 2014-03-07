//package com.funshion.videoService.search;
//
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.TextField;
//import org.apache.lucene.document.Field.Store;
//
//import com.funshion.luc.defines.LikeOperationTokenizerDefault;
//import com.funshion.luc.defines.ITableDefine;
//import com.funshion.luc.defines.PendIndexer;
//import com.funshion.search.IndexableRecord;
//import com.funshion.videoService.VideoletIndexableRecord;
//
//
//public class VideoletPendIndexer extends PendIndexer{
//	public static final String fieldName = ITableDefine.instance.getFieldDefine("titleLike".toUpperCase()).fieldName;
//
//	public VideoletPendIndexer(){
//		ITableDefine.instance.registerTokenlizer(fieldName, LikeOperationTokenizerDefault.class);
//	}
//	@Override
//	public void index(Document doc, IndexableRecord irec) {
//		String title = irec.valueOf(VideoletIndexableRecord.title.videoletFieldName);
//		Field field = new TextField(fieldName, title, Store.NO);
//		doc.add(field);
//	}
//
//}
