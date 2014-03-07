package com.funshion.videoService.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.funshion.luc.defines.AnaType;
import com.funshion.luc.defines.IFieldDefine;
import com.funshion.luc.defines.ITableDefine;
import com.funshion.luc.defines.IndexActionDetector;
import com.funshion.search.IndexableRecord;
import com.funshion.search.utils.LogHelper;

public class IncrementPublishFlagDetector extends IndexActionDetector{
	static final LogHelper log = new LogHelper("flagDetector");
	IFieldDefine idField;
	final String videoIdFileldName;
	public IncrementPublishFlagDetector(){
		idField = ITableDefine.instance.idField;
		videoIdFileldName = idField.fieldName;
	}
	@Override
	public ActionType checkType(IndexableRecord rec) throws Exception {

		ActionType type = rec.getActionType();
		if(type == ActionType.ADD){
			type.delTerm = null;
		}else{
			String videoId = rec.valueOf(videoIdFileldName);
			log.info("add delTerm for %s, by videoId's type %s, field %s:%s", type, idField.aType, idField.fieldName, videoId);
			if(idField.aType == AnaType.aInt){
				BytesRef br = new BytesRef();
				NumericUtils.intToPrefixCoded(Integer.parseInt(videoId), 0, br);
				type.delTerm = new Term(idField.fieldName, br);
			}else{
				throw new Exception("not support anaType " + type + " for videoid");
			}
		}
		return type;
	}

}
