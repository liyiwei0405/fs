package com.funshion.fsql.expression.ast;

import org.apache.lucene.search.Query;

import com.funshion.fsql.expression.FsqlInterpreter;

public interface QueryMakable {
	public Query toQueryElement(FsqlInterpreter interpreter) throws Exception;
}
