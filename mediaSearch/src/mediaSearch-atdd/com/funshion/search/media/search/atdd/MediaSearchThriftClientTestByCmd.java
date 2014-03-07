package com.funshion.search.media.search.atdd;

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

public class MediaSearchThriftClientTestByCmd{
	final String host;
	MediaSearchThriftClientTestByCmd(String testHost){
		host  = testHost;
	}
	public  void test(RetrieveStruct qs) { 
		try {
			long stTime1 = System.currentTimeMillis();
			TTransport socket = new TSocket(host, 3537, 500); 
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
							"\n conn:%s\topen:%s\ttrans:%s\tprotocol:%s\tquery:%s\t(%s)\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",
							stTime2 - stTime1, 
							stTime3 - stTime2, 
							stTime4 - stTime3, 
							stTime5 - stTime4, 
							stTime6 - stTime5,
							stTime6 - stTime1
							));
			System.out.print(result.retCode + "\t" + result.retMsg);
			System.out.print("\t use ms:" + ((int)(10000 * result.usedTime)) / 10000.0);
			System.out.println("\t totalmatch: " + result.total);

			System.out.println();

			if(result.ids != null){
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
			}
			System.out.flush();
			System.err.flush();

			socket.close(); 
		} catch (Exception e) { 
			System.out.println("!!!!!!!!!!exc=" + e + "!!!!!!!!!!!");
			e.printStackTrace(); 
		} 
	} 


	public void testLimit(String word) throws Exception{

		RetrieveStruct qs = new RetrieveStruct();
		PortalCompoundCondition cc = new PortalCompoundCondition();
		PortalItemCondition fullKw = new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES, OperType.SEARCH_TITLE_FULL,
				word);
		PortalItemCondition segKw = new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES, OperType.SEARCH_TITLE,
				word);
		
		PortalCompoundCondition ccCanPlay = new PortalCompoundCondition();
		PortalItemCondition isplay = new PortalItemCondition(FieldDefine.FIELD_NAME_ISPLAY, OperType.EQUAL,
				"1");
		PortalItemCondition hasvideolet = new PortalItemCondition(FieldDefine.FIELD_NAME_HAS_VIDEOLET, OperType.EQUAL,
				"1");
		PortalItemCondition mcids = new PortalItemCondition(FieldDefine.FIELD_NAME_MEDIA_CLASSID, OperType.EQUAL,
				"1"); 
		PortalItemCondition areaTactics = new PortalItemCondition(FieldDefine.FIELD_NAME_AREA_TACTIC, OperType.EQUAL,
				"pc2"); 
		
		PortalCompoundCondition ccOr = new PortalCompoundCondition();
		ccOr.addCondition(ConjunctType.AND, isplay);
		ccOr.addCondition(ConjunctType.AND, mcids);
		ccOr.addCondition(ConjunctType.NOT, areaTactics);
		
		PortalCompoundCondition ccOr2 = new PortalCompoundCondition();
		ccOr2.addCondition(ConjunctType.OR, ccOr);
		ccOr2.addCondition(ConjunctType.OR, hasvideolet);
		
		
		ccCanPlay.addCondition(ConjunctType.AND, segKw);
		ccCanPlay.addCondition(ConjunctType.AND, ccOr2);
		
		cc.addCondition(ConjunctType.OR, fullKw);
		cc.addCondition(ConjunctType.OR, ccCanPlay);
		
		qs.conditions = cc.toTokenList();
		qs.ver = 1;
		LimitRetrieve lb = new LimitRetrieve(0, 100);
		qs.limits = lb;
		//		qs.addToFilters(new FieldFilter(FieldDefine.FIELD_NAME_CAN_DISPLAY, 9, false));
		test(qs);

	}

	public static void main(String[]args) throws Exception{
		String host;
		if(args.length > 0){
			host = args[0];
		}else{
			host = Consoler.readString("connectHost:");
		}
		System.out.println("test host:" + host);
		MediaSearchThriftClientTestByCmd ttc = new MediaSearchThriftClientTestByCmd(host);
		while(true){
			System.out.flush();
			String line = Consoler.readString(":");
			System.out.flush();
			ttc.testLimit(line);
			Thread.sleep(500);
			System.out.println();
		}
		//		ttc.testSort("我饿啦");
		//		ttc.testWeight("非你莫属 爆笑刘俐俐事件解说");
		//		ttc.testFliter("你好");
	}
}