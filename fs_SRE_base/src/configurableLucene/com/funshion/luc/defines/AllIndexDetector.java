package com.funshion.luc.defines;

import com.funshion.search.IndexableRecord;

public class AllIndexDetector extends IndexActionDetector{

	@Override
	public ActionType checkType(IndexableRecord rec) {
		return ActionType.ADD;
	}

}
