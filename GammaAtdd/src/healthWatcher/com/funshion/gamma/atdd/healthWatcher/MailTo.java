package com.funshion.gamma.atdd.healthWatcher;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.SmtpClient;
public class MailTo {

	public final List<Address[]> mailTo;
	public final List<Address[]>ccList;
	public MailTo(List<Address[]> mailTo, List<Address[]>ccList){
		this.mailTo = mailTo;
		this.ccList = ccList;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("mailTo: ");
		int x = 0;
		for(Address[] mailToOne : this.mailTo){
			for(Address a : mailToOne){
				if(++x > 1){
					sb.append("; ");
				}
				sb.append(a);

			}
		}
		sb.append(", mailCC:");
		for(Address[]mailTo : ccList){
			for(Address a : mailTo){
				sb.append(a);
				sb.append(";");
			}
		}
		return sb.toString();
	}
	public static MailTo get(ConfigReader cr) throws Exception{
		String mailTo = cr.getValue("mailTo");
		List<Address[]>mailToAdd = SmtpClient.getMailListFromConfig(mailTo);
		if(mailToAdd.size() == 0){
			throw new Exception("mailTo not set!!");
		}
		String cc = cr.getValue("mailCC");
		List<Address[]>ccList = new ArrayList<Address[]>();
		if(cc != null){
			ccList = SmtpClient.getMailListFromConfig(cc);
		}
		return new MailTo(mailToAdd, ccList);
	}

}
