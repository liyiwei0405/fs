package com.funshion.search.utils;

import java.util.Calendar;

import com.funshion.search.utils.SmtpClient.MailHandle;

public abstract class TimingMail extends Thread{
	protected final long checkItvSeconds;
	public TimingMail() {
		this(60);
	}
	public TimingMail(int checkItvSeconds) {
		this.checkItvSeconds = checkItvSeconds;
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
				try {
					SmtpClient.sendMail(getHandler(), title, content);
				} catch (Exception e) {
					LogHelper.log.warn(e, "when send mail with title '" + title + "'");
				}
			}
			try {
				Thread.sleep(checkItvSeconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public final boolean sendMail(String title, String content) throws Exception{
		return SmtpClient.sendMail(getHandler(), title, content);
	}
	public abstract MailHandle getHandler() throws Exception ;
}
