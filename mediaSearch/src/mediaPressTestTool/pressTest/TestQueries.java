package pressTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.retrieve.media.thrift.LimitRetrieve;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.retrieve.media.thrift.Token;
import com.funshion.search.media.chgWatcher.FieldDefine;
import com.funshion.search.utils.LineReader;

public class TestQueries {
	static ArrayList<String>qws = new ArrayList<String>();
	static int words = 0;
	static void load(File f){
		try {
			LineReader lr = new LineReader(f);
			while(lr.hasNext()){
				String ret = lr.next();
				qws.add(ret);
			}
			lr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("qws size = " + qws.size());
	}
	static RetrieveStruct getQuery() throws Exception{
		int idx = (int) (Math.random() * 9);
		String word = getWord();
//		
		if(word == null){
			return null;
		}
//		System.out.println(word);
		return getQuery(word, idx);
	}
	private static String getWord() {
		return qws.get((int) (qws.size() * Math.random()));
	}
	static RetrieveStruct getQuery(String word, int pattern) throws Exception{
		RetrieveStruct qs = new RetrieveStruct();

		qs.conditions = test4NewTactics(word, (int)(Math.random() * 10));
		qs.ver = 1;
		
		LimitRetrieve lb;
		if(pattern == 0){
			lb = new LimitRetrieve(20, 20);
		}else if(pattern == 1){
			lb = new LimitRetrieve(40, 100);
		}else if(pattern == 2){
			lb = new LimitRetrieve(1000, 20);
		}else{
			lb = new LimitRetrieve(0, 20);
		}
		qs.limits = lb;
		return qs;
	}
	
	public static List<Token> test4NewTactics(String word, int mcids) throws Exception{

		//条件: isPlay = 1 
		final PortalItemCondition isPlay = new PortalItemCondition(FieldDefine.FIELD_NAME_ISPLAY,
				OperType.EQUAL, "1");
		//条件: 搜索词完全匹配某标题
		final PortalItemCondition fullMatch = new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
				OperType.SEARCH_TITLE_FULL, word);
		//条件: 关键词匹配
		final PortalItemCondition segMatch = new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
				OperType.SEARCH_TITLE, word);

		PortalCompoundCondition ver0Query = new PortalCompoundCondition();
		ver0Query.addCondition(ConjunctType.OR, fullMatch);
		ver0Query.addCondition(ConjunctType.OR, segMatch);
	

		final PortalItemCondition taMediaClassId_2 = new PortalItemCondition(FieldDefine.FIELD_NAME_MEDIA_CLASSID,
				OperType.EQUAL, "" + mcids);
		final PortalItemCondition taAreaTactis_2 = new PortalItemCondition(FieldDefine.FIELD_NAME_AREA_TACTIC,
				OperType.EQUAL, "pc" + mcids);

		//（关键词匹配 && (isplay == 1  && ta_x match) || has-videolet))|| fullMatch
		//条件: 有小视频
		final PortalItemCondition hasVideolet = new PortalItemCondition(FieldDefine.FIELD_NAME_HAS_VIDEOLET,
				OperType.EQUAL, "1");

		//构造 thrift 接口结构体
		RetrieveStruct rs  = new RetrieveStruct();
		//指定接口版本号
		rs.ver = 1;

		//构造复合查询  isplay == 1  && ta_x match
		PortalCompoundCondition canPlay = new PortalCompoundCondition();
		
		PortalCompoundCondition notTactic = new PortalCompoundCondition();
		notTactic.addCondition(ConjunctType.NOT, taAreaTactis_2);
		
		canPlay.addCondition(ConjunctType.AND, taMediaClassId_2);
		canPlay.addCondition(ConjunctType.AND, isPlay);
//		canPlay.addCondition(ConjunctType.NOT, taAreaTactis_2);
		
		PortalCompoundCondition canPlayOrHasVideolet = new PortalCompoundCondition();
		canPlayOrHasVideolet.addCondition(ConjunctType.OR, canPlay);
		canPlayOrHasVideolet.addCondition(ConjunctType.OR, hasVideolet);

		PortalCompoundCondition ftAndCanPlayOrHasVideolet = new PortalCompoundCondition();
		ftAndCanPlayOrHasVideolet.addCondition(ConjunctType.AND, segMatch);
		ftAndCanPlayOrHasVideolet.addCondition(ConjunctType.AND, canPlayOrHasVideolet);

		PortalCompoundCondition ver1Query = new PortalCompoundCondition();
		//指定 has-videolet条件
		ver1Query.addCondition(ConjunctType.OR, ftAndCanPlayOrHasVideolet);
		ver1Query.addCondition(ConjunctType.OR, fullMatch);		
		//构造查询主体

		//将查询条件赋值给thrift接口
//		System.out.println((++xdx) + "\t" + ver1Query);
		return ver1Query.toTokenList();
		
	}
	static int xdx = 0;
}
