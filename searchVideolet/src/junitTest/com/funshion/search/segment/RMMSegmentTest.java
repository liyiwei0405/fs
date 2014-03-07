package com.funshion.search.segment;

import static org.junit.Assert.*;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

public class RMMSegmentTest {

	@Test
	public void test() {
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
		RMMSegment seg = RMMSegment.instance;
		String toSeg;
		TokenHandler handler;
		toSeg = "";
		handler = seg.segment(toSeg);
		assertFalse("seg fail", handler.hasNext());
		
		toSeg = " .?)[}】";
		handler = seg.segment(toSeg);
		assertFalse("seg fail", handler.hasNext());
		
		
		toSeg = " .?你好)[}】";
		handler = seg.segment(toSeg);
		assertTrue("seg fail", handler.hasNext());
		handler.next();
		assertFalse("seg fail", handler.hasNext());
		
		toSeg = " .?大话西游and降魔篇)[}】";
		handler = seg.segment(toSeg);
		System.out.println(handler);
		assertTrue("seg fail", handler.hasNext());
		handler.next();
		assertTrue("seg fail", handler.hasNext());
		handler.next();
		assertTrue("seg fail", handler.hasNext());
		handler.next();
		assertTrue("seg fail", handler.hasNext());
		handler.next();
		assertTrue("seg fail", handler.hasNext());
		handler.next();
		assertFalse("seg fail", handler.hasNext());
	}

}
