package com.funshion.search.utils;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SmtpClient {
	static LogHelper log = new LogHelper("smtpClient");
	private MimeMessage mimeMsg;
	private String host ;
	private Session session;
	private Properties props = new Properties();

	private String username;
	private String password;

	private Multipart mp;
	public SmtpClient(String host, String name, String pwd,  int port){
		this.setNamePass(host, name, pwd);
		//	props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "" + port);
		props.setProperty("mail.smtp.socketFactory.port", "25"); 
		
		props.put("mail.smtp.timeout", 3000);
		
		createMimeMessage();
	}


	private void createMimeMessage(){
		session = Session.getDefaultInstance(props, null);
		mimeMsg = new MimeMessage(session);
		mp = new MimeMultipart();
	}


	public void setNeedAuth(boolean need) {
		if(need){
			props.put("mail.smtp.auth","true");
		}else{
			props.put("mail.smtp.auth","false");
		}
	}


	public void setNamePass(String host, String name,String pass) {
		this.host = host;
		username = name;
		password = pass;
	}

	public void setSubject(String mailSubject) throws MessagingException {
		mimeMsg.setSubject(mailSubject);
	}


	public void setBody(String mailBody) throws MessagingException {
		BodyPart bp = new MimeBodyPart();
		bp.setContent(mailBody, "text/html;charset=utf-8");
		mp.addBodyPart(bp);
	}


	public void addFileAffix(String filename) throws Exception {

		log.debug("add affix："+filename);

		BodyPart bp = new MimeBodyPart();
		FileDataSource fileds = new FileDataSource(filename);
		bp.setDataHandler(new DataHandler(fileds));
		bp.setFileName(fileds.getName());

		mp.addBodyPart(bp);
	}

	public void setFrom(String from, String fromer) throws MessagingException, IOException {
		mimeMsg.setFrom(new InternetAddress(from, fromer));
	}
	public void addTo(Address[] to) throws AddressException, MessagingException{
		mimeMsg.addRecipients(
				Message.RecipientType.TO,
				to);
	}
	public void addTo(String to) throws AddressException, MessagingException{
		addTo(InternetAddress.parse(to));
	}

	public void addCopyTo(Address[] copyTo) throws MessagingException{
		if(copyTo == null || copyTo.length == 0)
			return;
		mimeMsg.addRecipients(Message.RecipientType.CC, 
				copyTo);
	}

	public boolean sendout() throws MessagingException{
		Transport transport = null;
		try{
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();


			Session mailSession = Session.getInstance(props, null);
			transport = mailSession.getTransport("smtp");
			transport.connect(host, username, password);
			transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
			//transport.send(mimeMsg);
			transport.close();
			return true;
		}finally{
			if(transport != null){
				transport.close();
			}
		}
	}

	public static boolean sendMail(MailHandle handle,
			String subject, String body){
		return sendMail(handle, handle.getMailto(), null,
				subject, body);
	}
	public static boolean sendMail(ConfigReader mailConfig,
			String title, String body) throws IOException, AddressException{
		MailHandle hdl = new MailHandle(mailConfig);

		return sendMail(hdl, hdl.getMailto(), null, title, body);
	}

	public static boolean sendMail(MailHandle handle,
			List<Address[]> to, List<Address[]>cc, String subject, String body){
		try{
			SmtpClient theMail = new SmtpClient(handle.host, 
					handle.name, handle.passwd, handle.port);//设置发送邮件服务器smtp
			theMail.setNeedAuth(true);//设置smtp是否需要认证
			theMail.setSubject(subject);
			theMail.setBody(body);
			for(Address[] add : to){
				theMail.addTo(add);
			}
			theMail.setFrom(handle.from, handle.fromName) ;

			if(cc != null){
				for(Address []ccx : cc){
					theMail.addCopyTo(ccx);
				}
			}


			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			return theMail.sendout();
		}catch(Exception e){
			LogHelper.log.error(e, 
					"when send to %s, by %s", 
					to, 
					handle);
			return false;
		}
	}
	public static List<Address[]>getMailListFromConfig(String strLst){
		List<Address[]>ret = new ArrayList<Address[]>();
		if(strLst == null){
			return ret;
		}
		
		String[]strs = strLst.replace('\t', ' ').replace(';', ' ').split(" ");
		for(String x : strs){
			x = x.trim();
			if(x.length() > 0){
				try{
					ret.add(InternetAddress.parse(x));
				}catch(Exception e){
					e.printStackTrace();
					LogHelper.log.error(e, "when init receipt");
				}
			}
		}
		return ret;
	}
	public static class MailHandle{
		private List<Address[]> mailto;
		private final  String fromName;
		private final String host;
		private final String name;
		private final String passwd;
		private final String from;
		private final int port;
		public MailHandle(ConfigReader cr) throws AddressException {
			this(
					cr.getValue("host"), 
					cr.getValue("name"), 
					cr.getValue("passwd"), 
					cr.getValue("from"),
					cr.getValue("fromName"),
					cr.getInt("port", 25),

					cr.getValue("mailto") == null ? new ArrayList<Address[]>() : getMailListFromConfig(cr.getValue("mailto")));
		}

		public MailHandle(String host, String name, String passwd, String from,
				String fromName){
			this(host, name, passwd, from, fromName, 25);
		}
		public MailHandle(String host, String name, String passwd, String from,
				String fromName, int port){
			this(host, name, passwd, from, fromName, port, null);
		}
		public MailHandle(String host, String name, String passwd, String from,
				String fromName, int port, List<Address[]> mailto){
			this.host = host;
			this.name = name;
			this.passwd = passwd;
			this.from = from;
			this.fromName = fromName;
			this.port = port;
			this.mailto = mailto;
		}
		public String toString() {
			return host + ":" + name + ":" + passwd + ":" + from + ":" + fromName;
		}
		public List<Address[]> getMailto() {
			return mailto;
		}
		public void addMailto(Address[] mailto) {
			//FIXME
			this.mailto.add(mailto);
		}
	}
}
