package com.funshion.search.utils.systemWatcher.innerAction;

import java.io.File;

import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public final class SysInfoAction extends WatcherAction{
	public static final String CMD = "sysinfo";
	final String SysName;
	final long startTime;
	public SysInfoAction(String SysName) {
		super(CMD);
		this.SysName = SysName;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		AnswerMessage ret = super.answerTemplate();
		File baseFile = new File("");
		baseFile = baseFile.getAbsoluteFile();
		
		double passedSeconds = (System.currentTimeMillis() - startTime)/1000.0;
		double passedHour = (System.currentTimeMillis() - startTime)/60/60/10;
		passedHour = passedHour /100.0;
		
		
		ret.answerBody.add("SysName:" + SysName);
		ret.answerBody.add("uptime:" + passedHour + " Hour (" +passedSeconds + " seconds)");
		ret.answerBody.add("work at:" + baseFile.getAbsolutePath());
		ret.answerBody.add("maxMemory " + Runtime.getRuntime().maxMemory()/1000000.0 + "m ");
		ret.answerBody.add("totalMemory " + Runtime.getRuntime().totalMemory()/1000000.0 + "m "); 
		ret.answerBody.add("freeMemory " + Runtime.getRuntime().freeMemory()/1000000.0 + "m ");
		ret.answerBody.add("still can use " + 
			(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory())/1000000.0 + "m ");

		ret.actionStatus = ACTION_STATUS_OK;
		
		return ret;
	}
}