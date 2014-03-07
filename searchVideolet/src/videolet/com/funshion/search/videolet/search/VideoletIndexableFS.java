package com.funshion.search.videolet.search;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import com.funshion.search.DatumFile;
import com.funshion.search.IndexableFs;

public class VideoletIndexableFS extends IndexableFs{
	/**
	 * after index, we will test the Directory.
	 * aims to:
	 * 1.be sure index not corrupt
	 * 2.let 
	 * @param dir2
	 * @return 
	 * @throws IOException 
	 */
	private IndexReader testAndGetMMapDirectryReader(File f) throws IOException {
		Directory dir = MMapDirectory.open(f);
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(reader);
		for(Query q : testQueries){
			long st = System.currentTimeMillis();
			TopDocs docs = is.search(q, 10);
			long used = System.currentTimeMillis() - st;
			log.debug("use %sms get %s results, query : %s",  used, docs.totalHits, q);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return reader;
	}

	static Query[] testQueries = new Query[]{
		new TermQuery(new Term(VideoletIndexer.INDEX_TITLE, "的")),
		new TermQuery(new Term(VideoletIndexer.INDEX_TITLE, "美女")),
		new TermQuery(new Term(VideoletIndexer.INDEX_TITLE, "A")),
		new TermQuery(new Term(VideoletIndexer.INDEX_TITLE, "vs")),

		new TermQuery(new Term(VideoletIndexer.INDEX_TAGS, "的")),
		new TermQuery(new Term(VideoletIndexer.INDEX_TAGS, "美女")),
		new TermQuery(new Term(VideoletIndexer.INDEX_TAGS, "A")),
		new TermQuery(new Term(VideoletIndexer.INDEX_TAGS, "vs")),

	};
	public final class IndexEntry{
		final File f;
		final IndexReader ir;
		final VideoletSearcher searcher;
		final VideoletIndexer indexer;
		int crtIdx = 0;
		public IndexEntry(File f, IndexReader ir, VideoletSearcher searcher, VideoletIndexer indexer){
			this.f = f;
			this.ir = ir;
			this.searcher = searcher;
			this.indexer = indexer;
		}
		public void close() {
			// FIXME
		}
	};
	public static final VideoletIndexableFS instance = new VideoletIndexableFS();
	private VideoletIndexableFS() {
		super();
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
		Directory dir = reinitSystem(f);
		DatumFile df = this.crtDatumDir.get(0);
		VideoletIndexer indexer = new VideoletIndexer(dir);
		indexer.index(df, false);
		IndexReader ir = testAndGetMMapDirectryReader(f);
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
		IndexEntry ieNew = new IndexEntry(f, ir, new VideoletSearcher(ir), indexer);
		this.ie = ieNew;
	}


	protected File getNewIndexDirectory() throws IOException{
		File f;
		while(true){
			f = new File(new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS").format(System.currentTimeMillis()) + ".vlssIdx");
			if(f.exists()){
				continue;
			}else{
				break;
			}
		}
		return f;
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

	public VideoletSearcher getVideoletSearcher() {
		if(this.ie == null || ie.searcher == null){
			return null;
		}
		return ie.searcher;
	}

}
