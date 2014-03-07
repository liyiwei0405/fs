package com.funshion.gamma.atdd;

import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;

public interface ResultComparator {

	public void compare(Object input, Object real, Object expect, DeserializedFieldInfo cmpMask)throws Exception;
	public void compare(GammaTestCase input, Object real)throws Exception;
//	public boolean equals(Object real, Object expect);
}
