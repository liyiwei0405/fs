package pressTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;

import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.search.media.search.atdd.MediaSearchThriftClient;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;

public class LongConnectionTest {
	static String host;
	class SearchTest implements Runnable{
		MediaSearchThriftClient client;
		ArrayList<RetrieveStruct> qss = new ArrayList<RetrieveStruct>();
		SearchTest() throws Exception{
			TSocket socket = new TSocket(host, 3537); 
			socket.open();
			SocketAddress sa = socket.getSocket().getRemoteSocketAddress();
			if(sa instanceof InetSocketAddress){
				socket.getSocket().setReuseAddress(true);
			}
			TFramedTransport trans = new TFramedTransport(socket);
			TProtocol protocol = new TBinaryProtocol(trans); 
			client = new MediaSearchThriftClient(protocol); 
		}
		Statistic st;
		@Override
		public void run() {
			for(RetrieveStruct qs : qss){
				st = new Statistic("");

				try {
					long t1 = System.nanoTime();
					MediaRetrieveResult result = client.retrive1(qs);
					long t8 = System.nanoTime();
					st.resultCnt = result.total;
					st.serverError = result.retCode != 200;
					st.searchUsed = result.usedTime;
					st.totUsed = (t8 - t1) / 100000 / 10.0;
				} catch (Exception e) {
					e.printStackTrace();
					st.hasError = true;
				}
				
				flushResult(st);
			}
		}
	}

	public void execute(int threadNum) throws Exception{
		ArrayList<SearchTest>stss= new ArrayList<SearchTest>();
		for(int x = 0; x < threadNum; x ++){
			SearchTest st = new SearchTest();
			stss.add(st);
		}
		
		int idx = 0;
		while(true){
			RetrieveStruct qs = TestQueries.getQuery();
			if(qs == null){
				break;
			}
			int i = idx % threadNum;
			stss.get(i).qss.add(qs);
			idx ++;
		}
		
		System.out.println("loadOk");
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
			lw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	ExecutorService exe;
	LineWriter lw;
	public LongConnectionTest(int threadNum) throws IOException{
		exe = Executors.newFixedThreadPool(threadNum);
		lw = new LineWriter("q.xls", false, Charset.forName("gbk"));
		lw.writeLine("时间\t关键词\t命中\t搜索用时\t总用时\t异常\t-\t声明socket\t建立连接\t声明protocal\t声明client\t搜索\t关闭连接");
	}
	public static void main(String[]args) throws Exception{
		LogHelper.log.info("starting test");
		host = Consoler.readString("queryHost:");
		int threadNum = Consoler.readInt("query threads(default 50):", 50); 
		System.out.println("threads:" + threadNum);
		LongConnectionTest slt = new LongConnectionTest(threadNum);

		slt.execute(threadNum);

	}
}
