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

import test.VideoletThriftClient;

import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.videolet.thrift.QueryStruct;
import com.funshion.search.videolet.thrift.VideoletSearchResult;

public class ShortConnectionTest {
	static String host;
	class SearchTest implements Runnable{

		QueryStruct qs;
		SearchTest(QueryStruct qs){
			this.qs = qs;
		}
		Statistic st;
		@Override
		public void run() {
			st = new Statistic(qs.word);
			try {
				long t1 = System.nanoTime();
				long t2 = t1;
				TSocket socket = new TSocket(host, 3531); 
				long t3 = System.nanoTime();
				socket.open();
				SocketAddress sa = socket.getSocket().getRemoteSocketAddress();
				if(sa instanceof InetSocketAddress){
					socket.getSocket().setReuseAddress(true);
				}
				long t4 = System.nanoTime();
				TFramedTransport trans = new TFramedTransport(socket);
				TProtocol protocol = new TBinaryProtocol(trans); 
				long t5 = System.nanoTime();
				VideoletThriftClient client = new VideoletThriftClient(protocol); 
				long t6 = System.nanoTime();
				VideoletSearchResult result = client.query(qs);
				long t7 = System.nanoTime();
				socket.close();
				long t8 = System.nanoTime();
				st.resultCnt = result.total;
				st.serverError = result.status != 0;
				st.searchUsed = result.usedTime;
				st.totUsed = t8 - t1;
				st.v12 = t2 - t1;
				st.v23 = t3 - t2;
				st.v34 = t4 - t3;
				st.v45 = t5 - t4;
				st.v56 = t6 - t5;
				st.v67 = t7 - t6;
				st.v78 = t8 - t7;
			} catch (Exception e) {
				e.printStackTrace();
				st.hasError = true;
			}
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flushResult(st);
		}
	}
	
	public void execute(){
		ArrayList<QueryStruct>qss = new ArrayList<QueryStruct>();
		while(true){
			QueryStruct qs = TestQueries.getQuery();
			if(qs == null){
				break;
			}
			qss.add(qs);
		}
		System.out.println("loadOk");
		for(QueryStruct qs : qss){
			SearchTest st = new SearchTest(qs);
			this.exe.execute(st);
		}
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
	public ShortConnectionTest(int threadNum) throws IOException{
		exe = Executors.newFixedThreadPool(threadNum);
		lw = new LineWriter("/q.xls", false, Charset.forName("gbk"));
		lw.writeLine("时间\t关键词\t命中\t搜索用时\t总用时\t异常\t-\t声明socket\t建立连接\t声明protocal\t声明client\t搜索\t关闭连接");
	}
	public static void main(String[]args) throws Exception{
		host = Consoler.readString("queryHost:");
		int threadNum = Consoler.readInt("query threads(default 50):", 50); 
		ShortConnectionTest slt = new ShortConnectionTest(threadNum);
		
		slt.execute();
		
	}
}
