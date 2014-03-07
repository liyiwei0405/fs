package com.funshion.search.media.search.mediaTitleRewriter;

import static org.junit.Assert.fail;

import org.junit.Test;

public class TitleSuffixNumPatternTest {
	//	TitleSuffixNumPattern
	@Test
	public void testRewriteMediaTitle() {
		String org [] = new String[]{
				"大话西游第一	集",
				"武林外传第	一八季",
				"笑傲江湖第	十六辑",
				"痴心妄想第三十三 集",
				"bye第六四	部",

				"第一  集大话西游",
				"第一八   季武林外传",
				"第     十六部笑傲江湖",
				"第	 三十三辑痴心妄想",
				"第	六四  季bye",

				"第	一二   集大话西游第一集",
				"第   	一九 集武林外传第一八季",
				"第		十七 集笑傲江湖第十六部",
				"第 三十四		集痴心妄想第三十三辑",
				"第		六五 集bye第六四集",

				"第一 集",
				"第		一八部",
				"第	 十六  季",
				"第三十三 辑",
				"第		六四集",
		};
		MediaTitleSuffixNumFormatResult expect[] = new MediaTitleSuffixNumFormatResult[]{
				MediaTitleSuffixNumFormatResult.make("大话西游第一 集", "大话西游", 1),
				MediaTitleSuffixNumFormatResult.make("武林外传第 一八季", "武林外传", 18),
				MediaTitleSuffixNumFormatResult.make("笑傲江湖第 十六辑", "笑傲江湖", 16),
				MediaTitleSuffixNumFormatResult.make("痴心妄想第三十三 集", "痴心妄想", 33),
				MediaTitleSuffixNumFormatResult.make("bye第六四 部", "bye", 64),
				MediaTitleSuffixNumFormatResult.make("第一 集大话西游", "大话西游", 1),
				MediaTitleSuffixNumFormatResult.make("第一八 季武林外传", "武林外传", 18),
				MediaTitleSuffixNumFormatResult.make("第 十六部笑傲江湖", "笑傲江湖", 16),
				MediaTitleSuffixNumFormatResult.make("第 三十三辑痴心妄想", "痴心妄想", 33),
				MediaTitleSuffixNumFormatResult.make("第 六四 季bye", "bye", 64),
				MediaTitleSuffixNumFormatResult.make("第 一二 集大话西游第一集", "第 一二 集大话西游", 1),
				MediaTitleSuffixNumFormatResult.make("第 一九 集武林外传第一八季", "第 一九 集武林外传", 18),
				MediaTitleSuffixNumFormatResult.make("第 十七 集笑傲江湖第十六部", "第 十七 集笑傲江湖", 16),
				MediaTitleSuffixNumFormatResult.make("第 三十四 集痴心妄想第三十三辑", "第 三十四 集痴心妄想", 33),
				MediaTitleSuffixNumFormatResult.make("第 六五 集bye第六四集", "第 六五 集bye", 64),
				
				null,
				null,
				null,
				null,
				null,
		};
		
		for(String x : org){
			MediaTitleSuffixNumFormatResult mrr = MediaTitleSuffixNumFormatResult.rewriteTitle(x);
			if(mrr == null){
				System.out.println((String)null);
			}else{
				System.out.println(String.format("\"%s\", \"%s\", %s",  mrr.nameCnOrg, mrr.getNewNameCn(), mrr.getNum())); 
			}
		}
		for(int x = 0; x < org.length; x ++){
			System.out.println("testing " + org[x]);
			String v = org[x];
			MediaTitleSuffixNumFormatResult result = MediaTitleSuffixNumFormatResult.rewriteTitle(v);
			if(result == null){
				if(expect[x] != null){
					fail("not match! result is " + result + ", but expect is " + expect[x]);
				}else{
					System.out.println("test passed for " + org[x]);
				}
			}else{
				if(result.equals(expect[x])){
					System.out.println("test passed for " + org[x]);
				}else{
					System.out.println(String.format("\"%s\", \"%s\", %s",  result.nameCnOrg, result.getNewNameCn(), result.getNum())); 
					fail("not match! result is " + result + ", but expect is " + expect[x]);
				}
				
			}
		}
	}

}
