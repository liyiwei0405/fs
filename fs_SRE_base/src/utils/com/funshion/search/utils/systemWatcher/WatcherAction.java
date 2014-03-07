package com.funshion.search.utils.systemWatcher;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;


public abstract class WatcherAction{
	public static final short ANSWER_STATUS_OK_RUNNED = 0;
	public static final short ANSWER_STATUS_EXCEPTION_WHEN_RUN = 10;
	public static final short ANSWER_STATUS_NO_MESSAGE_ACTION_REGISTER = 20;
	public static final short ANSWER_STATUS_NO_MESSAGE_POINTER = 21;
	public static final short ANSWER_STATUS_NO_MESSAGE_NAME = 22;
	public static final short ANSWER_STATUS_NO_MESSAGE_PARAS = 23;
	public static final short ANSWER_STATUS_ACTION_RET_NULL = 30;


	public static final int ACTION_STATUS_OK = 1;
	protected static final short ACTION_STATUS_TMP_ERROR = 2;
	public static final int ACTION_STATUS_PARA_ERROR = 20;

	public static boolean isStatusOK(AnswerMessage am){
		if(am != null){
			if(am.answerStatus == ANSWER_STATUS_OK_RUNNED){
				if(am.actionStatus == ACTION_STATUS_OK){
					return true;
				}
			}
		}
		return false;
	}
	public static final AtomicLong il = new AtomicLong(0);
	
	public static AnswerMessage answerTemplate(){
		return answerTemplate(ANSWER_STATUS_OK_RUNNED);
	}
	public static AnswerMessage answerTemplate(short status){
		AnswerMessage am = new AnswerMessage();
		am.answerStatus = status;
		am.serverSeq = il.addAndGet(1);
		am.actionStatus = ACTION_STATUS_OK;
		am.answerBody = new ArrayList<String>();
		return am;
	}

	protected String remoteIp;
	protected int remotePort;
	public final String cmd;

	public WatcherAction(String cmd){
		this.cmd = cmd.trim().toLowerCase();
	}

	public String getCmd() {
		return cmd;
	}
	public abstract AnswerMessage run(QueryMessage message) throws Exception;
	public void setRemoteIp(String ip) {
		this.remoteIp = ip;
	}
	public void setRemotePort(int port) {
		this.remotePort = port;
	}

}