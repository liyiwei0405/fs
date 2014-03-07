package com.funshion.search.utils.systemWatcher.heartBeat;

import static org.junit.Assert.*;

import org.junit.Test;

public class ISNHBInfoTest {
	ISNHBInfo a1 = new ISNHBInfo("127.0.0.1", 154, "ses"), a2 = new ISNHBInfo("127.0.0.1", 154, "ses");
	@Test
	public void testHashCode() {
		assertEquals("expect a1. hashcode equals a2 ", a1.hashCode(), a2.hashCode());
	}

}
