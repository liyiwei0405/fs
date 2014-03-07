package com.funshion.videoService.search;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

import com.funshion.luc.defines.LikeOperationTokenizerDefault;
import com.funshion.luc.defines.LikeQueryMaker;

public class VideoletLikeQueryMaker extends LikeQueryMaker{
	public static final String fieldName = "titleLike".toUpperCase();
	//only support title
	@Override
	public Query makeLikeQuery(String field, String toSearch) throws Exception {
		if(!field.equalsIgnoreCase("TITLE")){
			throw new Exception("'like' only support FIELD 'TITLE', now is " + field);
		}

		PhraseQuery pq = new PhraseQuery();
		List<Term>terms = LikeOperationTokenizerDefault.toTerms(
				fieldName,
				toSearch);
		for(Term term : terms){
			pq.add(term);
		}
		return pq;
	}
}
