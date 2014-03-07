package com.funshion.gamma.atdd.healthWatcher;

import java.net.URLEncoder;

import com.funshion.search.utils.Charsets;

public class TestEncoder {
	public static void main(String[] args) throws Exception {
		String x = "~~!@#$%^&~()_~~~~{};'[].`1234567890-=~!@#$%^&~()_+~~.testcase";
		String s = URLEncoder.encode(x, Charsets.UTF8_STR);
		System.out.println(s);
	}
}
