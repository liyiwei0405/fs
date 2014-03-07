package com.funshion.search.utils.systemWatcher;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;

import com.funshion.search.utils.systemWatcher.innerAction.ShutDownAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;
@Ignore("obviously, this is not should be tested under JUnit")
public class SystemWatcherShutdownTest {
	static final int port = (int) (65400 + 100 * Math.random());


	@Test
	public void testRegStopHook() throws Exception {
		String prop = System.getenv("OS");
		final Process proc = Runtime.getRuntime().exec(new String[]{
				"java",
				prop != null && prop.toLowerCase().startsWith("windows") ? "-cp .;bin" : "-cp .:bin",
						"com.funshion.search.utils.systemWatcher.SystemWatcherShutdownTestServer",
						"" + port
		});
		System.out.println("port is " + port);
		new Thread(){
			public void run(){
				try{
					
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					InputStream ops = proc.getInputStream();
					while(true){
						int i = ops.read();
						if(i == -1){
							break;
						}
						bos.write(i);
						System.err.println((char)i);
					}
					byte[] bs = bos.toByteArray();
					System.err.println(new String(bs));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
		Thread.sleep(1000);
		MessageClient clt = new MessageClient(port);
		QueryMessage qm = MessageClient.empotyQuery(ShutDownAction.CMD);
		AnswerMessage am = clt.queryMsg(qm);
		System.out.println(am);
		Thread.sleep(300);
		fail("shutdown test fail!");
	}


}
