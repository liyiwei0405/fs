package com.funshion.search.media.search;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import com.funshion.search.analyzers.RMMSegmentTokenizer;

/**
 * @author liying
 *
 */
public final class FSMediaChineseAnalyzer extends Analyzer {

	/**
	 * Create a new SmartChineseAnalyzer, using the default stopword list.
	 */
	public FSMediaChineseAnalyzer() {
		super(new FieldReuseStrategy());
	}

	@Override
	public TokenStreamComponents createComponents(String fieldName, Reader reader) {
		final Tokenizer tokenizer;
		char c = fieldName.charAt(0);
		switch(c){
		case FieldDefine.FIELD_NAME_CHAR_IS_HD:
		case FieldDefine.FIELD_NAME_CHAR_ISPLAY:
		case FieldDefine.FIELD_NAME_CHAR_RELATED_PREIDS:
		case FieldDefine.FIELD_NAME_CHAR_DISPLAY_TYPE:
		case FieldDefine.FIELD_NAME_CHAR_COUNTRY:
		case FieldDefine.FIELD_NAME_CHAR_TAG_4_EDITOR:
		case FieldDefine.FIELD_NAME_CHAR_RELEASE_INFO:
		case FieldDefine.FIELD_NAME_CHAR_MEDIA_CLASSID:
		case FieldDefine.FIELD_NAME_CHAR_AREA_TACTIC:
			tokenizer = new SplitTokenizer(reader);
			return new TokenStreamComponents(tokenizer, tokenizer);
		case FieldDefine.FIELD_NAME_CHAR_NAMES:
			tokenizer = new MultiMediaNamePayloadTokenizer(reader);
			return new TokenStreamComponents(tokenizer, tokenizer);
		case FieldDefine.FIELD_NAME_CHAR_NAME_CN:
			tokenizer = new MediaNamePayloadTokenizer(reader, 0);
			return new TokenStreamComponents(tokenizer, tokenizer);
		case FieldDefine.FIELD_NAME_CHAR_NAME_EN:
			tokenizer = new MediaNamePayloadTokenizer(reader, 1);
			return new TokenStreamComponents(tokenizer, tokenizer);
		case FieldDefine.FIELD_NAME_CHAR_NAME_OT:
			tokenizer = new MediaNamePayloadTokenizer(reader, 2);
			return new TokenStreamComponents(tokenizer, tokenizer);
		case FieldDefine.FIELD_NAME_CHAR_NAME_SN:
			tokenizer = new MediaNamePayloadTokenizer(reader, 3);
			return new TokenStreamComponents(tokenizer, tokenizer);
//		case FieldDefine.FIELD_NAME_CHAR_PHRAGEQUERY:
//			tokenizer = new MultiSpanMediaNamePayloadTokenizer(reader);
//			return new TokenStreamComponents(tokenizer, tokenizer);
		default:
			tokenizer = new RMMSegmentTokenizer(reader);
			return new TokenStreamComponents(tokenizer, tokenizer);
		}

	}
}
