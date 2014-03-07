package com.funshion.search.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class KeyValuePairTest {
	private KeyValuePair<String, String> kv = new KeyValuePair<String, String>("key", "value");
	
	@Test
	public void testGet(){
		assertEquals("key", kv.getKeyString());
		assertEquals("value", kv.getValueString());
		assertEquals("key = value", kv.toString());
	}
	
	@Test
	public void testParseLine(){
		assertEquals(null, KeyValuePair.parseLine(null));
		assertEquals(null, KeyValuePair.parseLine("#key=value"));
		assertEquals(null, KeyValuePair.parseLine("=key=value"));
		assertEquals(null, KeyValuePair.parseLine("keyvalue"));
		assertEquals("key", KeyValuePair.parseLine("key=value").getKeyString());
		assertEquals("value", KeyValuePair.parseLine("key=value").getValueString());
		assertEquals("value", KeyValuePair.parseLine("key  = value").getValueString());
		assertEquals("value", KeyValuePair.parseLine(" key=   value").getValueString());
	}
}
