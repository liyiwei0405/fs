package com.funshion.search;

public interface IndexChannel {

	public IndexableRecordQueue openChannel(boolean isNew) throws Exception;
	
	public void rotateIndex(IndexableRecordQueue queue);
	
}
