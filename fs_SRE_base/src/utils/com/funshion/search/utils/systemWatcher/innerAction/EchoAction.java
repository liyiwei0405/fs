package com.funshion.search.utils.systemWatcher.innerAction;

import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;
/**
 * just echo the query. mainly used for test connection, make test case etc.
 * @author liying
 *
 */
public class EchoAction  extends WatcherAction{
	public static final String CMD = "echo";
	public EchoAction() {
		super(CMD);
	}
	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		AnswerMessage ret = super.answerTemplate();
		ret.actionStatus = ACTION_STATUS_OK;
		if(message.messageBody.size() < 1) {
			ret.answerBody.add("?");
		}else if(message.messageBody.size() == 1) {
			ret.answerBody.add(message.messageBody.get(0));
		}else{
			ret.addToAnswerBody("[warn] skip paras 1 or more:" + message.messageBody.get(0));
		}
		return ret;
	}
}

