package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuffixNum4Season extends TitleSuffixNumPattern{

	protected SuffixNum4Season() {
		super(Pattern.compile("(.+)season {0,}(\\d+)$"));
	}

	@Override
	public MediaTitleSuffixNumFormatResult rewrite(String input) {
		Matcher matcher = this.pattern.matcher(input.toLowerCase());
		boolean matched = matcher.matches();
		if(!matched){
			return null;
		}
		int ep = Integer.parseInt(matcher.group(2));
		return MediaTitleSuffixNumFormatResult.make(input, matcher.group(1), ep);
	}

}