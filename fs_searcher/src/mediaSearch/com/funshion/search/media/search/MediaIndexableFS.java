package com.funshion.search.media.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import com.funshion.retrieve.media.QueryParseTool;
import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.retrieve.media.thrift.LimitRetrieve;
import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.search.DatumFile;
import com.funshion.search.IndexableFs;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Misc;

public class MediaIndexableFS extends IndexableFs{
	/**
	 * after index, we will test the Directory.
	 * aims to:
	 * 1.be sure index not corrupt
	 * 2.let 
	 * @param dir2
	 * @return 
	 * @throws IOException 
	 */
	private IndexReader getMMapDirectryReader(File f) throws IOException {
		Directory dir = new MMapDirectory(f);//(FSDirectory.open(f), IOContext.DEFAULT);//MMapDirectory.open(f);
		IndexReader reader = DirectoryReader.open(dir);
		return reader;
	}

	public final class IndexEntry{
		final File f;
		final IndexReader ir;
		final MediaSearcher searcher;
		final MediaIndexer indexer;
		final Directory dir;
		int crtIdx = 0;
		public IndexEntry(File f) throws IOException{
			this.f = f;
			dir = reinitSystem(f);
			DatumFile df = crtDatumDir.get(0);
			log.info("re-index file %s(exists:%s, len:%s Bytes) to index directory %s", 
					df, 
					df.file.exists(), 
					df.file.length(), 
					f);
			indexer = new MediaIndexer(dir);
			indexer.index(df, false);
			log.info("index OK");
			ir = getMMapDirectryReader(f);
			searcher = new MediaSearcher(ir);

		}
		public void close() {
			try {
				indexer.close();
			} catch (IOException e) {
				log.error(e, "when close indexer %s", indexer);
				e.printStackTrace();
			}

			try {
				this.ir.close();
			} catch (IOException e) {
				log.error(e, "when close ir %s", ir);
				e.printStackTrace();
			}
			try {
				dir.close();
			} catch (IOException e) {
				log.error(e, "when close dir %s", dir);
				e.printStackTrace();
			}
			Misc.del(f);
		}
	};
	private static MediaIndexableFS instance;
	static{
		if(instance == null){
			try {
				instance = new MediaIndexableFS(new ConfigReader(MediaSSDaemon.daemonConfig, "index"));
			} catch (IOException e) {
				e.printStackTrace();
				LogHelper.log.error(e, "can not instance MediaIndexableFS");
				throw new RuntimeException(e);
			}
		}

	}
	public static MediaIndexableFS getInstance(){
		return instance;
	}
	private MediaIndexableFS(ConfigReader cr) throws IOException {
		super(cr);
	}

	IndexEntry ie ;
	@Override
	public void updateIndex() throws IOException {
		if(ie.crtIdx < super.crtDatumDir.size()){
			ie.indexer.index(super.crtDatumDir.get(ie.crtIdx), true);
			ie.crtIdx ++;
		}
	}

	@Override
	public void rotateIndex() throws IOException {
		File f = getNewIndexDirectory();
		/**Directory dir = reinitSystem(f);
		DatumFile df = this.crtDatumDir.get(0);
		MediaIndexer indexer = new MediaIndexer(dir);
		indexer.index(df, false);
		IndexReader ir = getMMapDirectryReader(f);**/
		IndexEntry ieNew = new IndexEntry(f);
		String testWords [] = new String[]{
				"的","大话西游之月光宝盒","武林外传","笑傲江湖", "天龙八部", ""
		};
		long now = System.nanoTime();
		TopFieldDocs docs = ieNew.searcher.search(new MatchAllDocsQuery(), 
				new Sort(new SortField(FieldDefine.FIELD_NAME_UNIC_ID,
						Type.INT, true)), 1000000);
		
		
		log.info("MatchAllDocsQuery use %sms got %s records", (System.nanoTime() - now)/ 1000/ 100 /10.0, docs.totalHits);
		for(String x : testWords){
			try {
				RetrieveStruct qs = new RetrieveStruct();
				LimitRetrieve lb = new LimitRetrieve(0, 1000);
				PortalCompoundCondition conds = new PortalCompoundCondition();
				conds.addCondition(ConjunctType.OR, new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
								OperType.SEARCH_TITLE_FULL,
								x));
				conds.addCondition(ConjunctType.OR, new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
								OperType.SEARCH_TITLE,
								x));
//				List<Token>lst = QueryParseTool.toList(conds);
				qs.conditions = QueryParseTool.toList(conds);
//				System.out.println(qs.conditions);
				qs.limits = lb;
				MediaRetrieveResult mr = ieNew.searcher.query(qs);
				log.info("query total result %s, use %sms for %s", mr.total, mr.usedTime, qs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		final IndexEntry crt = this.ie;
		Thread closeOldIndexThread = new Thread(){
			public void run(){
				if(crt == null){
					return;
				}
				try {
					Thread.sleep(30 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					crt.close();
				} catch (Exception e) {
					log.error(e, "when close old indexReader %s", crt);
					e.printStackTrace();
				}
			}
		};
		closeOldIndexThread.start();
		log.info("switch index dir! from %s to %s", ie == null ? "null": ie.dir, ieNew.dir);
		this.ie = ieNew;
		this.setCanServiceNow(true);
	}

	public Directory reinitSystem(File toIndexDir) throws IOException{
		if(!toIndexDir.exists()){
			toIndexDir.mkdirs();
		}else if(!toIndexDir.isDirectory()){
			throw new IOException("invalid dir, should be dir, but it's a regular file: " + toIndexDir.getAbsolutePath());
		}
		if(!toIndexDir.canWrite() || !toIndexDir.canRead() || !toIndexDir.canExecute()){
			throw new IOException("dir privileges should be 'drwx'");
		}
		Directory lDir = FSDirectory.open(toIndexDir);
		return lDir;
	}
	public MediaSearcher getSearcher() {
		if(this.ie == null || ie.searcher == null){
			return null;
		}
		return ie.searcher;
	}
}
