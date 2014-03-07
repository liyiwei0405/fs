package com.funshion.luc.defines;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;

/**
 * @author liying
 *
 */
public final class IAnalyzer extends Analyzer {
	
	/**
	 * Create a new SmartChineseAnalyzer, using the default stopword list.
	 */
	public IAnalyzer() {
		super(new IFieldReuseStrategy());
	}

	@Override
	public TokenStreamComponents createComponents(String fieldName, Reader reader) {
		return ITableDefine.instance.createComponents(fieldName, reader);
	}
}
