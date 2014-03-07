package pressTest;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.funshion.search.media.search.FieldDefine;
import com.funshion.search.media.thrift.FieldFilter;
import com.funshion.search.media.thrift.FullTextQuery;
import com.funshion.search.media.thrift.LimitBy;
import com.funshion.search.media.thrift.QueryStruct;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineReader;

public class TestQueries {
	static class WD{
		int idx;
		String word;
		int hash = 0;
		WD(String word, int idx){
			this.idx = idx;
			this.word = word;
		}
		public int hashcode(){
			if(hash == 0){
				hash = ((word + "-" + idx + "at").hashCode()) + idx+ "jsajjfsa".hashCode();
			}
			return hash;
		}
	}
	static LinkedList<WD>qws = new LinkedList<WD>();
	static int words = 0;
	static synchronized String getWord(){
		if(qws.size() == 0){
			return null;
		}
		words ++;
		int rand = 0;
		WD ret = qws.get(rand);
		qws.remove(rand);
		return ret.word;
	}
	static{
		try {
			String filePath = Consoler.readString("query words file:");
			LineReader lr = new LineReader(filePath);
			int idx = 0;
			while(lr.hasNext()){
				String ret = lr.next();
				qws.add(new WD(ret, ++idx));
			}
			lr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(qws, new Comparator<WD>(){

			@Override
			public int compare(WD o1, WD o2) {
				return o1.hashcode() - o2.hashcode();
			}
			
		});
	}
	static QueryStruct getQuery(){
		int idx = (int) (Math.random() * 9);
		String word = getWord();
		System.out.println(word);
		if(word == null){
			return null;
		}
		return getQuery(word, idx);
	}
	static QueryStruct getQuery(String word, int pattern){
		QueryStruct qs = new QueryStruct();
		qs.word = new FullTextQuery();
		qs.ver = 1;
		if(pattern == 0){
			LimitBy lb = new LimitBy(0, 21);
			qs.word.word = word;
			qs.limits = lb;
			return qs;
		}else if(pattern == 1){
			LimitBy lb = new LimitBy(0, 21);
			lb = new LimitBy(20, 20);
			qs.word.word = word;
			qs.limits = lb;
		}else if(pattern == 2){
			LimitBy lb = new LimitBy(0, 21);
			lb = new LimitBy(5000, 20);
			qs.word.word = word;
			qs.limits = lb;
		}else{
			LimitBy lb = new LimitBy(0, 21);
			lb = new LimitBy(20, 20);
			qs.word.word = word;
			qs.limits = lb;
			FieldFilter ff = new FieldFilter(FieldDefine.FIELD_NAME_CAN_DISPLAY, 1, false);
			qs.addToFilters(ff);
			int rand = (int) (Math.random() * 10);
			FieldFilter ffTa = new FieldFilter("" + ( rand + FieldDefine.FIELD_NAME_CHAR_TA_0), 1, false);
			qs.addToFilters(ffTa);
		}
		return qs;
	}
}
