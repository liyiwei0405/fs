package com.funshion.videoService.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineWriter;
import com.funshion.videoService.thrift.RetrieveStruct;
import com.funshion.videoService.thrift.VideoBaseListResult;
import com.funshion.videoService.thrift.VideoListResult;
import com.funshion.videoService.thrift.VideoService;
import com.funshion.videoService.thrift.VideoletBaseInfo;
import com.funshion.videoService.thrift.VideoletBaseRetrieveResult;
import com.funshion.videoService.thrift.VideoletInfo;
import com.funshion.videoService.thrift.VideoletRetrieveResult;

public class TestVideoServiceClient {
	public static SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日:HH-mm-ss");
	public static ConfigReader cr;
	static{
		try {
			cr = new ConfigReader(new File("./config/videoService.conf"), "client");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static String ip = null;
	static int port = 4537;

	public static VideoService.Client getClient(ConfigReader cr) throws TTransportException, Exception{
		String ip = cr.getValue("ip", "127.0.0.1");
		int port = cr.getInt("port", 4537);

		if(ip == null){
			ip = Consoler.readString("ip (default 127.0.0.1):");
			port = Consoler.readInt("port (default 4537):", 4537);
		}
		System.out.println("connecting to " + ip + ":" + port + " ...");
		TSocket skt = new TSocket(ip, port);
		skt.setTimeout(0);
		skt.open();
		System.out.println("connected to " + ip + ":" + port + " ...");
		TFramedTransport trans = new TFramedTransport(skt);
		TBinaryProtocol prot = new TBinaryProtocol(trans);
		return new VideoService.Client(prot);
	}

	public static void testInfo(String methodName) throws Exception{
		String[] ids = Consoler.readString("videoids(以空格分隔)>").trim().split(" ");
		List<Integer>lst = new ArrayList<Integer>(ids.length);
		for(String id : ids){
			if(id != null && id.length() > 0){
				lst.add(Integer.parseInt(id));
			}
		}
		VideoService.Client client = getClient(cr);
		long st = System.currentTimeMillis();
		if(methodName.equals("getVideoBaseListByIds")){
			VideoBaseListResult result = client.getVideoBaseListByIds(lst);
			long ed = System.currentTimeMillis();
			System.out.println("retCode : " + result.retCode);
			System.out.println("retMsg  : " + result.retMsg);
			System.out.println("total :" + result.videoBaseList.size());
			System.out.println("usedTime ms: " + (ed - st));
			for(VideoletBaseInfo vi : result.videoBaseList){
				System.out.println(vi);
			}
		}else{
			VideoListResult result = client.getVideoListByIds(lst);
			long ed = System.currentTimeMillis();
			System.out.println("retCode : " + result.retCode);
			System.out.println("retMsg  : " + result.retMsg);
			System.out.println("total :" + result.videoList.size());
			System.out.println("usedTime ms: " + (ed - st));
			for(VideoletInfo vi : result.videoList){
				System.out.println(vi);
			}
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	public static void testLimit() throws Exception, Exception{
		String fsql = Consoler.readString("fsql:");
		String paras = Consoler.readString("paras:");
		String tokens[] = paras.split(",");
		RetrieveStruct rs = new RetrieveStruct();
		rs.fsql = fsql;

		rs.paras = new ArrayList<String>();
		for(String x : tokens){
			x = x.trim();
			if(x.length() > 0){
				rs.paras.add(x);
			}
		}
		rs.ver = 1;
		System.out.println("...query:\n\t" + rs);
		VideoService.Client client = getClient(cr);
		
		VideoletBaseRetrieveResult res = client.retrieveVideoletBaseInfo(rs);
		System.out.println("...result:\n\t" + res);
	}

	public static void testSearch(String methodName, LineWriter lw) throws Exception{
		String fsql = "";
		while(true){
			try {
				fsql = Consoler.readString("fsql>");
				fsql = fsql.trim();
				if(fsql.length() < 1){
					continue;
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.err.flush();
			}
		}
		RetrieveStruct rs = new RetrieveStruct();
		rs.fsql = fsql;

		rs.paras = new ArrayList<String>(0);
		rs.ver = 1;
		VideoService.Client client = getClient(cr);
		//		rs.sortFields
		long st = System.currentTimeMillis();
		if(methodName.equals("retrieveVideoletBaseInfo")){
			VideoletBaseRetrieveResult res = client.retrieveVideoletBaseInfo(rs);

			long ed = System.currentTimeMillis();
			System.out.println("retCode : " + res.retCode);
			System.out.println("retMsg  : " + res.retMsg);
			System.out.println("total   : " + res.total);
			System.out.println("usedTime ms: " + res.usedTime);
			System.out.println("usedTime ms: " + (ed - st) + " (client side aware)");

			if(res.retCode == 200){
				for(VideoletBaseInfo vi : res.videoList){
					System.out.println(vi.toString());
				}
			}
			if(! fsql.trim().isEmpty()){
				lw.writeLine("\ndate: " + sdf.format(new Date()));
				lw.writeLine("fsql: " + fsql);
				lw.writeLine("retCode: " + res.retCode);
				lw.writeLine("retMsg: " + res.retMsg);
				lw.writeLine("total: " + res.total);
				lw.writeLine("usedTime ms: " + res.usedTime);
				lw.writeLine("usedTime ms: " + (ed - st) + " (client side aware)");
				lw.flush();
			}
		}else{
			VideoletRetrieveResult res = client.retrieveVideolet(rs);

			long ed = System.currentTimeMillis();
			System.out.println("retCode : " + res.retCode);
			System.out.println("retMsg  : " + res.retMsg);
			System.out.println("total   : " + res.total);
			System.out.println("usedTime ms: " + res.usedTime);
			System.out.println("usedTime ms: " + (ed - st) + " (client side aware)");

			if(res.retCode == 200){
				for(VideoletInfo vi : res.videoList){
					System.out.println(vi.toString());
				}
			}
			if(! fsql.trim().isEmpty()){
				lw.writeLine("\ndate: " + sdf.format(new Date()));
				lw.writeLine("fsql: " + fsql);
				lw.writeLine("retCode: " + res.retCode);
				lw.writeLine("retMsg: " + res.retMsg);
				lw.writeLine("total: " + res.total);
				lw.writeLine("usedTime ms: " + res.usedTime);
				lw.writeLine("usedTime ms: " + (ed - st) + " (client side aware)");
				lw.flush();
			}
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	public static void main(String[] args) throws Exception {
		LineWriter lw = new LineWriter(new File("./fsql.txt"), true, Charset.forName("utf-8"));

		System.out.println("select methods: getVideoBaseListByIds, getVideoListByIds, retrieveVideoletBaseInfo, retrieveVideolet");
		String methodName = Consoler.readString("").trim();
		while(true){
			try{
				if(methodName.equals("getVideoBaseListByIds") || methodName.equals("getVideoListByIds")){
					testInfo(methodName);
				}else if(methodName.equals("retrieveVideoletBaseInfo") || methodName.equals("retrieveVideolet")){
					testSearch(methodName, lw);
				}else{
					System.out.println("wrong method");
				}
			}catch(Exception e){
				e.printStackTrace();
				Thread.sleep(500);
			}
		}
	}
}
