package com.funshion.luc.defines;

import org.apache.lucene.search.Query;

public abstract class LikeQueryMaker {

	public abstract Query makeLikeQuery(String field, String word)throws Exception;
}
