package com.funshion.search.utils.systemWatcher.innerAction;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.heartBeat.BeatEcho;
import com.funshion.search.utils.systemWatcher.heartBeat.HeartBeatChecker;
import com.funshion.search.utils.systemWatcher.heartBeat.ISNHBInfo;
import com.funshion.search.utils.systemWatcher.heartBeat.ISNHeartBeat;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class HeartBeatAction extends WatcherAction{
	public static final LogHelper log = new LogHelper("HeartBeatAction");
	public static final String CMD = "heartBeat";
	public HeartBeatAction() {
		super(CMD);
	}

	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		AnswerMessage am = WatcherAction.answerTemplate();
		int bodySize = message.getMessageBody().size();
		if(bodySize == 0){
			log.warn("invalid heartbeat from %s:%s", this.remoteIp, this.remotePort);
			am.setAnswerStatus(ANSWER_STATUS_NO_MESSAGE_PARAS);
			am.setActionStatus(ACTION_STATUS_OK);
		}else{
			for(String x : message.messageBody){
				ISNHeartBeat beat = ISNHeartBeat.parse(x);
				ISNHBInfo bc = new ISNHBInfo(super.remoteIp, beat.svcPort, beat.businessType);
				long ret = HeartBeatChecker.instance.heartBeat(bc,  beat.session);
				BeatEcho be = new BeatEcho(bc.businessType, bc.rmtPort, ret);
				am.addToAnswerBody(be.toString()) ;
				log.debug("heartbeat from %s:%s for business '%s', session %s", this.remoteIp, this.remotePort, beat.businessType, beat.session);
			}
			am.setAnswerStatus(ANSWER_STATUS_OK_RUNNED);
			am.setActionStatus(ACTION_STATUS_OK);
		}
		return am;
	}
}
