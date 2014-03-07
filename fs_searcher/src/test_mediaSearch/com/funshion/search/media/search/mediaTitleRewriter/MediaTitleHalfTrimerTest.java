package com.funshion.search.media.search.mediaTitleRewriter;

import static org.junit.Assert.*;

import org.junit.Test;

public class MediaTitleHalfTrimerTest {

	@Test
	public void testTrim() {
		String key[] = new String[]{
				null,
				"",
				"你好",
				"全集你好",
				"你好全集",
				"视频你好",
				"你好视频",
				"未删节版你好",
				"你好未删节版",
				"未删减版你好",
				"你好未删减版",
				"你好未删节",
				"未删节你好",
				"删减你好",
				"你好未删",
				"你好节减",
				"节减你好",
				"你好节减",
		};
		String expect[] = new String[]{
				null,
				null,
				null,
				"你好",
				"你好",
				"你好",
				"你好",
				"你好",
				"你好",
				"你好",
				"你好",
				"你好",
				"你好",
				null,
				null,
				null,
				null,
				null,
		};
		for(int x = 0; x < key.length; x ++){
			String[] to = MediaTitleSuffixSpecialWordTrimer.instance.trim(key[x]);
			if(to == null ){
				if(expect[x] != null){
					fail("MediaTitleHalfTrimer trim fail! for '" + key[x] 
							+ "', expect '" + expect[x] + "', but get '" + to + "'");
				}
			}else{
				assertEquals("MediaTitleHalfTrimer trim fail! for '" + key[x] 
						+ "', expect '" + expect[x] + "', but get '" + to[0] + "'",
						to[0], expect[x]);
			}
		}
	}
	@Test
	public void getAliasTest(){
		String[] aliaWord = new String[]{
				null,
				"未删节",
				"未删节版",
				"视频",
				"大象"
		};
		String expect[] = new String[]{
				null,
				"未删节版",
				"未删节版",
				"视频",
				"大象",
		};
		for(int idx = 0; idx < aliaWord.length; idx ++){
			String x = aliaWord[idx];
			String real = MediaTitleSuffixSpecialWordTrimer.instance.getAlia(x);
			assertEquals("not equal for " + aliaWord[idx], real, expect[idx]);
		}
	}
}
