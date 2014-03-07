package com.funshion.retrieve.media;

import com.funshion.search.media.search.FieldDefine;

public enum StoreType{
		INT, TXT;
		public static StoreType getType(char c){
			switch(c){
			case FieldDefine.FIELD_NAME_CHAR_UNIC_ID:
			case FieldDefine.FIELD_NAME_CHAR_TATICS:
//			case FieldDefine.FIELD_NAME_CHAR_ISPLAY:
			case FieldDefine.FIELD_NAME_CHAR_ORDERING:
			case FieldDefine.FIELD_NAME_CHAR_COVER_PIC_ID:
			case FieldDefine.FIELD_NAME_CHAR_TA_0:
			case FieldDefine.FIELD_NAME_CHAR_TA_1:
			case FieldDefine.FIELD_NAME_CHAR_TA_2:
			case FieldDefine.FIELD_NAME_CHAR_TA_3:
			case FieldDefine.FIELD_NAME_CHAR_TA_4:
			case FieldDefine.FIELD_NAME_CHAR_TA_5:
			case FieldDefine.FIELD_NAME_CHAR_TA_6:
			case FieldDefine.FIELD_NAME_CHAR_TA_7:
			case FieldDefine.FIELD_NAME_CHAR_TA_8:
			case FieldDefine.FIELD_NAME_CHAR_TA_9:
			case FieldDefine.FIELD_NAME_CHAR_COPYRIGHT:
			case FieldDefine.FIELD_NAME_CHAR_PLAY_NUM:
			case FieldDefine.FIELD_NAME_CHAR_PLAY_AFTER_NUM:
			case FieldDefine.FIELD_NAME_CHAR_VOTENUM:
			case FieldDefine.FIELD_NAME_CHAR_WANT_SEE_NUM:
			case FieldDefine.FIELD_NAME_CHAR_PROGRAM_TYPE:
			case FieldDefine.FIELD_NAME_CHAR_RELEASE_DATE:
			case FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET:
				return StoreType.INT;
			}
			return StoreType.TXT;
		}
	}