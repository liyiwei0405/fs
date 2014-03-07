package org.apache.lucene.search;

import org.apache.lucene.search.similarities.PerFieldSimilarityWrapper;
import org.apache.lucene.search.similarities.Similarity;

import com.funshion.search.media.search.FieldDefine;

public class SimlarityProvider extends PerFieldSimilarityWrapper{

	@Override
	public Similarity get(String name) {
		char c = name.charAt(0);
		if(c == FieldDefine.FIELD_NAME_CHAR_NAMES
				|| c == FieldDefine.FIELD_NAME_CHAR_NAME_CN
				|| c == FieldDefine.FIELD_NAME_CHAR_NAME_EN
				|| c == FieldDefine.FIELD_NAME_CHAR_NAME_SN
				|| c == FieldDefine.FIELD_NAME_CHAR_NAME_OT){
			return new FsMediaSimilarity();
		}
		
		return new FsNoScoreSimilarity();
		
	}

}
