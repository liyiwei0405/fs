package com.funshion.luc.defines;

import org.apache.lucene.analysis.Analyzer.ReuseStrategy;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;

public final class IFieldReuseStrategy extends ReuseStrategy {
	ITableDefine tabDef;
	public IFieldReuseStrategy() {
		this.tabDef = ITableDefine.instance;
	}

	@Override
	public TokenStreamComponents getReusableComponents(String fieldName) {
		return tabDef.getReusableComponents(fieldName);
	}

	@Override
	public void setReusableComponents(String fieldName,
			TokenStreamComponents components) {
		tabDef.setReusableComponents(fieldName,
				components);
	}
}