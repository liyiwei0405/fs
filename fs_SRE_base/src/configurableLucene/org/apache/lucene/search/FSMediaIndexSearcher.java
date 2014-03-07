package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;

public class FSMediaIndexSearcher extends IndexSearcher{

	public FSMediaIndexSearcher(IndexReader r) {
		super(r);
	}

	protected TopFieldDocs search(List<AtomicReaderContext> leaves, Weight weight, FieldDoc after, int nDocs,
			Sort sort, boolean fillFields, boolean doDocScores, boolean doMaxScore) throws IOException {
		int limit = reader.maxDoc();
		if (limit == 0) {
			limit = 1;
		}
		nDocs = Math.min(nDocs, limit);
		//FIXME should set 5'th and 6'th parameter to false 
		FSMediaCollector collector = new FSMediaCollector(
				sort, nDocs, after, fillFields, 
				false, false,
				!weight.scoresDocsOutOfOrder());
		/**
		 FSMediaCollector collector = new FSMediaCollector(sort, nDocs, after,
				fillFields, doDocScores,
				doMaxScore, !weight.scoresDocsOutOfOrder());
		 */
		search(leaves, weight, collector);
		return (TopFieldDocs) collector.topDocs();
	}

}
