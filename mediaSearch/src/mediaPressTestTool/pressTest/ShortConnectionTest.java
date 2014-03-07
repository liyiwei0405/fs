package pressTest;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.search.media.search.atdd.MediaSearchThriftClient;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;

public class ShortConnectionTest {
	
	static String host;
	class SearchTest implements Runnable{
		Statistic st;
		SearchTest() throws Exception{

		}

		@Override
		public void run() {

			while(true){

				st = new Statistic("");
				TSocket socket = null;


				try {
					long t1 = System.nanoTime();
					socket = new TSocket(host, 3537); 
					socket.open();
					SocketAddress sa = socket.getSocket().getRemoteSocketAddress();
					if(sa instanceof InetSocketAddress){
						socket.getSocket().setReuseAddress(true);
					}
					TFramedTransport trans = new TFramedTransport(socket);
					TProtocol protocol = new TBinaryProtocol(trans); 
					MediaSearchThriftClient client = new MediaSearchThriftClient(protocol); 
					RetrieveStruct qs = TestQueries.getQuery();
					MediaRetrieveResult result = client.retrive1(qs);
					long t8 = System.nanoTime();
					st.resultCnt = result.total;
					st.serverError = result.retCode != 200;
					st.searchUsed = result.usedTime;
					st.totUsed = (t8 - t1) / 100000 / 10.0;
				} catch (Exception e) {
					e.printStackTrace();
					st.hasError = true;
				}finally{
					if(socket != null){
						socket.close();
					}
				}

				flushResult(st);

			}
		}
	}

	public void execute(int threadNum) throws Exception{
		initWriter();
		
		ArrayList<SearchTest>stss= new ArrayList<SearchTest>();
		for(int x = 0; x < threadNum; x ++){
			SearchTest st = new SearchTest();
			stss.add(st);
		}


		System.out.println("loadOk: " + TestQueries.qws.size());
		for(SearchTest st : stss){
			this.exe.execute(st);
		}
		exe.shutdown();
	}
	SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss.SSS");
	synchronized void flushResult(Statistic st) {
		try {
			lw.write(sdf.format(System.currentTimeMillis()));
			lw.write('\t');
			lw.writeLine(st);
			int num = itg.addAndGet(1);
			if(num % 500000 == 0){
				initWriter();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	int num = 0;
	void initWriter() throws IOException{
		if(lw != null){
			lw.close();
		}
		lw = new LineWriter(new File(saveDir, "q" + (++ num) + ".xls"), false, Charset.forName("gbk"));
		lw.writeLine("时间\t关键词\t命中\t搜索用时\t总用时\t异常\t-\t声明socket\t建立连接\t声明protocal\t声明client\t搜索\t关闭连接");
	}
	
	ExecutorService exe;
	LineWriter lw;
	AtomicInteger itg = new AtomicInteger(0);
	
	public ShortConnectionTest(int threadNum) throws IOException{
		exe = Executors.newFixedThreadPool(threadNum);
		
	}
	static File saveDir;
	public static void main(String[]args) throws Exception{
		LogHelper.log.info("starting test");

		ConfigReader cr;
		if(args.length < 2){
			String cfgPath = Consoler.readString("cfgPath:");
			String section = Consoler.readString("section:");
			cr = new ConfigReader(new File(cfgPath), section);
		}else{
			cr = new ConfigReader(new File(args[0]), args[1]);
		}
		File fileToLoad = new File(cr.getValue("fileToLoad")); 
		TestQueries.load(fileToLoad);
		
		host = cr.getValue("host");
		int threadNum = cr.getInt("threads", 32); 
		System.out.println("threads:" + threadNum);
		saveDir = new File(cr.getValue("saveDir"));
		if(!saveDir.exists()){
			saveDir.mkdirs();
		}
		ShortConnectionTest slt = new ShortConnectionTest(threadNum);
		slt.execute(threadNum);
	}
}
