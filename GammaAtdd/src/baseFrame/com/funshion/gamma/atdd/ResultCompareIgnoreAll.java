package com.funshion.gamma.atdd;

import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;

public class ResultCompareIgnoreAll extends ResultComparatorBase{

	@Override
	public void compare(Object input, Object real, Object expect, DeserializedFieldInfo cmpMask) throws Exception {
		return;
	}

	@Override
	public void compare(GammaTestCase input, Object real) throws Exception {
		compare(input.paras, real, input.expect, input.cmpMask);
	}


}
