package com.funshion.search.utils.systemWatcher.heartBeat;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.funshion.search.utils.systemWatcher.heartBeat.HBCounter;
import com.funshion.search.utils.systemWatcher.heartBeat.ISNHBInfo;

public class HBCounterTest {
	static HBCounter counter;
	static long startTime;
	static ISNHBInfo client = new ISNHBInfo("127.0.0.1", 1543, "testBus");
	@BeforeClass
	public static void init() throws InterruptedException{
		startTime = System.currentTimeMillis();
		counter = new HBCounter(client, 1000, startTime);
	}
	@Test
	public void test() throws InterruptedException{
		assertTrue("beat fail !", counter.beat());
		Thread.sleep(50);
		long now = System.currentTimeMillis();
		counter.beat(now);
		assertEquals("beat count mismatch", 3, counter.getBeatCount());
		assertEquals("beat session mismatch", client, counter.businessClient);
		assertEquals("beat start mismatch", startTime, counter.startTime);
		assertTrue("session lost too early", counter.isValid());
		
		Thread.sleep(900);
		assertEquals("beat count mismatch", 3, counter.getBeatCount());
		assertEquals("beat session mismatch", client, counter.businessClient);
		assertEquals("beat start mismatch", startTime, counter.startTime);
		assertTrue("session lost too early", counter.isValid());
		
		assertTrue("beat fail !", counter.beat());
		Thread.sleep(900);
		assertTrue("beat fail !", counter.beat());
		assertEquals("beat count mismatch", 5, counter.getBeatCount());
		assertEquals("beat session mismatch", client, counter.businessClient);
		assertEquals("beat start mismatch", startTime, counter.startTime);
		assertTrue("session lost too early", counter.isValid());
		
		Thread.sleep(2001);
		assertFalse("should beat fail", counter.beat());
		assertEquals("beat count mismatch", 5, counter.getBeatCount());
		assertEquals("beat session mismatch", client, counter.businessClient);
		assertEquals("beat start mismatch", startTime, counter.startTime);
		assertFalse("session lost too late", counter.isValid());
		
		System.out.println(counter);
	}

}
