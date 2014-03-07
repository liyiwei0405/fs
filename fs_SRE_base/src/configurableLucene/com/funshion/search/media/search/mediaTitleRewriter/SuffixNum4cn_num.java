package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuffixNum4cn_num extends TitleSuffixNumPattern{
//FIXME here has a problem 一九四二 2 ==》1942 2 will not match！
	protected SuffixNum4cn_num() {
		super(Pattern.compile("(.+[\\u4E00-\\u9FA0]+) {0,}(\\d{1,2})$"));
	}

	@Override
	public MediaTitleSuffixNumFormatResult rewrite(String input) {
		Matcher matcher = this.pattern.matcher(input);
		boolean matched = matcher.matches();
		if(!matched){
			return null;
		}
		int ep = Integer.parseInt(matcher.group(2));
		return MediaTitleSuffixNumFormatResult.make(input, matcher.group(1), ep);
	}
}