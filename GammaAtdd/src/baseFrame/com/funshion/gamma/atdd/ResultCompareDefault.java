package com.funshion.gamma.atdd;

import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;

public class ResultCompareDefault extends ResultComparatorBase{

	@Override
	public void compare(Object input, Object real, Object expect, DeserializedFieldInfo cmpMask) throws Exception {
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
		public List<String>data = new ArrayList<String>();
	}
	public static void main(String[] args) throws Exception {
		K k = new K();
		k.data.add("1");
		k.data.add("11");
		K k2 = new K();
		k2.data.add("1");
		k2.data.add("2");
		ResultCompareIgnoreRetMsg dft = new ResultCompareIgnoreRetMsg();
		dft.assertEquals("", "", "", k, k2, null);
	}
}
