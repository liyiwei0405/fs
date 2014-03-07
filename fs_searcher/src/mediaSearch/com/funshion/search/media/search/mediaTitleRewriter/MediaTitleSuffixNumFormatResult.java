package com.funshion.search.media.search.mediaTitleRewriter;

import com.funshion.search.analyzers.CharacterFormat;

public class MediaTitleSuffixNumFormatResult{
	private String newNameCn = null;
	private int num = -1;
	public final String nameCnOrg;
	public MediaTitleSuffixNumFormatResult(String org){
		this.nameCnOrg = org;
	}
	
	public String getNewNameCn() {
		return newNameCn;
	}
	
	public int getNum() {
		return num;
	}

	public void setNewNameCn(String newNameCn) {
		this.newNameCn = newNameCn.trim();
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		if(!(o instanceof MediaTitleSuffixNumFormatResult)){
			return false;
		}
		MediaTitleSuffixNumFormatResult ot = (MediaTitleSuffixNumFormatResult) o;
		return this.newNameCn.equals(ot.newNameCn) && this.num == ot.num;
	}
	public String toString(){
		return "newNameCn:'" + newNameCn + "', num = " + num + ", from org='"+ nameCnOrg + "'";
	}
	public static MediaTitleSuffixNumFormatResult make(String org, String newNameCn, int num){
		if(num < 1){
			System.err.println("error num " + num + ", for MediaTitleResult org " + org);
			return null;
		}
		if(newNameCn == null){
			System.err.println("error! null newNameCn, for MediaTitleResult org " + org);
			return null;
		}
		MediaTitleSuffixNumFormatResult mr = new MediaTitleSuffixNumFormatResult(org);
		mr.setNewNameCn(newNameCn);
		mr.setNum(num);
		return mr;
	}

	public String norm() {
		return newNameCn.trim() + " " + num;
	}
	public static MediaTitleSuffixNumFormatResult rewriteTitle(String title){
		String fltTitle = CharacterFormat.rewriteString(title);
		return TitleSuffixNumPattern.rewriteMediaTitle(fltTitle);
	}
}