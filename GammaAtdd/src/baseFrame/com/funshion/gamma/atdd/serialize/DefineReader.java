package com.funshion.gamma.atdd.serialize;


public class DefineReader{
	public static boolean validLile(String line){
		if(line == null)
			return false;
		line = line.trim();
		if(line.length() == 0)
			return false;
		char c = line.charAt(0);
		if(c == '#' || c =='@' || c == '＠'){
			return false;
		}
		return true;
	}
	final DefineIterator itr;
	String crtLine = null;
	int lineNum;
	DefineReader(DefineIterator itr){
		this.itr = itr;
		this.crtLine = itr.getCurrentLine().trim();
	}
	boolean hasNext(){
		while(itr.hasNext()){
			lineNum ++;
			String line = itr.getCurrentLine();
			if(!validLile(line))
				continue;
			this.crtLine = line.trim();
			return true;
		}
		return false;
	}
	String next(){
		return this.crtLine;
	}
	int currentNum(){
		return this.lineNum;
	}
	public String lineInfo() {
		return "@line " + lineNum + ", '" + crtLine + "'";
	}
}