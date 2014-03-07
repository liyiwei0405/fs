package com.funshion.search.utils;

import java.io.IOException;
import java.util.Calendar;

import com.funshion.search.utils.SmtpClient.MailHandle;


public abstract class TimingMail extends Thread{
	protected final long checkItvSeconds;
	protected final MailHandle handler;
	public TimingMail(ConfigReader cr) throws IOException{
		this(new MailHandle(cr), 60);
	}
	public TimingMail(ConfigReader cr, int checkItvSeconds) throws IOException{
		this(new MailHandle(cr), checkItvSeconds);
	}
	public TimingMail(MailHandle handler, int checkItvSeconds){
		this.checkItvSeconds = checkItvSeconds;
		this.handler = handler;
	}
	
	public abstract boolean canSend(Calendar c);
	public abstract String getMailTitle(Calendar c);
	public abstract String getMailContent(Calendar c);
	
	public final void run(){
		while(true){
			Calendar c = Calendar.getInstance();
			
			if(canSend(c)){
				String title = getMailTitle(c);
				String content = getMailContent(c);
				SmtpClient.send_Mail(handler, title, content);
			}
			try {
				Thread.sleep(checkItvSeconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public final boolean sendMail(String title, String content){
		return SmtpClient.send_Mail(handler, title, content);
	}
}
