package com.funshion.luc.defines;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.funshion.search.IndexableRecord;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Misc;

public final class IndexEntry{
	//a queue contains to-close index reader

	private static final LinkedBlockingQueue<CloseableReader> toClose = 
			new LinkedBlockingQueue<CloseableReader>();
	static{
		Thread closeOldIndexThread = new Thread(){
			public void run(){
				LogHelper log = new LogHelper("ieCloseQueue");
				log.info("close queue starts");
				while(true){
					CloseableReader reader = toClose.peek();
					if(reader != null){
						if(reader.reader == null){
							toClose.poll();
						}else{
							long used = System.currentTimeMillis() - reader.toClose;
							if(used >= 3000){
								toClose.poll();
								try {
									reader.close();
									log.debug("close with timeout %s, remain to close %s", used, toClose.size());
								} catch (Exception e) {
									log.error(e, "when close old indexReader %s", reader.reader);
									e.printStackTrace();
								}

							}
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		closeOldIndexThread.start();
	}
	class CloseableReader{
		final long toClose;
		final Object reader;
		CloseableReader(Object reader){
			this.toClose = System.currentTimeMillis();
			this.reader = reader;
		}
		public void close() throws IOException {

			if(reader != null){
				if(reader instanceof IndexReader){
					((IndexReader)reader).close();
				}else if(reader instanceof IndexEntry){
					((IndexEntry)reader).closeAll();
				}
			}
		}

	}
	IndexReader multiReader;
	AbstractSearcher searcher;
	private final ConfigurableIndexer indexer;
	private final Directory diskDir, ramDir, mmDir;
	public final File indexPathDir;
	private final LogHelper log;
//	private MMapDirectory ;
	public IndexEntry(File indexDir) throws Exception{
		log = new LogHelper("ie-" + indexDir.getName());
		this.indexPathDir = indexDir;
		diskDir = reinitSystem(indexDir);
		log.info("re-index  to index directory %s", 
				indexDir);
		ramDir = new RAMDirectory();
		mmDir = new MMapDirectory(indexPathDir);
		indexer = new ConfigurableIndexer(diskDir, ramDir, ITableDefine.instance);
	}
	void indexAll(final Iterator<IndexableRecord> itr) throws Exception{
		indexer.index(itr, null);
		log.info("index ALL OK");
		multiReader =  getMultiReader();
		searcher = ITableDefine.instance.newAbstractSearcher(multiReader);
		warmUp();
	}
	void update(final Iterator<IndexableRecord> itr) throws Exception{
		indexer.index(itr, ITableDefine.instance.getDetector());
		updateSearcher();
		log.info("index OK");
	}
	private void warmUp(){
		try {
			ITableDefine.instance.newAtddTestor().AtddTest(searcher);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void closeReader(final IndexReader indexReader){
		toClose.add(new CloseableReader(indexReader));
	}

	public void closeSequece(){
		toClose.add(new CloseableReader(this));
	}
	private void closeDir(Directory dir){
		try {
			dir.close();
		} catch (IOException e) {
			log.error(e, "when close dir %s", diskDir);
			e.printStackTrace();
		}
	}
	private void closeAll() {
		if(multiReader != null){
			try {
				multiReader.close();
			} catch (IOException e1) {
				log.error(e1, "when close indexReader %s", multiReader);
				e1.printStackTrace();
			}
		}
		try {
			indexer.close();
		} catch (IOException e) {
			log.error(e, "when close indexer %s", indexer);
			e.printStackTrace();
		}
		closeDir(ramDir);
		closeDir(this.mmDir);
		closeDir(diskDir);
		try{
			Misc.del(indexPathDir);
			log.warn("deleting %s for directory %s", indexPathDir, diskDir);
		}catch(Throwable e){
			log.error(e, "delete %s for directory %s", indexPathDir, diskDir);
		}
	}

	public void updateSearcher() throws Exception {
		IndexReader indexReaderOld = this.multiReader;
		multiReader = getMultiReader();
		searcher = ITableDefine.instance.newAbstractSearcher(multiReader);
		closeReader(indexReaderOld);
	}

	private Directory reinitSystem(File toIndexDir) throws IOException{
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

	/**
	 * after index, we will test the Directory.
	 * aims to:
	 * 1.be sure index not corrupt
	 * 2.let 
	 * @param dir2
	 * @return 
	 * @throws IOException 
	 */

	private IndexReader getMultiReader() throws IOException {
//		if(this.mmDir == null){
//			mmDir = new MMapDirectory(indexPathDir);
//		}

		return new MultiReader(DirectoryReader.open(mmDir), DirectoryReader.open(ramDir));
	}
	
	protected File getNewIndexDirectory() throws IOException{
		File f;
		while(true){
			f = new File(indexPathDir, new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS").format(System.currentTimeMillis()) + ".vlssIdx");
			if(f.exists()){
				continue;
			}else{
				break;
			}
		}
		return f;
	}

}