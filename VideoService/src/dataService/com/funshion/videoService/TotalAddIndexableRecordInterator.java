package com.funshion.videoService;

import java.util.Iterator;

import com.funshion.luc.defines.IndexActionDetector.ActionType;
import com.funshion.search.IndexableRecord;
import com.funshion.videoService.thrift.VideoletInfo;

public class TotalAddIndexableRecordInterator implements Iterator<IndexableRecord>{

	final Iterator<VideoletInfo> itr;
	public TotalAddIndexableRecordInterator(Iterator<VideoletInfo> itr){
		this.itr = itr;
	}
	@Override
	public boolean hasNext() {
		return itr.hasNext();
	}
	
	@Override
	public IndexableRecord next() {
		VideoletInfo vi = itr.next();
		try {
			return new VideoletIndexableRecord(vi, ActionType.ADD);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		throw new RuntimeException("not support method #remove() for " + this.getClass());
	}

}
