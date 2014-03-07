package com.funshion.search.media.search.mediaTitleRewriter;

import static org.junit.Assert.*;

import org.junit.Test;

public class Rewrite4cn_numTest {
	SuffixNum4cn_num rr = new SuffixNum4cn_num();
	@Test
	public void testRewrite() {
		String []org = new String[]{
				"",
				"1",
				"12",
				"汉字1",
				"汉字 1",
				"汉字12",
				"汉字 12",
				"汉字123",
				"汉字 123",
				"汉字12 ",
				"汉字",
		};
		MediaTitleSuffixNumFormatResult[] results = new MediaTitleSuffixNumFormatResult[]{
			null,
			null,
			null,
			MediaTitleSuffixNumFormatResult.make("汉字1", "汉字", 1),
			MediaTitleSuffixNumFormatResult.make("汉字 1", "汉字", 1),
			MediaTitleSuffixNumFormatResult.make("汉字12", "汉字", 12),
			MediaTitleSuffixNumFormatResult.make("汉字 12", "汉字", 12),
			null,
			null,
			null,
			null
		};
		for(int x = 0; x < org.length; x ++){
			System.out.println("testing " + org[x]);
			String v = org[x];
			MediaTitleSuffixNumFormatResult result = rr.rewrite(v);
			if(result == null){
				if(results[x] != null){
					fail("not match! result is " + result + ", but expect is " + results[x]);
				}else{
					System.out.println("test passed for " + org[x]);
				}
			}else{
				if(result.equals(results[x])){
					System.out.println("test passed for " + org[x]);
				}else{
					fail("not match! result is " + result + ", but expect is " + results[x]);
				}
				
			}
		}
	}

}
