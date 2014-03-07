package com.funshion.luc.defines;

import org.apache.lucene.search.Query;

public abstract class FullTextQueryMaker {

	public abstract Query makeTitleQuery(String field, String word);
	public abstract Query makeTitleFullQuery(String field, String word) throws Exception;
}
