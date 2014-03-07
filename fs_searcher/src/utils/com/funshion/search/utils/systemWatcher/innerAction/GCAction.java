package com.funshion.search.utils.systemWatcher.innerAction;

import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public final class GCAction extends WatcherAction{
	public static final String CMD = "gc";
	public GCAction() {
		super(CMD);
	}

	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		AnswerMessage ret = super.answerTemplate();
		ret.actionStatus = ACTION_STATUS_OK;
		Runtime runtime = Runtime.getRuntime();
		ret.answerBody.add("maxMemory " + runtime.maxMemory()/1000000.0 + "m ");

		ret.answerBody.add("totalMemory " + runtime.totalMemory()/1000000.0 + "m " );
		ret.answerBody.add("freeMemory " + runtime.freeMemory()/1000000.0 + "m " );
		ret.answerBody.add("still can use " + 
				(runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory())/1000000.0 + "m ");
		System.gc();
		ret.answerBody.add("maxMemory " + runtime.maxMemory()/1000000.0 + "m " );
		ret.answerBody.add("totalMemory " + runtime.totalMemory()/1000000.0 + "m " );
		ret.answerBody.add("freeMemory " + runtime.freeMemory()/1000000.0 + "m " );
		ret.answerBody.add("still can use " + 
				(runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory())/1000000.0 + "m ");

		return ret;
	}
}