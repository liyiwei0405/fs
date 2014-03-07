package com.funshion.search.utils.systemWatcher.innerAction;

import java.util.Map.Entry;
import java.util.Properties;

import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class SysPropAction extends WatcherAction{
	public static final String CMD = "sysprop";
	public SysPropAction() {
		super(CMD);
	}
/**
 * message body size:
 * 0, check all
 * 1, check this property
 * 2, set the property
 * others, error
 */
	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		AnswerMessage ret = super.answerTemplate();
		if(message.messageBody.size() == 0){
			Properties pps = System.getProperties();
			flushProperties(pps, ret);
			ret.actionStatus = ACTION_STATUS_OK;
		}else if(message.messageBody.size() == 1) {
			String prpt = System.getProperty(message.messageBody.get(0));
			if(prpt == null) {
				prpt = "WARN:NO_THIS_PROPERTY:" + message.messageBody.get(0);
			}
			ret.answerBody.add(prpt);
			ret.actionStatus = ACTION_STATUS_OK;
		}else if(message.messageBody.size() == 2) {
			Properties pps = System.getProperties();
			System.setProperty(message.messageBody.get(0), message.messageBody.get(1));
			flushProperties(pps, ret);
			ret.actionStatus = ACTION_STATUS_OK;
		}else {
			ret.answerBody.add(message.messageBody.toString());
			ret.actionStatus = ACTION_STATUS_PARA_ERROR;
		}
		return ret;
	}
	private void flushProperties(Properties pps, AnswerMessage ret){
		for(Entry<Object, Object> e : pps.entrySet()){
			ret.addToAnswerBody(e.toString().replace("\n", "\\n").replace("\r", "\\r"));
		}
		
	}
}
