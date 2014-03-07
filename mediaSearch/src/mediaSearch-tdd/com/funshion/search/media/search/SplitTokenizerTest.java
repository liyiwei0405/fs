package com.funshion.search.media.search;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import com.funshion.luc.defines.SplitTokenizer;

public class SplitTokenizerTest {
	SplitTokenizer sp ;
	String str = "	 古往今来，InternNet 分发复杂，2015将要出现的I567型号的5#PC将会代替其他产品，表现为 	300￥一下的（数码产品）。";
	String[] tokens = new String[]{
			"",
			"古往今来 internnet 分发复杂 2015将要出现的i567型号的5 pc将会代替其他产品 表现为",
			"300 一下的 数码产品"	
	};
	void init(){
		sp = new SplitTokenizer(new StringReader(str));
	}
	@Test
	public void testIncrementToken() throws IOException {
		init();
		CharTermAttribute termAtt = sp.getAttribute(CharTermAttribute.class);
		for(String x : tokens){
			assertTrue("no result for '" + x + "'", sp.incrementToken());
			String token = termAtt.toString();
			assertTrue("mismatch! expect '" + x +"', but get '" + token + "'", token.equals(x));
		}
		assertFalse("has more element as '" + termAtt + "'", sp.incrementToken());
		System.out.println("increment token test PASS");
	}

	@Test
	public void testReset() throws IOException {
		testIncrementToken();
		System.out.println("increment token test PASS");
		sp.reset();
		System.out.println("resetted");
		testIncrementToken();
		System.out.println("reset token test PASS");
	}

}
