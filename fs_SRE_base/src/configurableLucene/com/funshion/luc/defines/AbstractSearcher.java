package com.funshion.luc.defines;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FSMediaIndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SimlarityProvider;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

import com.funshion.search.utils.LogHelper;

public abstract class AbstractSearcher {

	public final FSMediaIndexSearcher sIns;
	public final LogHelper log = new LogHelper("mediaSearcher");
	public AbstractSearcher(IndexReader ir) throws IOException{
		sIns = new FSMediaIndexSearcher(ir);
		sIns.setSimilarity(new SimlarityProvider());
	}

	public TopDocs search(Query bqx, Sort sort, int topN) throws Exception{
		
		long time1 = System.nanoTime();
//		log.info("query is topN %s, query %s, sort %s, by %s", topN, bqx, sort, sIns);
		TopDocs td =  this.sIns.search(bqx, topN, sort);
		long time2 = System.nanoTime();

		if(log.logger.isDebugEnabled()){
			log.debug("search search use %s", (time2 - time1) / 10000/100.0);
		}
		return td;
	}
}
