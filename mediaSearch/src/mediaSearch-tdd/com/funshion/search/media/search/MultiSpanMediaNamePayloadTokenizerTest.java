package com.funshion.search.media.search;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

public class MultiSpanMediaNamePayloadTokenizerTest {

	@Test
	public void test() throws IOException {
		String toSeg = 
				"123	四五六 七八9	ni bye	我恨你"
						+ '\n' +
						"灰色		j/D四八9	99归一	狠心0"
						+ '\n' +
						"JBt	四五六 七八9	ni bye	的武王f？"
						+ '\n' +
						"123	四五六 七八9	ni bye	我恨你/pz ";
		String expect[] = new String[]{
				"123", "$",	"四", "五", "六", "七", "八", "9", "$", "ni", "bye", "$", "我", "恨", "你", "$",
				"灰", "色", "$",		"j", "d", "四", "八", "9", "$", "99", "归", "一",	"$", "狠", "心", "0", "$",
				"jbt", "$",	"四", "五", "六", "七", "八", "9", "$", "ni", "bye", "$", "的", "武", "王", "f", "$",
				"123", "$",	"四", "五","六", "七", "八", "9", "$",	"ni", "bye", "$", "我", "恨", "你", "pz", "$"
		};

		MultiSpanMediaNamePayloadTokenizer t = new MultiSpanMediaNamePayloadTokenizer(new StringReader(toSeg));
		Class termAtt= CharTermAttribute.class;
		Class payloadAtt = PayloadAttribute.class;
		int idx = 0;
		while(t.incrementToken()){
			CharTermAttribute ct = (CharTermAttribute)t.getAttribute(termAtt);
			PayloadAttribute pl = (PayloadAttribute)t.getAttribute(payloadAtt);
			String str = new String(ct.buffer(), 0, ct.length());
			if(idx >= expect.length){
				fail("expect end reach! expect.length : " + expect.length);
			}
			String now = expect[idx ++];

			if(now.equals(str)){
				System.out.println("match: " + idx + ", for " + now);
			}else{
				System.out.println("NOT match: " + idx + ", for " + now);
				fail("NOT match: " + idx + ", for " + now);
			}
			BytesRef br = pl.getPayload();
			if("$".equals(now)){
				assertTrue(br.bytes[0] < 1);
				System.out.println("   payload : " + ((0xff &br.bytes[0])<<12));
			}else{
				assertTrue(br.bytes[0] == 1);
			}
		}
		if(idx != expect.length){
			fail("expect end NOT reach! expect.length : " + expect.length);
		}
		t.close();
	}


}
