package com.funshion.luc.defines;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.funshion.search.IndexChannel;
import com.funshion.search.IndexableRecordQueue;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.FileBasedLock;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Misc;

public class IndexChannelImp implements IndexChannel{
	LogHelper log = new LogHelper("indexProxy");
	private IndexEntry ie ;

	FileBasedLock indexDirectoryLock;
	public IndexChannelImp(ConfigReader cr) throws IOException {
		this.indexPathDir = 
				new File(cr.getValue("indexPath","/indexes"));
		if(!indexPathDir.exists()){
			if(!indexPathDir.mkdirs()){
				throw new IOException("can not mkdir:" + indexPathDir.getAbsolutePath());
			}
		}else{
			if(!indexPathDir.isDirectory()){
				throw new IOException("must be dir:" + indexPathDir.getAbsolutePath());
			}
		}

		indexDirectoryLock = FileBasedLock.tryLockDirAndGetTheLockObject(indexPathDir) ;
		delFilesInDir(indexPathDir);
	}

	private boolean canServiceNow = false;
	private final File indexPathDir;
	private void delFilesInDir(File file){
		File[]fs = file.listFiles();
		if(fs != null){
			for(File f : fs){
				Misc.del(f);
			}
		}
	}

	public void rotateIndex(IndexableRecordQueue queue) {
		if(queue.isUpdate()){
			throw new RuntimeException("can not rotate index to update queue");
		}
		IndexEntry ieNew = queue.getIndexEntry();
		if(ieNew == this.ie){
			return;
		}
		final IndexEntry crt = this.ie;
		if(crt != null){
			crt.closeSequece();
		}
		log.warn("switch index dir! from %s to %s", ie == null ? "null": ie.indexPathDir, ieNew.indexPathDir);
		this.ie = ieNew;
		this.canServiceNow = true;
	}
	
//	public void rotateIndex(Iterator<IndexableRecord> itr) throws Exception {
//		File f = getNewIndexDirectory();
//		IndexEntry ieNew = new IndexEntry(f);
//		ieNew.indexAll(itr);
//		final IndexEntry crt = this.ie;
//		if(crt != null){
//			crt.closeSequece();
//		}
//		log.info("switch index dir! from %s to %s", ie == null ? "null": ie.diskDir, ieNew.diskDir);
//		this.ie = ieNew;
//		this.canServiceNow = true;
//	}
	
	public AbstractSearcher getSearcher() {
		if(this.ie == null || ie.searcher == null){
			return null;
		}
		return ie.searcher;
	}
	public boolean isCanServiceNow() {
		return canServiceNow;
	}
	
	public IndexableRecordQueue openChannel(boolean isNew) throws Exception {
		IndexableRecordQueue queue;
		if(isNew){
			File f = getNewIndexDirectory();
			IndexEntry ieNew = new IndexEntry(f);
			queue = new IndexableRecordQueueImp(isNew, this, ieNew);
		}else{
			queue = new IndexableRecordQueueImp(isNew, this, this.ie);
		}
		return queue;
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
