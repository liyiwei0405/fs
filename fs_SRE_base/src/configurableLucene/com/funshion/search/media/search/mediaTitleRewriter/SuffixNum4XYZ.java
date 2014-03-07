package com.funshion.search.media.search.mediaTitleRewriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SuffixNum4XYZ extends TitleSuffixNumPattern{
	final Pattern perfixPattern;
	protected SuffixNum4XYZ(String numPattern) {
		super(Pattern.compile("(.+)第[ \t]{0,}([" + numPattern + "]+)[ \t]{0,}[部季集辑]$"));
		perfixPattern = Pattern.compile("^第[ \t]{0,}([" + numPattern + "]+)[ \t]{0,}[部季集辑](.+)$");
	}

	@Override
	public MediaTitleSuffixNumFormatResult rewrite(String input) {
		
		String shortName;
		String number;
		int num;
		
		Matcher matcher = this.pattern.matcher(input);
		boolean matched = matcher.matches();
		if(matched){
			shortName = matcher.group(1);
			number = matcher.group(2);
			num = getNum(number);
		}else{
			matcher = this.perfixPattern.matcher(input);
			matched = matcher.matches();
			if(matched){
				shortName = matcher.group(2);
				number = matcher.group(1);
				num = getNum(number);
			}else{
				return null;
			}
		}

		return MediaTitleSuffixNumFormatResult.make(input, shortName, num);
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