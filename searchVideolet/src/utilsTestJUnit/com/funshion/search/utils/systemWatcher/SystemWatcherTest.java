package com.funshion.search.utils.systemWatcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.funshion.search.utils.systemWatcher.innerAction.EchoAction;
import com.funshion.search.utils.systemWatcher.innerAction.ShutDownAction;
import com.funshion.search.utils.systemWatcher.innerAction.ShutDownAction.ShutdownHook;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class SystemWatcherTest {
	static final int port = (int) (65400 + 100 * Math.random());
	static final String sysName = "SystemWatcherTest";
	static SystemWatcher sw;

	@BeforeClass
	public static void initTest() throws TTransportException, IOException{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
		sw = new SystemWatcher(port, sysName);
	}
	@Test(expected=IOException.class)
	public void testInit() throws TTransportException, IOException{
		String prop = System.getenv("OS");
		if(prop != null && prop.toLowerCase().startsWith("windows")){
			throw new IOException("this method ingnored!!!!!! becouse you are running testcase on windows");
		}


		System.out.println("be sure you are not run testcase under windows enviroment");
		new SystemWatcher(port, sysName);
	}
	final String icmd = "testAction";
	public class IEchoAction  extends WatcherAction{

		public IEchoAction() {
			super(icmd);
		}
		@Override
		public AnswerMessage run(QueryMessage message) throws Exception {
			AnswerMessage ret = super.answerTemplate();
			ret.actionStatus = ACTION_STATUS_OK;
			if(message.messageBody.size() < 1) {
				ret.answerBody.add("?");
			}else if(message.messageBody.size() == 1) {
				ret.answerBody.add(cmd + " tests: " + message.messageBody.get(0));
			}else{
				ret.addToAnswerBody("[warn] skip paras1+:" + message.messageBody.get(0));
			}
			return ret;
		}
	}
	@Test
	public void testActionsNewInject() {
		sw.regAction(new IEchoAction());
		Set<String>set = sw.actions();
		String[] innerSvc = new String[]{
				"force stop",
				"help",
				"ch_log_excp",
				"gc",
				"sysprop",
				"echo",
				"sysinfo",
				icmd.toLowerCase()
		};
		for(String x : innerSvc){
			if(!set.contains(x)){
				fail("actions do not contains inner action " + x);
			}
		}

	}
	@Test
	public void testActions() {
		Set<String>set = sw.actions();
		String[] innerSvc = new String[]{
				"force stop",
				"help",
				"ch_log_excp",
				"gc",
				"sysprop",
				"echo",
				"sysinfo",
				"heartbeat"
		};
		for(String x : innerSvc){
			if(!set.contains(x)){
				fail("actions do not contains inner action " + x);
			}
		}
	}

	@Test
	public void testRegAction() throws TException {
		IEchoAction ea = new IEchoAction();
		sw.regAction(new IEchoAction());

		MessageClient clt = new MessageClient(port);
		QueryMessage qm = MessageClient.empotyQuery(ea.cmd);
		AnswerMessage am = clt.queryMsg(qm);
		assertEquals("error echo answer status", am.answerStatus, WatcherAction.ANSWER_STATUS_OK_RUNNED);
		assertEquals("error echo action status", am.actionStatus, WatcherAction.ACTION_STATUS_OK);
		assertTrue("error echo result status", am.answerBody.get(0).equals("?"));

		String queryStr = "echo?";
		qm.messageBody.add("echo?");
		am = clt.queryMsg(qm);
		assertEquals("error echo answer status", am.answerStatus, WatcherAction.ANSWER_STATUS_OK_RUNNED);
		assertEquals("error echo action status", am.actionStatus, WatcherAction.ACTION_STATUS_OK);
		assertTrue("error echo result status", am.answerBody.get(0).endsWith(queryStr));

		qm.messageBody.add("echo?");
		am = clt.queryMsg(qm);
		assertEquals("error echo answer status", am.answerStatus, WatcherAction.ANSWER_STATUS_OK_RUNNED);
		assertEquals("error echo action status", am.actionStatus, WatcherAction.ACTION_STATUS_OK);
		assertTrue("error echo result status", am.answerBody.get(0).endsWith(queryStr));

	}


	@Ignore("should not be tested using JUnit")
	public void testBlock() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPort() {
		assertEquals("port mismatch", sw.getPort(), port);
	}

	@Test
	@Ignore("this test case should be re-design")
	public void testGetShutDownAction() throws Exception {
		final ShutdownHook sh = sw.regStopHook();
		assertNotNull(sh);
		clientAskForQuit();
		Thread tDistroy = new Thread(){
			public void run(){
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sh.distroy();
			}
		};
		tDistroy.start();
		Thread.sleep(1000);
		ShutdownHook sh2 = sw.regStopHook();
		assertNull(sh2);

	}

	@AfterClass
	public static void closeSys(){
		sw.close();
	}

	private void clientAskForQuit() throws TException{
		new Thread(){
			public void run(){
				try{
					MessageClient clt = new MessageClient(port);
					QueryMessage qm = MessageClient.empotyQuery(ShutDownAction.CMD);
					AnswerMessage am = clt.queryMsg(qm);
					System.out.println(am);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}
}
