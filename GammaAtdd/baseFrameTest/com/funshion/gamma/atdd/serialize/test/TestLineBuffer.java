package com.funshion.gamma.atdd.serialize.test;

import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.serialize.DefineIterator;
import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;
import com.funshion.gamma.atdd.serialize.ThriftDeserializeTool;
import com.funshion.gamma.atdd.serialize.shell.LineBuffer;

public class TestLineBuffer {
	public static void main(String[] args) throws Exception {
		ArrayList<String>lst = LineBuffer.read();
		for(String line : lst){
			System.out.println(line);
		}
		System.out.println("");
		DefineIterator itr = new DefineIterator(lst.iterator());
		List<DeserializedFieldInfo> fis = ThriftDeserializeTool.deserializeObjects(itr);
		
		for(DeserializedFieldInfo fi : fis){
			System.out.println(fi);
			
			System.out.println("-——————————————————————————————————————————————————");
		}
	}
	
}
