package com.funshion.luc.defines;

import org.apache.lucene.document.Document;

import com.funshion.search.IndexableRecord;

public abstract class PendIndexer {
	
	public abstract void index(Document doc, IndexableRecord irec);
}
