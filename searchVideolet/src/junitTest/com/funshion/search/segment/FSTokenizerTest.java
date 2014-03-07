package com.funshion.search.segment;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;

import com.funshion.search.analyzers.FSTokenizer;

public class FSTokenizerTest {

	@Test
	public void testIncrementToken() throws IOException {
		String str = "  jisa的份上口儿肯 定是４   ６５４５４\t５４   ";
		String expect = "  jisa的份上口儿肯 定是4   65454	54   ";
		StringReader reader = new StringReader(str);
		FSTokenizer fst = new FSTokenizer(reader);
		boolean inc = fst.incrementToken();
		assertTrue("increament fail!", inc);
		OffsetAttribute att = fst.getAttribute(OffsetAttribute.class);
		int startOffset = att.startOffset();
		int endOffset = att.endOffset();
		System.out.println(startOffset + "\t" + endOffset);
		
		CharTermAttribute cta = fst.getAttribute(CharTermAttribute.class);
		System.out.println(cta.toString());
		assertEquals("not equals! new version?", cta.toString(), expect);
		inc = fst.incrementToken();
		assertFalse("should has only one token for this class", inc);
		
		fst.reset();
		inc = fst.incrementToken();
		assertFalse("should has only one token for this class", inc);
		
		fst.close();
	}



}
