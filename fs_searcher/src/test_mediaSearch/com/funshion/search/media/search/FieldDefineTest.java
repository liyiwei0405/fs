package com.funshion.search.media.search;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

public class FieldDefineTest {

	@Test
	public void test() throws Exception {
		Field[] fields = FieldDefine.class.getFields();
		HashSet<Character>cMap = new HashSet<Character>();
		HashSet<String>strMap = new HashSet<String>();
		for(Field f : fields){
			String name = f.getName();
			System.out.println("checking " + name + " with value '" + f.get(null) + "'");
			if(name.equals("FULL_NAME_END")){
				String end = (String) f.get(null);
				assertEquals("end is not $", end, "$");
			}else if(name.contains("_CHAR_")){
				Character c = (Character) f.get(null);
				if(cMap.contains(c)){
					throw new Exception("duplicate Character " + c);
				}else{
					cMap.add(c);
				}
			}else{
				String str = (String) f.get(null);
				if(str.length() != 1){
					throw new Exception("not 1-len ！" );
				}
				if(strMap.contains(str)){
					throw new Exception("duplicate Character " + str);
				}else{
					strMap.add(str);
				}
			}
		}
	}

}
