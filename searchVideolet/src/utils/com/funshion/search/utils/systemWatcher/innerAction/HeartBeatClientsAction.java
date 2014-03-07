package com.funshion.search.utils.systemWatcher.innerAction;

import java.util.List;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.heartBeat.HeartBeatChecker;
import com.funshion.search.utils.systemWatcher.heartBeat.ISNHBInfo;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class HeartBeatClientsAction extends WatcherAction{
	public static final LogHelper log = new LogHelper("hbClients");
	public static final String CMD = "hbclients";
	public HeartBeatClientsAction() {
		super(CMD);
	}

	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		AnswerMessage am = WatcherAction.answerTemplate();
		int bodySize = message.getMessageBody().size();
		if(bodySize == 0){
			List<ISNHBInfo> lst = HeartBeatChecker.instance.getClients();
			for(ISNHBInfo ii : lst){
				am.addToAnswerBody(ii.toString());
			}
			am.setAnswerStatus(ANSWER_STATUS_OK_RUNNED);
			am.setActionStatus(ACTION_STATUS_OK);
		}else if(bodySize == 1){
			List<ISNHBInfo> lst = HeartBeatChecker.instance.getClients(message.getMessageBody().get(0));
			for(ISNHBInfo ii : lst){
				am.addToAnswerBody(ii.toString());
			}
			am.setAnswerStatus(ANSWER_STATUS_OK_RUNNED);
			am.setActionStatus(ACTION_STATUS_OK);
		}else{
			am.setAnswerStatus(ANSWER_STATUS_OK_RUNNED);
			am.setActionStatus(ACTION_STATUS_PARA_ERROR);
			am.addToAnswerBody("too much request parameters:" + message.getMessageBody());
		}
		return am;
	}
}
