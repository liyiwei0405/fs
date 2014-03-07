package test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
import com.funshion.search.videolet.chgWatcher.VideoletRecord;
import com.funshion.search.videolet.search.VideoletIndexer;
import com.funshion.search.videolet.thrift.FieldFilter;
import com.funshion.search.videolet.thrift.FieldWeight;
import com.funshion.search.videolet.thrift.LimitBy;
import com.funshion.search.videolet.thrift.QueryStruct;
import com.funshion.search.videolet.thrift.SortBy;
import com.funshion.search.videolet.thrift.VideoletResult;
import com.funshion.search.videolet.thrift.VideoletSearchResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class TestVideoletThriftClient{

	class MongoDBEntryGet{
		MongoClient ugcClient, microClient;
		DBCollection  colUgc;
		DBCollection  colMicro;
		MongoDBEntryGet() throws UnknownHostException, IOException{
			ConfigReader cr = new ConfigReader("./config/VideoletExport/mongo.rdf", "fs_video_ugc");
			MongoClient ugcClient = new MongoClient(cr.getValue("ip") , cr.getInt("port") );
			DB db = ugcClient.getDB(cr.getValue("database"));
			colUgc = db.getCollection(cr.getValue("table"));

			cr = new ConfigReader("./config/VideoletExport/mongo.rdf", "fs_video");
			microClient = new MongoClient(cr.getValue("ip") , cr.getInt("port") );
			db = microClient.getDB(cr.getValue("database"));
			colMicro = db.getCollection(cr.getValue("table"));
		}
		public DBObject getUGC(int videoid){
			return get(colUgc, videoid);
		}
		public DBObject getMicro(int videoid){
			return get(colMicro, videoid);
		}
		public DBObject get(DBCollection  col, int videoid){
			DBCursor cur = col.find(new BasicDBObject("videoid", videoid));
			if(cur.hasNext()){
				return cur.next();
			}else{
				return null;
			}
		}
	}
	MongoDBEntryGet vg;
	TestVideoletThriftClient() throws UnknownHostException, IOException{
		vg = new MongoDBEntryGet();
	}
	public  void test(QueryStruct qs) { 
		test(qs, false);
	}
	public  void test(QueryStruct qs, boolean isGroup) { 
		try { 
			long stTime1 = System.currentTimeMillis();
			TTransport socket = new TSocket(host, 3521); 
			long stTime2 = System.currentTimeMillis();
			socket.open(); 
			long stTime3 = System.currentTimeMillis();
			TFramedTransport trans = new TFramedTransport(socket);
			TProtocol protocol = new TBinaryProtocol(trans); 
			long stTime4 = System.currentTimeMillis();
			VideoletThriftClient client = new VideoletThriftClient(protocol); 
			long stTime5 = System.currentTimeMillis();
			VideoletSearchResult result = client.query(qs);
			long stTime6 = System.currentTimeMillis();
			System.out.println(
					String.format(
							"\n %s\t%s\t%s\t%s\t%s\t(%s)\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",
							stTime2 - stTime1, 
							stTime3 - stTime2, 
							stTime4 - stTime3, 
							stTime5 - stTime4, 
							stTime6 - stTime5,
							stTime6 - stTime1
							));
			System.out.print(result.status);
			System.out.print("\t useSeconds:" + ((int)(10000 * result.usedTime)) / 10000.0);
			System.out.println("\t totalmatch: " + result.total);

			System.out.println();

			if(isGroup){
				if(result.ids != null){
					for(VideoletResult vr : result.ids){
						System.out.println("\t@groupby=" + vr.videoid + ", @count=" + vr.videotype);
					}
				}
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				PrintStream pr = System.out;
				if(result.ids != null){
					for(VideoletResult vr : result.ids){
						System.out.flush();
						System.err.flush();
						DBObject dbo;
						String prefix = "";
						if(vr.videotype == VideoletRecord.isMicroVideoType){
							dbo = vg.getMicro(vr.videoid);
							pr = System.err;
							prefix = "MIC";
						}else if(vr.videotype == VideoletRecord.isUGCType){
							dbo = vg.getUGC(vr.videoid);
							prefix = "UGC";
							pr = System.out;
						}else {
							System.err.println("unknown videotype:" + vr.videotype);
							continue;
						}
						String toPrint = "\t【%s】_id = %s, videoid = %s, 【%s】,createDate = %s, modifyDate = %s, [%s], {%s}, %s";
						pr.println(
								String.format(toPrint,
										prefix,
										dbo.get("_id"),
										dbo.get("videoid"),
										dbo.get("playnum"),
										sdf.format(1000L * (Integer)(dbo.get("createdate"))),
										sdf.format(1000L * (Integer)(dbo.get("modifydate"))),
										dbo.get("publishflag"),
										dbo.get("title"),
										dbo.get("tags"))
								);
					}
				}
				System.out.flush();
				System.err.flush();
			}
			socket.close(); 
		} catch (TTransportException e) { 
			e.printStackTrace(); 
		} catch (TException e) {
			e.printStackTrace(); 
		} 
	} 

	public void testSort(String word){
		//吃饭啦
		QueryStruct qs = new QueryStruct();
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

		test(qs);

		srtLst.clear();
		srtLst.add(byModifyDate);
		srtLst.add(byWeight);
		srtLst.add(byPlayNum);
		qs.sortFields = srtLst;
		test(qs);


		srtLst.clear();
		srtLst.add(byPlayNum);
		srtLst.add(byModifyDate);
		srtLst.add(byWeight);

		qs.sortFields = srtLst;

		test(qs);
		test(qs);

	}
	public void testWeight(String word){

		QueryStruct qs = new QueryStruct();
		List<FieldWeight>weightLst = new ArrayList<FieldWeight>();
		FieldWeight titleHigh = new FieldWeight(20, "title");
		FieldWeight tagHigh = new FieldWeight(20, "tags");

		qs.word = word;
		qs.searchModel = 1;
		test(qs);

		weightLst.clear();
		weightLst.add(titleHigh);
		qs.weights = weightLst;

		test(qs);

		weightLst.clear();
		weightLst.add(tagHigh);
		qs.weights = weightLst;

		test(qs);
		test(qs);

	}
	public void testLimit(String word){

		QueryStruct qs = new QueryStruct();
		LimitBy lb = new LimitBy(0, 21);
		qs.word = word;
		qs.searchModel = 1;
		qs.limits = lb;
		test(qs);

		qs = new QueryStruct();
		lb = new LimitBy(20, 20);
		qs.word = word;
		qs.searchModel = 1;
		qs.limits = lb;
		test(qs);

		qs = new QueryStruct();
		lb = new LimitBy(5000, 20);
		qs.word = word;
		qs.searchModel = 1;
		qs.limits = lb;
		test(qs);

	}

	public void testFliter(String word){

		QueryStruct qs = new QueryStruct();
		List<FieldFilter>filterLst = new ArrayList<FieldFilter>();
		FieldFilter f2 = new FieldFilter(VideoletIndexer.INDEX_VIDEO_OR_UGC, VideoletRecord.isUGCType, false);
		qs.word = word;
		qs.searchModel = 1;
		test(qs);


		filterLst.add(f2);
		qs.filters = filterLst;
		test(qs);

	}

	/**

	2013-03-27 19:53:04,735 DEBUG [VideoletQueryIface] - use client 192.168.130.37:23518 for businessType:VideoletSS from 127.0.0.1:57780
2013-03-27 19:53:04,736 DEBUG [192.168.130.37:23518] - connect to 192.168.130.37:23518, use 1
org.sphx.api.SphinxException: searchd error: per-query max_matches=5020 out of bounds (per-server max_matches=1500)
        at org.sphx.api.SphinxClient.response(SphinxClient.java:430)
        at org.sphx.api.SphinxClient.executeCommand(SphinxClient.java:488)
        at org.sphx.api.SphinxClient.runQueries(SphinxClient.java:1243)
        at org.sphx.api.SphinxClient.query(SphinxClient.java:1047)
        at org.sphx.api.SphinxClient.query(SphinxClient.java:1026)
        at com.funshion.search.videolet.ss.videolet.VideoletQueryIface.query(VideoletQueryIface.java:149)
        at com.funshion.search.videolet.ss.videolet.VideoletQueryIface.query(VideoletQueryIface.java:71)
        at com.funshion.search.videolet.thrift.QueryVideolet$Processor$query.getResult(QueryVideolet.java:172)
        at com.funshion.search.videolet.thrift.QueryVideolet$Processor$query.getResult(QueryVideolet.java:1)
        at org.apache.thrift.ProcessFunction.process(ProcessFunction.java:39)
        at org.apache.thrift.TBaseProcessor.process(TBaseProcessor.java:39)
        at org.apache.thrift.server.TThreadPoolServer$WorkerProcess.run(TThreadPoolServer.java:206)
        at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(Unknown Source)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
        at java.lang.Thread.run(Unknown Source)
2013-03-27 19:53:04,753 DEBUG [VideoletQueryIface] - use client 192.168.130.37:33518 for businessType:VideoletSS from 127.0.0.1:57780
	 * @throws IOException 
	 * @throws UnknownHostException 

	 **/
	
	public static String host = "127.0.0.1";
	public static void main(String[]args) throws UnknownHostException, IOException{
		String str = Consoler.readString("connectHost:");
		if(str.length() != 0){
			host = str;
		}
		System.out.println("test host:" + host);
		TestVideoletThriftClient ttc = new TestVideoletThriftClient();
		ttc.testLimit("你好");
		ttc.testSort("我饿啦");
		ttc.testWeight("非你莫属 爆笑刘俐俐事件解说");
		ttc.testFliter("你好");
	}
}