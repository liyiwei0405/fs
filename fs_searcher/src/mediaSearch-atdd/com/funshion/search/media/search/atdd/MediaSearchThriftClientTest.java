package com.funshion.search.media.search.atdd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.retrieve.media.thrift.LimitRetrieve;
import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.MediaRetrieveResultRecord;
import com.funshion.retrieve.media.thrift.MedieRetrieveResultItem;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.search.media.search.FieldDefine;
import com.funshion.search.utils.Consoler;

public class MediaSearchThriftClientTest extends ATDDChecker{
	final String host;
	final int timeout = 5000;//ms
	double maxQueryTime = 5000;
	boolean showDetail = false;
	MediaSearchThriftClientTest(String testHost){
		host  = testHost;
	}
	public MediaRetrieveResult test(RetrieveStruct qs) throws Exception { 
		TTransport socket = null;
		try {
			long stTime1 = System.currentTimeMillis();
			socket = new TSocket(host, 3537, timeout); 
			long stTime2 = System.currentTimeMillis();
			socket.open(); 
			long stTime3 = System.currentTimeMillis();
			TFramedTransport trans = new TFramedTransport(socket);
			TProtocol protocol = new TBinaryProtocol(trans); 
			long stTime4 = System.currentTimeMillis();
			MediaSearchThriftClient client = new MediaSearchThriftClient(protocol); 
			long stTime5 = System.currentTimeMillis();
			MediaRetrieveResult result = client.retrive1(qs);
			long stTime6 = System.currentTimeMillis();
			System.out.println(
					String.format(
							"\n conn:%s\topen:%s\ttrans:%s\tprotocol:%s\tquery:%s\t(%s)\tfor %s+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",
							stTime2 - stTime1, 
							stTime3 - stTime2, 
							stTime4 - stTime3, 
							stTime5 - stTime4, 
							stTime6 - stTime5,
							stTime6 - stTime1,
							qs.toString()
							));
			System.out.print(result.retCode + "\t" + result.retMsg);
			System.out.print("\t use ms:" + ((int)(10000 * result.usedTime)) / 10000.0);
			System.out.println("\t totalmatch: " + result.total);
			if(result.ids != null && showDetail){
				System.out.println();
				int r = 0;
				for(MediaRetrieveResultRecord vr : result.ids){
					System.out.flush();
					System.err.flush();
					System.out.print("\n【" + ++ r + "】");
					for(MedieRetrieveResultItem mri: vr.items){

						System.out.print(mri.field);
						System.out.print("=" + mri.value.trim().replace('\n', ' '));
						System.out.print("; ");
					}
				}
				System.out.println();
				System.err.println();
			}

			System.out.flush();
			System.err.flush();

			return result;
		}finally{
			if(socket != null){
				socket.close();
			}
		}
	} 

	public MediaRetrieveResult testWeight(String word) throws Exception{
		throw new Exception("not implements yet");
	}
	public MediaRetrieveResult testRawQuery(String word) throws Exception{
		PortalCompoundCondition qs = newQueryStruct(word, ConjunctType.AND);
		LimitRetrieve lb = new LimitRetrieve(0, 20);
		RetrieveStruct rs = new RetrieveStruct();
		rs.conditions = qs.toTokenList();
		rs.limits = lb;
		rs.ver = 1;
		return test(rs);
	}
	public PortalCompoundCondition newQueryStruct(String word, ConjunctType cType) throws Exception{
		//		RetrieveStruct qs = new RetrieveStruct();
		//		PortalCompoundCondition cond = new PortalCompoundCondition();
		//		PortalCompoundCondition condCanPlay = new PortalCompoundCondition();

		PortalItemCondition matchCondition = new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
				OperType.SEARCH_TITLE,
				word);

		//		PortalItemCondition fullMatchCondition = new PortalItemCondition(OperType.SEARCH_TITLE_FULL,
		//				FieldDefine.FIELD_NAME_NAMES,
		//				word);
		//		PortalItemCondition isDisplayCondition = new PortalItemCondition(OperType.EQUAL,
		//				FieldDefine.FIELD_NAME_ISPLAY,
		//				"1");
		//
		//		PortalItemCondition hasVideoletCondition = new PortalItemCondition(OperType.EQUAL,
		//				FieldDefine.FIELD_NAME_HAS_VIDEOLET,
		//				"1");
		//		PortalItemCondition taCondition = new PortalItemCondition(OperType.EQUAL,
		//				FieldDefine.FIELD_NAME_TA_0_9,
		//				"0");

		//		condCanPlay.addCondition(ConnjunctType.AND, matchCondition);
		//		condCanPlay.addCondition(ConnjunctType.AND, taCondition);
		//		condCanPlay.addCondition(ConnjunctType.AND, isDisplayCondition);
		//		
		PortalCompoundCondition condMain = new PortalCompoundCondition();

		//		condMain.addCondition(ConnjunctType.OR, condCanPlay);
		//		condMain.addCondition(ConnjunctType.OR, hasVideoletCondition);
		//		condMain.addCondition(ConnjunctType.OR, fullMatchCondition);
		{
			condMain.addCondition(cType, matchCondition);
			return condMain;
		}
	}
	public void basicCheck(MediaRetrieveResult result) throws Exception{
		is(result.retCode == 200, "retCode check fail! %s, retMsg '%s'", result.retCode, result.retMsg);
		is(result.usedTime < this.maxQueryTime, "too long usedtime %s, limit is %s", result.usedTime, this.maxQueryTime);
	}
	public void testLimit(String word) throws Exception{
		RetrieveStruct rs  = new RetrieveStruct();
		rs.ver = 1;
		PortalCompoundCondition qs = newQueryStruct(word, ConjunctType.AND);
		rs.conditions = qs.toTokenList();

		LimitRetrieve lb = new LimitRetrieve(0, 20);
		rs.limits = lb;

		MediaRetrieveResult res = test(rs);
		basicCheck(res);
		int min = 6;
		is(res.total > min, "query result should not less than %s, but get %s", min, res.total);
		int offset = res.total - min;
		int limit = min + 10;
		//expect min result hit
		rs.limits = new LimitRetrieve(offset, limit);
		MediaRetrieveResult res2 = test(rs);
		basicCheck(res2);
		is(res2.ids.size() == min, "expect %s, but get %s hits! total = %s(last is %s), offset %s, limit %s",
				min, res2.ids.size(), res2.total, res.total, offset, limit);
		info("limit check ok!");
	}

	protected String getValue(MediaRetrieveResultRecord rec, String name){
		for(MedieRetrieveResultItem itm : rec.items){
			if(itm.getField().endsWith(name)){
				if(itm.getValue().equals("49115") || itm.getValue().equals("15855")){
					System.out.println(rec);
				}
				return itm.getValue();
			}
		}
		return null;
	}
	private int getCount(String word) throws Exception{
		RetrieveStruct rs  = new RetrieveStruct();
		rs.ver = 1;
		PortalCompoundCondition qs = newQueryStruct(word, ConjunctType.AND);
		rs.conditions = qs.toTokenList();

		MediaRetrieveResult mres0 = test(rs);
		this.basicCheck(mres0);
		return mres0.total;
	}
	public void testUpcse2LowerCase() throws Exception{
		String words[] = new String[]{
				"LOVE",
				"love",
				"Love",
				"LoVe",
				"LOvE"
		};
		int count[] = new int[words.length];
		for(int x = 0; x < words.length; x ++){
			count[x] = getCount(words[x]);
			is(count[x] > 0, "error! why %s's result is %s?", words[x], count[x]);
			if(x > 0){
				is(count[x - 1] == count[x], "misMatch count! %s(%s), %s(%s)",
						words[x], count[x], words[x - 1], count[x - 1]);
			}
		}
		info("testUpcse2LowerCase passed!");
	}
	public void testFliter(String word) throws Exception{
		RetrieveStruct rs  = new RetrieveStruct();
		rs.ver = 1;
		PortalCompoundCondition qs = newQueryStruct(word, ConjunctType.AND);
		rs.conditions = qs.toTokenList();

		LimitRetrieve lb = new LimitRetrieve(0, 10);
		rs.limits = lb;
		MediaRetrieveResult mres0 = test(rs);
		this.basicCheck(mres0);
		int minCount = 100;

		is(mres0.total > minCount, "query result should not less than %s, but get %s", minCount, mres0.total);


	}
	public void test4NewTactics(String word, int tac) throws Exception{

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
		RetrieveStruct rs0  = new RetrieveStruct();
		//指定接口版本号
		rs0.ver = 1;
		rs0.conditions = ver0Query.toTokenList();
		rs0.limits = new LimitRetrieve(0, 1000000);

		//RPC调用
		//MediaRetrieveResult res = client.retrive1(qs)
		final MediaRetrieveResult mres0 = test(rs0);


	
//		final PortalItemCondition taMediaClassId_1 = new PortalItemCondition(FieldDefine.FIELD_NAME_MEDIA_CLASSID,
//				OperType.EQUAL, "1");

		final PortalItemCondition taMediaClassId_2 = new PortalItemCondition(FieldDefine.FIELD_NAME_MEDIA_CLASSID,
				OperType.EQUAL, "2");

//		final PortalItemCondition taArea_0 = new PortalItemCondition(FieldDefine.FIELD_NAME_AREA_TACTIC,
//				OperType.EQUAL, "0");
//
//		final PortalItemCondition taArea_1 = new PortalItemCondition(FieldDefine.FIELD_NAME_AREA_TACTIC,
//				OperType.EQUAL, "1");
//
//		final PortalItemCondition taArea_2 = new PortalItemCondition(FieldDefine.FIELD_NAME_AREA_TACTIC,
//				OperType.EQUAL, "2");
		
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
		canPlay.addCondition(ConjunctType.AND, isPlay);
		canPlay.addCondition(ConjunctType.AND, taMediaClassId_2);

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
		rs.conditions = ver1Query.toTokenList();
		//指定结果集读取范围(相当于sql limit 15, 25)
		LimitRetrieve lb = new LimitRetrieve(0, 1000000);
		rs.limits = lb;

		//RPC调用
		//MediaRetrieveResult res = client.retrive1(qs)
		final MediaRetrieveResult mres1 = test(rs);
		this.basicCheck(mres1);

		HashSet<MediaRetrieveResultRecord> diff = diff(mres0, mres1);
		checkValid(diff, tac);
		
	}
	
	public void testFliter4NewTactics(String word, int tac) throws Exception{

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
		RetrieveStruct rs0  = new RetrieveStruct();
		//指定接口版本号
		rs0.ver = 1;
		rs0.conditions = ver0Query.toTokenList();
		rs0.limits = new LimitRetrieve(0, 1000000);

		//RPC调用
		//MediaRetrieveResult res = client.retrive1(qs)
		final MediaRetrieveResult mres0 = test(rs0);


		//（关键词匹配 && (isplay == 1  && ta_x match) || has-videolet))|| fullMatch
		//条件: 有小视频
		final PortalItemCondition hasVideolet = new PortalItemCondition(FieldDefine.FIELD_NAME_HAS_VIDEOLET,
				OperType.EQUAL, "1");

		//条件: ta_x != 1 (系统内部将其转化为字段 FIELD_NAME_TA_0_9 中 <b>存在</b> ta_x, 此处x=2)
		final PortalItemCondition taMatch_0 = new PortalItemCondition(FieldDefine.FIELD_NAME_TA_0_9,
				OperType.EQUAL, tac + "");

		//构造 thrift 接口结构体
		RetrieveStruct rs  = new RetrieveStruct();
		//指定接口版本号
		rs.ver = 1;

		//构造复合查询  isplay == 1  && ta_x match
		PortalCompoundCondition canPlay = new PortalCompoundCondition();
		canPlay.addCondition(ConjunctType.AND, isPlay);
		canPlay.addCondition(ConjunctType.AND, taMatch_0);

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
		rs.conditions = ver1Query.toTokenList();
		//指定结果集读取范围(相当于sql limit 15, 25)
		LimitRetrieve lb = new LimitRetrieve(0, 1000000);
		rs.limits = lb;

		//RPC调用
		//MediaRetrieveResult res = client.retrive1(qs)
		final MediaRetrieveResult mres1 = test(rs);
		this.basicCheck(mres1);

		HashSet<MediaRetrieveResultRecord> diff = diff(mres0, mres1);
		checkValid(diff, tac);
		info("check filter valid ok");
	}

	String value(MediaRetrieveResultRecord rec, char field){
		for(MedieRetrieveResultItem itm : rec.items){
			if(itm.field.charAt(0) == field){
				return itm.value;
			}
		}
		throw new RuntimeException("unknown field " + field + " for record " + rec);
	}
	private void checkValid(MediaRetrieveResultRecord rec, int tac) throws Exception{
		boolean hasVideolet = true, canPlay = false;
		if("1".equals(value(rec, FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET))){
			hasVideolet = true;
		}else{
			hasVideolet = false;
		}
		if("1".equals(value(rec, FieldDefine.FIELD_NAME_CHAR_ISPLAY))){
			String tacs = value(rec, FieldDefine.FIELD_NAME_CHAR_TA_0_9);
			String[]tacss = tacs.split("\t");
			for(String x : tacss){
				if(x.length() == 0){
					continue;
				}
				int t = Integer.parseInt(x);
				if(t == tac){
					canPlay = true;
					break;
				}
			}
		}
		boolean include = hasVideolet || canPlay;
		is(!include, "should not filter out! hasVideolet %s isplay %s, ta_x %S, for rec %s",
				value(rec, FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET), 
				value(rec, FieldDefine.FIELD_NAME_CHAR_ISPLAY), 
				value(rec, FieldDefine.FIELD_NAME_CHAR_TA_0_9)
				,rec);
//		info("Pass check! hasVideolet %s isplay %s, ta_x %S, for rec %s",
//				value(rec, FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET), 
//				value(rec, FieldDefine.FIELD_NAME_CHAR_ISPLAY), 
//				value(rec, FieldDefine.FIELD_NAME_CHAR_TA_0_9)
//				,rec);
	}
	private void checkValid(HashSet<MediaRetrieveResultRecord>set, int tac) throws Exception{
		Iterator<MediaRetrieveResultRecord> itr = set.iterator();
		while(itr.hasNext()){
			MediaRetrieveResultRecord rec = itr.next();
			checkValid(rec, tac);
		}
	}
	private HashSet<MediaRetrieveResultRecord> diff(MediaRetrieveResult mres0,
			MediaRetrieveResult mres1) throws Exception {
		HashMap<Integer, MediaRetrieveResultRecord>map0 = new HashMap<Integer, MediaRetrieveResultRecord>();
		HashMap<Integer, MediaRetrieveResultRecord>map1 = new HashMap<Integer, MediaRetrieveResultRecord>();

		for(MediaRetrieveResultRecord rec : mres0.ids){
			int unicId = Integer.parseInt(value(rec, FieldDefine.FIELD_NAME_CHAR_UNIC_ID));
			is(!map0.containsKey(unicId), "already has id %s, for record %s", unicId, rec);
			map0.put(unicId, rec);
		}
		for(MediaRetrieveResultRecord rec : mres1.ids){
			int unicId =Integer.parseInt(value(rec, FieldDefine.FIELD_NAME_CHAR_UNIC_ID));
			is(map0.containsKey(unicId), "NOT has id %s in mres0, for record %s", unicId, rec);

			is(!map1.containsKey(unicId), "already has id %s, for record %s", unicId, rec);
			map1.put(unicId, rec);
		}
		info("map size %s:%s", map0.size(), map1.size());

		HashSet<MediaRetrieveResultRecord>fltOut = new HashSet<MediaRetrieveResultRecord>();
		for(int x : map0.keySet()){
			if(map1.containsKey(x)){
				continue;
			}
			fltOut.add(map0.get(x));
		}
		info("diff size %s", fltOut.size());
		return fltOut;
	}
	public static void main(String[]args) throws Exception{

		String host;
		if(args.length > 0){
			host = args[0];
		}else{
			host = Consoler.readString("connectHost:");
		}
		System.out.println("test host:" + host);
		MediaSearchThriftClientTest ttc = new MediaSearchThriftClientTest(host);
		ttc.test();
	}
	@Override
	public void test() throws Exception {

		String word = "的";
		MediaRetrieveResult mi = testRawQuery(word);
		info("testing connection and word %s, timeout=%s", word, timeout);
		this.basicCheck(mi);
		is(mi.total > 5000, "too small search result for %s, count is %s", word, mi.total);
		info("OK testing connection and word %s, count %s, usedtime %sms", word, mi.total, mi.usedTime);

		info("test limit....");
		word = "爱情";
		this.testLimit(word);

		info("test filters....");
		word = "A";
		this.testFliter(word);

		info("test testUpcse2LowerCase....");
		testUpcse2LowerCase();

		info("test testFliter4NewTactics....");
		word = "大话西游降魔篇第二季";
		this.testFliter4NewTactics(word, 2);


	}
}