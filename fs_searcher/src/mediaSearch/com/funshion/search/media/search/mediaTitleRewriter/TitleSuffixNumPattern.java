package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Pattern;

import com.funshion.search.media.search.mediaTitleRewriter.SuffixNum4XYZ.RegRewriteAlb;
import com.funshion.search.media.search.mediaTitleRewriter.RegRewriteHzSuffix;


public abstract class TitleSuffixNumPattern{
	protected static final int errorRet = -1;
	final Pattern pattern;
	public TitleSuffixNumPattern(Pattern pattern){
		this.pattern = pattern;
	}
	public abstract MediaTitleSuffixNumFormatResult rewrite(String input);
	
	static TitleSuffixNumPattern[] rPatterns = new TitleSuffixNumPattern[]{
			new RegRewriteAlb(),
			new RegRewriteHzSuffix(),
			new SuffixNum4EP(),
			new SuffixNum4Season(),
			new SuffixNum4cn_num()
	};
	public static MediaTitleSuffixNumFormatResult rewriteMediaTitle(String input){
		for(TitleSuffixNumPattern rp : rPatterns){
			MediaTitleSuffixNumFormatResult mr = rp.rewrite(input);
			if(mr != null){
				return mr;
			}
		}
		return null;
	}
	
}