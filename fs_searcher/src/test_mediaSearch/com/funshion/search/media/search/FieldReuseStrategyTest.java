package com.funshion.search.media.search;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.junit.Test;

import com.funshion.search.analyzers.RMMSegmentTokenizer;

public class FieldReuseStrategyTest {
	public static Map<String, TokenStreamComponents> createComponents(Reader reader) {
		Map<String, TokenStreamComponents>map = new HashMap<String, TokenStreamComponents>();
		Tokenizer tokenizer;
		tokenizer = new SplitTokenizer(reader);
		TokenStreamComponents SplitTokenizerComp =  new TokenStreamComponents(tokenizer, tokenizer);

		map.put(FieldDefine.FIELD_NAME_IS_HD, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_ISPLAY, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_RELATED_PREIDS, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_DISPLAY_TYPE, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_COUNTRY, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_TAG_4_EDITOR, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_RELEASE_INFO, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_MEDIA_CLASSID, SplitTokenizerComp);
		map.put(FieldDefine.FIELD_NAME_AREA_TACTIC, SplitTokenizerComp);
		
		tokenizer = new MultiMediaNamePayloadTokenizer(reader);
		TokenStreamComponents MultiMediaNamePayloadTokenizerComp =  new TokenStreamComponents(tokenizer, tokenizer);
		map.put(FieldDefine.FIELD_NAME_NAMES, MultiMediaNamePayloadTokenizerComp);

		tokenizer = new MediaNamePayloadTokenizer(reader, 0);
		TokenStreamComponents mediaNamePayloadTokenizer0 =  new TokenStreamComponents(tokenizer, tokenizer);
		map.put(FieldDefine.FIELD_NAME_NAME_CN, mediaNamePayloadTokenizer0);

		tokenizer = new MediaNamePayloadTokenizer(reader, 1);
		TokenStreamComponents mediaNamePayloadTokenizer1 =  new TokenStreamComponents(tokenizer, tokenizer);
		map.put(FieldDefine.FIELD_NAME_NAME_EN, mediaNamePayloadTokenizer1);

		tokenizer = new MediaNamePayloadTokenizer(reader, 2);
		TokenStreamComponents mediaNamePayloadTokenizer2 =  new TokenStreamComponents(tokenizer, tokenizer);
		map.put(FieldDefine.FIELD_NAME_NAME_OT, mediaNamePayloadTokenizer2);

		tokenizer = new MediaNamePayloadTokenizer(reader, 3);
		TokenStreamComponents mediaNamePayloadTokenizer3 =  new TokenStreamComponents(tokenizer, tokenizer);
		map.put(FieldDefine.FIELD_NAME_NAME_SN, mediaNamePayloadTokenizer3);

//		tokenizer = new MultiSpanMediaNamePayloadTokenizer(reader);
//		TokenStreamComponents multiSpanMediaNamePayloadTokenizer = new TokenStreamComponents(tokenizer, tokenizer);
//		map.put(FieldDefine.FIELD_NAME_PHRAGEQUERY, multiSpanMediaNamePayloadTokenizer);

		tokenizer = new RMMSegmentTokenizer(reader);
		TokenStreamComponents dft =  new TokenStreamComponents(tokenizer, tokenizer);
		map.put(null, dft);

		return map;
	}
	@Test
	public void testGetReusableComponents() {
		StringReader sr = new StringReader("");
		FieldReuseStrategy strat = new FieldReuseStrategy();
		Map<String, TokenStreamComponents> map = createComponents(sr);
		Iterator<Entry<String, TokenStreamComponents>>itr = map.entrySet().iterator();
		while(itr.hasNext()){
			Entry<String, TokenStreamComponents> e = itr.next();
			if(e.getKey() == null){
				strat.setReusableComponents("！", e.getValue());
			}else{
				strat.setReusableComponents(e.getKey(), e.getValue());
			}
		}
		TokenStreamComponents cmp = strat.getReusableComponents("?");
		assertTrue("get null expect default", cmp != null && (cmp.getTokenizer() instanceof RMMSegmentTokenizer));
		
		itr = map.entrySet().iterator();
		while(itr.hasNext()){
			Entry<String, TokenStreamComponents> e = itr.next();
			if(e.getKey() == null){
				continue;
			}else{
				assertSame("fail for " + e.getKey() + ", cmp = " + e.getValue(), strat.getReusableComponents(e.getKey()), e.getValue());
			}
		}
		strat.close();
	}

}
