package com.funshion.luc.defines;

import java.util.Iterator;

import com.funshion.search.IndexChannel;
import com.funshion.search.IndexableRecord;
import com.funshion.search.IndexableRecordQueue;

public class IndexableRecordQueueImp implements IndexableRecordQueue{
	private final boolean newQueue;
	final IndexChannel proxy;
	private boolean indexed = false;
	private final IndexEntry ie;
	IndexableRecordQueueImp(boolean newQueue, IndexChannel proxy, IndexEntry ie){
		this.newQueue = newQueue;
		this.proxy = proxy;
		this.ie = ie;
	}
	@Override
	public synchronized void index(Iterator<IndexableRecord> iterator) throws Exception {
		try{
			if(!newQueue || indexed){
				ie.update(iterator);
			}else{
				ie.indexAll(iterator);
			}
		}finally{
			this.indexed = true;
		}

	}
	@Override
	public IndexEntry getIndexEntry() {
		return this.ie;
	}
	@Override
	public boolean isUpdate() {
		return !newQueue;
	}

}
