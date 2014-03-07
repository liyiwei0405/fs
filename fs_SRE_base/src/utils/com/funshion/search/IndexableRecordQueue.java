package com.funshion.search;

import java.util.Iterator;

import com.funshion.luc.defines.IndexEntry;

public interface IndexableRecordQueue {

	public void index(Iterator<IndexableRecord> iterator) throws Exception;

	public IndexEntry getIndexEntry();

	public boolean isUpdate();


}
