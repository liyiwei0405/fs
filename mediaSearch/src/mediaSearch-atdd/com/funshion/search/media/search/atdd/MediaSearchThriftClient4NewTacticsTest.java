package com.funshion.search.media.search.atdd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.funshion.search.media.chgWatcher.FieldDefine;
import com.funshion.search.utils.Consoler;

public class MediaSearchThriftClient4NewTacticsTest extends ATDDChecker{
	final String host;
	final int timeout = 5000;//ms
	double maxQueryTime = 5000;
	boolean showDetail = false;
	MediaSearchThriftClient4NewTacticsTest(String testHost){
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

	public void basicCheck(MediaRetrieveResult result) throws Exception{
		is(result.retCode == 200, "retCode check fail! %s, retMsg '%s'", result.retCode, result.retMsg);
		is(result.usedTime < this.maxQueryTime, "too long usedtime %s, limit is %s", result.usedTime, this.maxQueryTime);
	}

	public void test4NewTactics(String word, int mcids) throws Exception{

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
		rs.conditions = ver1Query.toTokenList();
		//指定结果集读取范围(相当于sql limit 15, 25)
		LimitRetrieve lb = new LimitRetrieve(0, 1000000);
		rs.limits = lb;
		//RPC调用
		//MediaRetrieveResult res = client.retrive1(qs)
		final MediaRetrieveResult mres1 = test(rs);
		this.basicCheck(mres1);

		info("checking mres1 valid...");
		List<MediaRetrieveResultRecord>lst2 = mres1.ids;
		Set<MediaRetrieveResultRecord> set = new HashSet<MediaRetrieveResultRecord>();
		set.addAll(lst2);
		checkValid(set, mcids, "pc"+mcids);
		
		for(int x = 0; x < 10 && x < mres1.total; x ++){
			info("sample id " + this.value(mres1.ids.get(x), FieldDefine.FIELD_NAME_UNIC_ID.charAt(0)));
			info("sample id " + mres1.ids.get(x));
		}
		HashSet<MediaRetrieveResultRecord> diff = diff(mres0, mres1);
		checkNotValid(diff, mcids);
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
	private void checkNotValid(MediaRetrieveResultRecord rec, int tac) throws Exception{
		boolean hasVideolet = true, canPlay = false;
		if("1".equals(value(rec, FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET))){
			hasVideolet = true;
		}else{
			hasVideolet = false;
		}
		String []value = value(rec, FieldDefine.FIELD_NAME_CHAR_ISPLAY).split("\t");
		boolean canPlayValue = value[0].equals("1");
		if(canPlayValue){
			String tacs = value(rec, FieldDefine.FIELD_NAME_CHAR_MEDIA_CLASSID);
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
	}
	private void checkValid(Set<MediaRetrieveResultRecord>set, int mcids, String tacFbd)  throws Exception{
		Iterator<MediaRetrieveResultRecord> itr = set.iterator();
		while(itr.hasNext()){
			MediaRetrieveResultRecord rec = itr.next();
			checkValid(rec, mcids, tacFbd);
		}
	}
	
	private void checkValid(MediaRetrieveResultRecord rec, int mcid, String tacFbd) throws Exception{
		boolean hasVideolet = true, canPlay = false;
		if("1".equals(value(rec, FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET))){
			hasVideolet = true;
		}else{
			hasVideolet = false;
		}
		String []value = value(rec, FieldDefine.FIELD_NAME_CHAR_ISPLAY).split("\t");
		boolean canPlayValue = value[0].equals("1");
		if(canPlayValue){
			String mcids = value(rec, FieldDefine.FIELD_NAME_CHAR_MEDIA_CLASSID);
			String[]tacss = mcids.split("\t");
			boolean mcidAllow = false;
			for(String x : tacss){
				if(x.length() == 0){
					continue;
				}
				int t = Integer.parseInt(x);
				if(t == mcid){
					mcidAllow = true;
					break;
				}
			}
			canPlay = true;
			if(mcidAllow){
				String tacs = value(rec, FieldDefine.FIELD_NAME_CHAR_AREA_TACTIC);
				String[] tacstrs = tacs.split("\t");
				for(String x : tacstrs){
					if(x.equals(tacFbd)){
						canPlay = false;
					}
				}
			}
		}
		boolean include = hasVideolet || canPlay;
		is(include, "should Filter out! hasVideolet %s isplay %s, ta_x %S, for rec %s",
				value(rec, FieldDefine.FIELD_NAME_CHAR_HAS_VIDEOLET), 
				value(rec, FieldDefine.FIELD_NAME_CHAR_ISPLAY), 
				value(rec, FieldDefine.FIELD_NAME_CHAR_TA_0_9)
				,rec);
	}
	private void checkNotValid(HashSet<MediaRetrieveResultRecord>set, int tac) throws Exception{
		Iterator<MediaRetrieveResultRecord> itr = set.iterator();
		while(itr.hasNext()){
			MediaRetrieveResultRecord rec = itr.next();
			checkNotValid(rec, tac);
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

	@Override
	public void test() throws Exception {
		String word = "爱情 的";
		this.test4NewTactics(word, 2);
	}
	public static void main(String[]args) throws Exception{

		String host;
		if(args.length > 0){
			host = args[0];
		}else{
			host = Consoler.readString("connectHost:");
		}
		System.out.println("test host:" + host);
		MediaSearchThriftClient4NewTacticsTest ttc = new MediaSearchThriftClient4NewTacticsTest(host);
		ttc.test();
	}

}