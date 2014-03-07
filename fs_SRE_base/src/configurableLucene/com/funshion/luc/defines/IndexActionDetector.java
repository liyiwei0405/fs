package com.funshion.luc.defines;

import org.apache.lucene.index.Term;

import com.funshion.search.IndexableRecord;

public abstract class IndexActionDetector {
	/**
	 * NOT thread safe!
	 * @author liying
	 *
	 */
	public static enum ActionType{
		ADD, UPDATE, SKIP, DEL;
		public Term delTerm;
	}
	public abstract ActionType checkType(IndexableRecord rec) throws Exception;
}
