package com.funshion.search.videolet.search;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import com.funshion.search.analyzers.FSTokenizer;
import com.funshion.search.analyzers.StrictTokenizer;

/**
 * 
 * @author liying
 *
 */
public final class FSVideoletChineseAnalyzer extends Analyzer {
	public final static class FieldReuseStrategy extends ReuseStrategy {
		TokenStreamComponents tStrict;
		TokenStreamComponents tSegment;
		@Override
		public TokenStreamComponents getReusableComponents(String fieldName) {
			if(fieldName.equals(VideoletIndexer.INDEX_TAGS_STRICT) || fieldName.equals(VideoletIndexer.INDEX_TITLE_STRICT)){//FIXME how to use internal?
				return tStrict;
			}else{
				return tSegment;
			}
		}

		@Override
		public void setReusableComponents(String fieldName,
				TokenStreamComponents components) {
			if(fieldName.equals(VideoletIndexer.INDEX_TAGS_STRICT) || fieldName.equals(VideoletIndexer.INDEX_TITLE_STRICT)){//FIXME how to use internal?
				tStrict = components;
			}else{
				tSegment = components;
			}
		}
	}

	/**
	 * Create a new SmartChineseAnalyzer, using the default stopword list.
	 */
	public FSVideoletChineseAnalyzer() {
		super(new FieldReuseStrategy());
	}

	@Override
	public TokenStreamComponents createComponents(String fieldName, Reader reader) {
		if(fieldName.equals(VideoletIndexer.INDEX_TAGS_STRICT) || fieldName.equals(VideoletIndexer.INDEX_TITLE_STRICT)){//FIXME how to use internal?
			Tokenizer tokenizer = new StrictTokenizer(reader);
			return new TokenStreamComponents(tokenizer, tokenizer);
		}else{
			Tokenizer tokenizer = new FSTokenizer(reader);
			return new TokenStreamComponents(tokenizer, tokenizer);
		}

	}
}
