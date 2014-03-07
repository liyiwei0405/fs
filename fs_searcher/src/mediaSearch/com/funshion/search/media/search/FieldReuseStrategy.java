package com.funshion.search.media.search;

import org.apache.lucene.analysis.Analyzer.ReuseStrategy;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;

public final class FieldReuseStrategy extends ReuseStrategy {
		TokenStreamComponents tSplit;
		TokenStreamComponents tMultiName, tNameCn, tNameEn, tNameSn, tNameOt, tPhrase;
		TokenStreamComponents tSegment;
		@Override
		public TokenStreamComponents getReusableComponents(String fieldName) {
			char c = fieldName.charAt(0);
			switch(c){
			case FieldDefine.FIELD_NAME_CHAR_IS_HD:
			case FieldDefine.FIELD_NAME_CHAR_ISPLAY:
			case FieldDefine.FIELD_NAME_CHAR_RELATED_PREIDS:
			case FieldDefine.FIELD_NAME_CHAR_DISPLAY_TYPE:
			case FieldDefine.FIELD_NAME_CHAR_COUNTRY:
			case FieldDefine.FIELD_NAME_CHAR_TAG_4_EDITOR:
			case FieldDefine.FIELD_NAME_CHAR_RELEASE_INFO:
			case FieldDefine.FIELD_NAME_CHAR_AREA_TACTIC:
			case FieldDefine.FIELD_NAME_CHAR_MEDIA_CLASSID:
				return tSplit;
			case FieldDefine.FIELD_NAME_CHAR_NAMES:
				return tMultiName;
			case FieldDefine.FIELD_NAME_CHAR_NAME_CN:
				return tNameCn;
			case FieldDefine.FIELD_NAME_CHAR_NAME_EN:
				return tNameEn;
			case FieldDefine.FIELD_NAME_CHAR_NAME_OT:
				return tNameOt;
			case FieldDefine.FIELD_NAME_CHAR_NAME_SN:
				return tNameSn;
//			case FieldDefine.FIELD_NAME_CHAR_PHRAGEQUERY:
//				return tPhrase;
			default:
				return tSegment;
			}
		}

		@Override
		public void setReusableComponents(String fieldName,
				TokenStreamComponents components) {
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
				
				tSplit = components;
				break;
			case FieldDefine.FIELD_NAME_CHAR_NAMES:
				tMultiName = components;
				break;
			case FieldDefine.FIELD_NAME_CHAR_NAME_CN:
				tNameCn = components;
				break;
			case FieldDefine.FIELD_NAME_CHAR_NAME_EN:
				tNameEn = components;
				break;
			case FieldDefine.FIELD_NAME_CHAR_NAME_OT:
				tNameOt = components;
				break;
			case FieldDefine.FIELD_NAME_CHAR_NAME_SN:
				tNameSn = components;
				break;
//			case FieldDefine.FIELD_NAME_CHAR_PHRAGEQUERY:
//				tPhrase = components;
//				break;
			default:
				tSegment = components;	
			}

		}
	}