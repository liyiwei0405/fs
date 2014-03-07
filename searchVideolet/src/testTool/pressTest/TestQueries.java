package pressTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.funshion.search.utils.LineReader;
import com.funshion.search.videolet.search.VideoletIndexer;
import com.funshion.search.videolet.thrift.FieldWeight;
import com.funshion.search.videolet.thrift.LimitBy;
import com.funshion.search.videolet.thrift.QueryStruct;
import com.funshion.search.videolet.thrift.SortBy;

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
			LineReader lr = new LineReader("/parsed-ZheJiang_HangZhou_CTC_LOG_27");
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
		if(pattern == 0){
			LimitBy lb = new LimitBy(0, 21);
			qs.word = word;
			qs.searchModel = 1;
			qs.limits = lb;
			return qs;
		}else if(pattern == 1){
			LimitBy lb = new LimitBy(0, 21);
			lb = new LimitBy(20, 20);
			qs.word = word;
			qs.searchModel = 1;
			qs.limits = lb;
		}else if(pattern == 2){
			LimitBy lb = new LimitBy(0, 21);
			lb = new LimitBy(5000, 20);
			qs.word = word;
			qs.searchModel = 1;
			qs.limits = lb;
		}else if(pattern == 3){
			List<SortBy>srtLst = new ArrayList<SortBy>();
			SortBy byWeight = new SortBy( "@r", false);
			SortBy byModifyDate = new SortBy(VideoletIndexer.INDEX_MODIFY_DATE, false);
			SortBy byPlayNum = new SortBy(VideoletIndexer.INDEX_PLAY_NUM, false);

			qs.word = word;
			qs.searchModel = 1;
			srtLst.clear();
			srtLst.add(byWeight);
			srtLst.add(byModifyDate);
			srtLst.add(byPlayNum);
			qs.sortFields = srtLst;

			return qs;
		}else if(pattern == 4){
			List<SortBy>srtLst = new ArrayList<SortBy>();
			SortBy byWeight = new SortBy( "@r", false);
			SortBy byModifyDate = new SortBy(VideoletIndexer.INDEX_MODIFY_DATE, false);
			SortBy byPlayNum = new SortBy(VideoletIndexer.INDEX_PLAY_NUM, false);

			qs.word = word;
			qs.searchModel = 1;
		

			srtLst.clear();
			srtLst.add(byModifyDate);
			srtLst.add(byWeight);
			srtLst.add(byPlayNum);
			qs.sortFields = srtLst;
			return qs;
		}else if(pattern == 5){
			List<SortBy>srtLst = new ArrayList<SortBy>();
			SortBy byWeight = new SortBy( "@r", false);
			SortBy byModifyDate = new SortBy(VideoletIndexer.INDEX_MODIFY_DATE, false);
			SortBy byPlayNum = new SortBy(VideoletIndexer.INDEX_PLAY_NUM, false);

			qs.word = word;
			qs.searchModel = 1;

			srtLst.clear();
			srtLst.add(byPlayNum);
			srtLst.add(byModifyDate);
			srtLst.add(byWeight);
			return qs;
		}else if(pattern == 6){
			List<FieldWeight>weightLst = new ArrayList<FieldWeight>();
			FieldWeight titleHigh = new FieldWeight(20, VideoletIndexer.INDEX_TITLE);
			FieldWeight tagHigh = new FieldWeight(20, VideoletIndexer.INDEX_TAGS);
			
			weightLst.add(tagHigh);
			weightLst.add(titleHigh);
			qs.word = word;
			return qs;


		}else if(pattern == 7){
			List<FieldWeight>weightLst = new ArrayList<FieldWeight>();
			FieldWeight titleHigh = new FieldWeight(20,  VideoletIndexer.INDEX_TITLE);

			qs.word = word;
			qs.searchModel = 1;

			weightLst.clear();
			weightLst.add(titleHigh);
			qs.weights = weightLst;

			return qs;
		}else if(pattern == 8){
			List<FieldWeight>weightLst = new ArrayList<FieldWeight>();
			FieldWeight tagHigh = new FieldWeight(20,  VideoletIndexer.INDEX_TAGS);

			qs.word = word;
			qs.searchModel = 1;
			
			weightLst.clear();
			weightLst.add(tagHigh);
			qs.weights = weightLst;
			return qs;
		}else{
			qs.word = word;
			return qs;
		}
		return qs;
	}
}
