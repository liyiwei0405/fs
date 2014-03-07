package com.funshion.search.media.chgWatcher;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class MediaRecordTest {
	
	@Test
	public void testSplitToList(){
		MediaRecord mr = new MediaRecord();
		String input = "a / b / c";
		List<String> res = mr.splitToList(input, " / ");
		assertEquals(res.get(0), "a");
		assertEquals(res.get(1), "b");
		assertEquals(res.get(2), "c");
	}

}
