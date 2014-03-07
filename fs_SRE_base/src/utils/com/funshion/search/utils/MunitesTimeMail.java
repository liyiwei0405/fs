package com.funshion.search.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public abstract class MunitesTimeMail extends TimingMail{
	LogHelper log = new LogHelper("MunitesTimeMail");
	final ArrayList<int[]>lst = new ArrayList<int[]>();
	public MunitesTimeMail(int checkItv) {
		super(checkItv);
	}
	public void setTime(int[]...mailAtMunites){
		lst.clear();
		lst.addAll(Arrays.asList(mailAtMunites));
	}
	/**
	 * String as "HH:mm" or "HH"
	 * @param mailAtMunites
	 */
	public void setTime(String...mailAtMunites){
		ArrayList<int[]>lst = new ArrayList<int[]>();
		for(String xx : mailAtMunites){
			xx = xx.trim();
			if(xx.length() == 0)
				continue;
			int pos = xx.indexOf(":");
			final int mailHour, mailMunites;
			if(pos == -1){
				mailHour = Integer.parseInt(xx);
				mailMunites = 0;
			}else{
				mailHour = Integer.parseInt(xx.substring(0, pos).trim());
				mailMunites = Integer.parseInt(xx.substring(1 + pos).trim());
			}

			log.info("reg mail log at Hour %s, munite %s.", mailHour, mailMunites);
			lst.add(new int[]{
					mailHour, mailMunites
			});
		}
		this.lst.clear();
		this.lst.addAll(lst);
		if(this.lst.size() == 0){
			log.warn("mail log disable");
		}
		
		
	}
	@Override
	public boolean canSend(Calendar c) {
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int munites = c.get(Calendar.MINUTE);
		for(int[] x : lst){
			if(x[0] == hour){
				if(x[1] == munites){
					return true;
				}
			}
		}
		return false;
	}

}
