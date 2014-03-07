package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuffixNum4EP extends TitleSuffixNumPattern{

	protected SuffixNum4EP() {
		super(Pattern.compile("(.+)[Ee][Pp] {0,}(\\d+)$"));
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