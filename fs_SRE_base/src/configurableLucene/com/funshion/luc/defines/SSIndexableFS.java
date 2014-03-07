//package com.funshion.luc.defines;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.MMapDirectory;
//
//import com.funshion.search.DatumFile;
//import com.funshion.search.FSIndexableRecordFactory;
//import com.funshion.search.FSIndexableRecordIterator;
//import com.funshion.search.IndexableFs;
//import com.funshion.search.utils.ConfigReader;
//import com.funshion.search.utils.LogHelper;
//import com.funshion.search.utils.Misc;
//
//public class SSIndexableFS extends IndexableFs{
//	public final class IndexEntry{
//		final File f;
//		final IndexReader indexReader;
//		final AbstractSearcher searcher;
//		final ConfigurableIndexer indexer;
//		final Directory dir;
//		int crtIdx = 0;
//		public IndexEntry(File f, ConfigurableIndexer indexer, Directory dir, int crtIndex) throws Exception{
//			this.f = f;
//			this.indexer = indexer;
//			this.dir = dir;
//			this.crtIdx = crtIndex;
//			indexReader = getMMapDirectryReader(f);
//			searcher = ITableDefine.instance.newAbstractSearcher(indexReader);
//			warmUp();
//		}
//
//		public IndexEntry(File f) throws Exception{
//			this.f = f;
//			dir = reinitSystem(f);
//			DatumFile df = crtDatumDir.get(0);
//			log.info("re-index file %s(exists:%s, len:%s Bytes) to index directory %s", 
//					df, 
//					df.file.exists(), 
//					df.file.length(), 
//					f);
//			indexer = new ConfigurableIndexer(dir, ITableDefine.instance);
//			indexer.index(new FSIndexableRecordIterator(df.file, factory), null);
//			log.info("index OK");
//			indexReader = getMMapDirectryReader(f);
//			searcher = ITableDefine.instance.newAbstractSearcher(indexReader);
//			warmUp();
//		}
//		private void warmUp(){
//			try {
//				ITableDefine.instance.newAtddTestor().AtddTest(searcher);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		public IndexEntry cloneNewEntry() throws Exception{
//			return new IndexEntry(this.f, this.indexer, this.dir, this.crtIdx);
//		}
//		public void closeReader(){
//			try {
//				this.indexReader.close();
//			} catch (IOException e) {
//				log.error(e, "when close ir %s", indexReader);
//				e.printStackTrace();
//			}
//			
//		}
//		public void close() {
//			try {
//				dir.close();
//			} catch (IOException e) {
//				log.error(e, "when close dir %s", dir);
//				e.printStackTrace();
//			}
//			try {
//				indexer.close();
//			} catch (IOException e) {
//				log.error(e, "when close indexer %s", indexer);
//				e.printStackTrace();
//			}
//
//			this.closeReader();
//			Misc.del(f);
//		}
//	}
//
//	private IndexEntry ie ;
//	private static SSIndexableFS instance;
//	final FSIndexableRecordFactory factory;
//	public static SSIndexableFS getInstance(){
//		return instance;
//	}
//	static void initInstance(FSIndexableRecordFactory factory){
//		if(instance == null){
//			try {
//				instance = new SSIndexableFS(ITableDefine.instance.getIndexConfig(), factory);
//			} catch (IOException e) {
//				e.printStackTrace();
//				LogHelper.log.error(e, "can not instance MediaIndexableFS");
//				throw new RuntimeException(e);
//			}
//		}
//	}
//	/**
//	 * after index, we will test the Directory.
//	 * aims to:
//	 * 1.be sure index not corrupt
//	 * 2.let 
//	 * @param dir2
//	 * @return 
//	 * @throws IOException 
//	 */
//
//	private IndexReader getMMapDirectryReader(File f) throws IOException {
//		Directory dir = new MMapDirectory(f);//(FSDirectory.open(f), IOContext.DEFAULT);//MMapDirectory.open(f);
//		IndexReader reader = DirectoryReader.open(dir);
//		return reader;
//	}
//
//
//	private SSIndexableFS(ConfigReader cr, FSIndexableRecordFactory factory) throws IOException {
//		super(cr);
//		this.factory = factory;
//	}
//
//
//	@Override
//	public void updateIndex() throws Exception {
//		ie.crtIdx ++;
//		if(ie.crtIdx < super.crtDatumDir.size()){
//			ie.indexer.index(new FSIndexableRecordIterator(super.crtDatumDir.get(ie.crtIdx).file, factory), ITableDefine.instance.getDetector());
//			IndexEntry old = this.ie;
//			ie = old.cloneNewEntry();
//			closeIndexEntry(old, false);
//		}else{
//			log.error("strange ERROR! crtDatumDir.Size() <= ie.crtIdx, %s <= %s", 
//					ie.crtIdx, super.crtDatumDir.size());
//		}
//	}
//
//	void closeIndexEntry(final IndexEntry crt, final boolean closeAll){
//		Thread closeOldIndexThread = new Thread(){
//			public void run(){
//				if(crt == null){
//					return;
//				}
//				try {
//					Thread.sleep(10 * 1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				try {
//					if(closeAll){
//						crt.close();
//					}else{
//						crt.closeReader();
//					}
//				} catch (Exception e) {
//					log.error(e, "when close old indexReader %s", crt);
//					e.printStackTrace();
//				}
//			}
//		};
//		closeOldIndexThread.start();
//	}
//	@Override
//	public void rotateIndex() throws Exception {
//		File f = getNewIndexDirectory();
//
//		IndexEntry ieNew = new IndexEntry(f);
//
//		final IndexEntry crt = this.ie;
//		closeIndexEntry(crt, true);
//		log.info("switch index dir! from %s to %s", ie == null ? "null": ie.dir, ieNew.dir);
//		this.ie = ieNew;
//		this.setCanServiceNow(true);
//	}
//
//	public Directory reinitSystem(File toIndexDir) throws IOException{
//		if(!toIndexDir.exists()){
//			toIndexDir.mkdirs();
//		}else if(!toIndexDir.isDirectory()){
//			throw new IOException("invalid dir, should be dir, but it's a regular file: " + toIndexDir.getAbsolutePath());
//		}
//		if(!toIndexDir.canWrite() || !toIndexDir.canRead() || !toIndexDir.canExecute()){
//			throw new IOException("dir privileges should be 'drwx'");
//		}
//		Directory lDir = FSDirectory.open(toIndexDir);
//		return lDir;
//	}
//	public AbstractSearcher getSearcher() {
//		if(this.ie == null || ie.searcher == null){
//			return null;
//		}
//		return ie.searcher;
//	}
//}
