package org.apache.lucene.search;

import org.apache.lucene.search.similarities.PerFieldSimilarityWrapper;
import org.apache.lucene.search.similarities.Similarity;

import com.funshion.luc.defines.ITableDefine;


public class SimlarityProvider extends PerFieldSimilarityWrapper{

	@Override
	public Similarity get(String name) {
		if(ITableDefine.instance.isScoreField(name)){
			return new FsMediaSimilarity();
		}
		
		
		return new FsNoScoreSimilarity();
		
	}

}
