package com.funshion.search.media.search;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.junit.Test;

import com.funshion.search.analyzers.RMMSegmentTokenizer;
import com.funshion.search.media.search.FSMediaChineseAnalyzer;
import com.funshion.search.media.search.FieldDefine;
import com.funshion.search.media.search.FieldReuseStrategyTest;

public class FSMediaChineseAnalyzerTest {
	@Test
	public void testCreateComponents() throws IllegalArgumentException, IllegalAccessException {
		Map<String, TokenStreamComponents> map = FieldReuseStrategyTest.createComponents(new StringReader(""));
		FSMediaChineseAnalyzer ana =new FSMediaChineseAnalyzer();
		Field[] fields = FieldDefine.class.getFields();
		for(Field f : fields){
			String name = f.getName();
			System.out.println("checking " + name + " with value '" + f.get(null) + "'");
			if(name.equals("FULL_NAME_END")){
				String end = (String) f.get(null);
				assertEquals("end is not $", end, "$");
			}else if(name.contains("_CHAR_")){
				continue;
			}else{
				String str = (String) f.get(null);
				TokenStreamComponents cmp = map.get(str);
				TokenStreamComponents cmp2 = ana.createComponents(str, new StringReader(""));
				assertEquals("for " + f + ", ERROR for " + str + "(" + cmp + ")", 
						cmp == null ? RMMSegmentTokenizer.class : cmp.getTokenizer().getClass(), 
								cmp2.getTokenizer().getClass());
				
			}
		}
		ana.close();
	
	}

}
