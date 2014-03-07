package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;


public class FSMediaCollector extends Collector{
	TopFieldCollector coll;
	public FSMediaCollector(Sort sort, int numHits, FieldDoc after,
		      boolean fillFields, boolean trackDocScores, boolean trackMaxScore,
		      boolean docsScoredInOrder) throws IOException{
		coll = TopFieldCollector.create(sort, numHits, fillFields, trackDocScores, trackMaxScore, docsScoredInOrder);
//		coll = TopFieldCollector.create(sort, numHits, fillFields, true, true, docsScoredInOrder);
	}
	@Override
	public void setScorer(Scorer scorer) throws IOException {
		coll.setScorer(new FSMediaScore(scorer));
	}

	@Override
	public void collect(int doc) throws IOException {
		coll.collect(doc);
	}

	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		coll.setNextReader(context);
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return coll.acceptsDocsOutOfOrder();
	}
	
	public TopDocs topDocs(){
		return coll.topDocs();
	}
	
}
