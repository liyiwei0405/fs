package com.funshion.search.utils.systemWatcher.innerAction;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class ChangeLogExceptionAction extends WatcherAction{
	public static final String CMD = "CH_LOG_ExcP";
	public ChangeLogExceptionAction() {
		super(CMD);
	}

	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		AnswerMessage ret = super.answerTemplate();
		if(message.messageBody.size() > 0){
			String cmdPara = message.messageBody.get(0);
			String to ;
			if("true".equalsIgnoreCase(cmdPara)){
				LogHelper.setPrintException(true);
			}else if("false".equalsIgnoreCase(cmdPara)){
				LogHelper.setPrintException(false);
			}
			to = "now is  " + LogHelper.isPrintException() ;
			ret.actionStatus = ACTION_STATUS_OK;
			ret.answerBody.add(to);
		}else{
			String prmt = " use ture/false para switch to change";
			ret.actionStatus = ACTION_STATUS_PARA_ERROR;
			ret.answerBody.add(prmt);
		}

		return ret;
	}
}