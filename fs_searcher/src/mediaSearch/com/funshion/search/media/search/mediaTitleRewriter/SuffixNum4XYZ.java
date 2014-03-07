package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SuffixNum4XYZ extends TitleSuffixNumPattern{

	protected SuffixNum4XYZ(String numPattern) {
		super(Pattern.compile("(.+)第 {0,}([" + numPattern + "]+) {0,}[部季集辑]$"));
	}

	@Override
	public MediaTitleSuffixNumFormatResult rewrite(String input) {
		Matcher matcher = this.pattern.matcher(input);
		boolean matched = matcher.matches();
		if(!matched){
			return null;
		}
		String s2 = matcher.group(1);
		String number = matcher.group(2);
		int num = getNum(number);

		return MediaTitleSuffixNumFormatResult.make(input, s2, num);
	}

	public abstract int getNum(String str);

	public static class RegRewriteAlb extends SuffixNum4XYZ{

		protected RegRewriteAlb() {
			super("0123456789");
		}

		@Override
		public int getNum(String str) {
			try{
				return Integer.parseInt(str);
			}catch(Exception e){
				return errorRet;
			}
		}
	}

	
}