package com.funshion.gamma.atdd.serialize.test;

import java.io.File;
import java.io.IOException;

import com.funshion.gamma.atdd.serialize.StringTokenlizer;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineReader;

public class StringTokenlizerTest {
	public static void main(String[] args) throws IOException {
		LineReader lr = new LineReader(new File("tmp/testToken.txt"));
		while(lr.hasNext()){
			String line = lr.next().trim();
			StringTokenlizer st = new StringTokenlizer(line);
			System.out.println(line + "==>");
			while(true){
				int[] arr = st.nextTokenIndex();
				if(arr == null){
					break;
				}
				System.out.print("[");
				System.out.print(line.substring(arr[0], arr[1]));
				System.out.print("] ");
			}
			System.out.println();
			Consoler.readString(":");
		}
		lr.close();
	}
}
