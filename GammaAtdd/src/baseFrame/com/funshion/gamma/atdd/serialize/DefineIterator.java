package com.funshion.gamma.atdd.serialize;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DefineIterator {
	Iterator<String>itr ;
	private String line;
	public DefineIterator(Iterator<String>itr){
		this.itr = itr;
	}
	public DefineIterator(List<String>lst){
		this(lst.iterator());
	}
	public DefineIterator(String[]lst){
		this(Arrays.asList(lst).iterator());
	}
	
	int lineNum = 0;
	public boolean hasNext(){
		if(itr.hasNext()){
			lineNum ++;
			line = itr.next();
			return true;
		}
		return false;
	}
	public String getCurrentLine(){
		return line;
	}
}
