package com.funshion.gamma.atdd;

import java.util.List;

import com.funshion.gamma.atdd.serialize.DefineIterator;
import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;
import com.funshion.gamma.atdd.serialize.ThriftDeserializeTool;

public class QueryReturnValue {
	public final Object expect;
	public final DeserializedFieldInfo cmpMask;
	
	public QueryReturnValue(Object expect, DeserializedFieldInfo cmpMask){
		this.expect = expect;
		this.cmpMask = cmpMask;
	}
	
	public static QueryReturnValue fromDeserializedFieldInfoList(List<DeserializedFieldInfo> lst) throws Exception{
		
		if(lst.size() != 1){
			throw new Exception("return value must be one, but now get " + lst.size());
		}

		return new QueryReturnValue(lst.get(0).value, lst.get(0));
	}
	public static QueryReturnValue fromStringList(List<String> lst) throws Exception{
		List<DeserializedFieldInfo> fis = ThriftDeserializeTool.deserializeObjects(new DefineIterator(lst));
		return fromDeserializedFieldInfoList(fis);
	}	
	
}
