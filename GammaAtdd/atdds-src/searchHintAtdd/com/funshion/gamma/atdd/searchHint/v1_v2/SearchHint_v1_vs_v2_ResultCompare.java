package com.funshion.gamma.atdd.searchHint.v1_v2;

import com.funshion.gamma.atdd.ResultComparatorBase;
import com.funshion.search.utils.LogHelper;

public class SearchHint_v1_vs_v2_ResultCompare extends ResultComparatorBase{


	@Override
	public void compare(Object input, Object real, Object expect) throws Exception {
		if(checkIsNull("", input, real, expect)){
			LogHelper.log.warn("expect and real all is null? checker restrict, maybe error call! for input %s",
					input );
		}
		com.funshion.searchHint.thrift.v1.HintResult exp = 
				(com.funshion.searchHint.thrift.v1.HintResult) expect;
		com.funshion.searchHint.thrift.v2.HintResult rea = 
				(com.funshion.searchHint.thrift.v2.HintResult) real;

		assertListEquals("", input,
				exp.records,
				rea.records, null);
		message("match!", input, real, expect);

	}

}
