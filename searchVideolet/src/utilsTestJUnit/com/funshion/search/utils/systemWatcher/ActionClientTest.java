package com.funshion.search.utils.systemWatcher;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.funshion.search.utils.systemWatcher.innerAction.EchoAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class ActionClientTest {
	static final int port = (int) (65400 + 100 * Math.random());
	static final String sysName = "SystemWatcherTest";
	static SystemWatcher sw;

	@BeforeClass
	public static void initTest() throws TTransportException, IOException{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
		sw = new SystemWatcher(port, sysName, false);
	}
	
	@Test
	public void echoTest() throws TException{
		MessageClient clt = new MessageClient(port);
		QueryMessage qm = MessageClient.empotyQuery(EchoAction.CMD);
		AnswerMessage am = clt.queryMsg(qm);
		assertEquals("error echo answer status", am.answerStatus, WatcherAction.ANSWER_STATUS_OK_RUNNED);
		assertEquals("error echo action status", am.actionStatus, WatcherAction.ACTION_STATUS_OK);
		assertEquals("error echo result status", am.answerBody.get(0), "?");
		
		String queryStr = "echo?";
		qm.messageBody.add("echo?");
		am = clt.queryMsg(qm);
		assertEquals("error echo answer status", am.answerStatus, WatcherAction.ANSWER_STATUS_OK_RUNNED);
		assertEquals("error echo action status", am.actionStatus, WatcherAction.ACTION_STATUS_OK);
		assertEquals("error echo result status", am.answerBody.get(0), queryStr);
		
		qm.messageBody.add("echo?");
		am = clt.queryMsg(qm);
		assertEquals("error echo answer status", am.answerStatus, WatcherAction.ANSWER_STATUS_OK_RUNNED);
		assertEquals("error echo action status", am.actionStatus, WatcherAction.ACTION_STATUS_OK);
		assertEquals("error echo result status", am.answerBody.get(0), "[warn] skip paras 1 or more:" + queryStr);
	}
	
	@AfterClass
	public static void closeSys(){
		sw.close();
	}
}
