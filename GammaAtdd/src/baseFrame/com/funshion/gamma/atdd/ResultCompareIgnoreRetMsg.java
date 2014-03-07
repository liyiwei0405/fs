package com.funshion.gamma.atdd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;

public class ResultCompareIgnoreRetMsg extends ResultComparatorBase{
	public static final String ignoreName = "retMsg";
	static class IgnoreRetMsgMask extends DeserializedFieldInfo{
		public IgnoreRetMsgMask(Object expect) throws Exception {
			super("IgnoreRetMsgMask", expect);
			Field f = expect.getClass().getField(ignoreName);
			if(f != null){
				DeserializedFieldInfo fi = new DeserializedFieldInfo(ignoreName, null);
				fi.setIgnoreCompare();
				super.regMask(ignoreName, fi);
			}
		}
	}
	@Override
	public void compare(Object input, Object real, Object expect, DeserializedFieldInfo cmpMask) throws Exception {
		if(cmpMask == null){
			cmpMask = new IgnoreRetMsgMask(expect);
		}else{
			DeserializedFieldInfo fi = new DeserializedFieldInfo(ignoreName, null);
			fi.setIgnoreCompare();
			cmpMask.regMask(ignoreName, fi);
		}
		if(real == null){
			if(expect == null){
				return;
			}else{
				throw new CompareFailException(input, real, expect);
			}
		}else{
			super.assertEquals("", "", input, real, expect, cmpMask);
		}
		if(isPrintResult()){
			log.info("match! input = %s, \n\t expect: %s \n\t   real: %s", input, expect, real);
		}
	}

	@Override
	public void compare(GammaTestCase input, Object real) throws Exception {
		compare(input.paras, real, input.expect, input.cmpMask);
	}

	
	
	static class K{
		public String retMsg = "OK";
		public List<String>data = new ArrayList<String>();
	}
	public static void main(String[] args) throws Exception {
		K k = new K();
		k.data.add("1");
		k.data.add("11");
		K k2 = new K();
		k2.retMsg = "sdfasdfas";
		k2.data.add("1");
		k2.data.add("11");
//		ResultCompareDefault dft = new ResultCompareDefault();
		ResultCompareIgnoreRetMsg dft = new ResultCompareIgnoreRetMsg();
		dft.compare("", k, k2, null);
	}
}
