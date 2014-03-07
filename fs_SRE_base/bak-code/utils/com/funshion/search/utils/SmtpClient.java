package com.funshion.search.utils;

import java.io.IOException;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
	String to;
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
		mimeMsg.setFrom(new InternetAddress(from, fromer));   //设置发信人
	}

	public void setTo(String to) throws AddressException, MessagingException{
		this.to = to;
		mimeMsg.setRecipients(
				Message.RecipientType.TO,
				InternetAddress.parse(to));
	}

	public boolean setCopyTo(String copyto){
		if(copyto == null)
			return false;
		try{
			mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copyto));
			return true;
		}catch(Exception e){ 
			return false;
		}
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

	public static boolean send_Mail(MailHandle handle,
			String subject, String body){
		return sendMail(handle, handle.mailto,
				subject, body);
	}
	public static boolean sendMail(ConfigReader mailConfig,
			String title, String body) throws IOException{
		MailHandle hdl = new MailHandle(mailConfig);

		return sendMail(hdl, hdl.mailto, title, body);
	}
	public static boolean sendMail(MailHandle handle,
			String to, String subject, String body){
		try{
			SmtpClient themail = new SmtpClient(handle.host, 
					handle.name, handle.passwd, handle.port);//设置发送邮件服务器smtp
			themail.setNeedAuth(true);//设置smtp是否需要认证
			themail.setSubject(subject);
			themail.setBody(body);
			themail.setTo(to);
			themail.setFrom(handle.from, handle.fromName) ;

			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			return themail.sendout();
		}catch(Exception e){
			LogHelper.log.error(e, 
					"when send to %s, by %s", 
					to, 
					handle);
			return false;
		}
	}

	public static class MailHandle{
		private final String mailto;
		private final  String fromName;
		private final String host;
		private final String name;
		private final String passwd;
		private final String from;
		private final int port;
		public MailHandle(ConfigReader cr) throws IOException{
			this(
					cr.getValue("host"), 
					cr.getValue("name"), 
					cr.getValue("passwd"), 
					cr.getValue("from"),
					cr.getValue("fromName"),
					cr.getInt("port", 25),
					cr.getValue("mailto"));
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
				String fromName, int port, String mailto){
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
	}
}
