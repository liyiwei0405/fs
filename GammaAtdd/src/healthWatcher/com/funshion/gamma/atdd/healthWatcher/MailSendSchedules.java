package com.funshion.gamma.atdd.healthWatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.internet.AddressException;

import com.funshion.gamma.atdd.serialize.ThriftSerializeTool;
import com.funshion.search.Counter;
import com.funshion.search.CounterNum;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.SmtpClient;

public class MailSendSchedules extends Thread{
	private static final Object GlobalLock = new Object();

	final LogHelper log;

	private long lastSend = 0;
	final long maxBufferLen = 64 * 1024;
	Counter<String>counter = new Counter<String>();
	public MailSendSchedules(RpcServiceInfo rpcInfo, ServiceInstanceLocationInfo locationInfo){
		log = new LogHelper(rpcInfo.serviceNameInConfigCenter + "@" + locationInfo.shortName());
	}
	public void mail(MailTitleAndContent mail, MailTo mailTo) throws AddressException, IOException{
		SmtpClient.sendMail(MailConfig.instance.getMailHandler(), 
				mailTo.mailTo, mailTo.ccList,
				mail.title,
				getBufferedMailInfo() + mail.content
				);
	}
	private String getBufferedMailInfo() {
		List<Entry<String, CounterNum>> ents= counter.topEntry(10000);
		counter.renew();
		int x = 0;
		StringBuilder sb = new StringBuilder();
		String fmt = "<tr><td>%s</td><td>%s</td></tr>\n";
		for(Entry<String, CounterNum> e : ents){
			sb.append(String.format(fmt, e.getKey(), e.getValue()));
			x += e.getValue().intValue();
		}
		if(x > 0){
			return "<font color = red> totalBufferedMail:" + x + ", PLEASE see detail in my logs</font>\n" +
					"<table border = 2><border = 1>"
					+ String.format(fmt, "错误", "次数")
					+ sb
					+ "</table>";

		}else{
			return "";
		}
	}
	public void sendBufferableMail(MailTitleAndContent mail, MailTo mailTo) throws AddressException, IOException{
		if(!bufferMail(mail)){
			mail(mail, mailTo);
			this.setLastSend();
		}
	}
	private boolean shouldBuffer(){
		return System.currentTimeMillis() - this.getLastSend() < MailConfig.instance.getMinSendItv();
	}
	public boolean bufferMail(MailTitleAndContent mail){
		if(shouldBuffer()){
			counter.count(mail.title);
			log.fatal("bufferedMail! title = %s",
					mail.title);
			return true;
		}else{
			return false;
		}
	}

	public long getLastSend() {
		return lastSend;
	}

	public void setLastSend() {
		this.lastSend = System.currentTimeMillis();
	}

	static class MailTitleAndContent{
		String title;
		String content;
		Exception e;
	}
	public MailTitleAndContent getMailContent(String exps, Exception e,
			ServiceInstanceLocationInfo locationInfo, RpcServiceInfo rpcInfo,
			Method m, NamedGammaTestCase tc){
		MailTitleAndContent ret = new MailTitleAndContent();
		ret.title = "[" + rpcInfo.serviceNameInConfigCenter + "] " + locationInfo.shortName() + " | " + (e.getMessage() == null ? (e instanceof java.lang.reflect.InvocationTargetException ? "Socket Read timed out" : "") : e.getMessage());

		StringBuilder sb = new StringBuilder();
		sb.append("<table border=1>");
		buildContent(sb, "环境:", MailConfig.instance.getEnvName());
		buildContent(sb, "time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()));
		buildContent(sb, "nameCC", rpcInfo.serviceNameInConfigCenter);
		buildContent(sb, "service", rpcInfo.fullServiceClassName);
		buildContent(sb, "Server", locationInfo.toString());
		buildContent(sb, "Method", 
				"<font color=red><b>" + ThriftSerializeTool.methodStringName(m) + "</font></b>\n" + 
						m.toString().replace(" ", "\n"));
		buildContent(sb, "TestCase", tc.name );
		buildContent(sb, "md5Flag", tc.md5Flag );


		if(e != null){
			ByteArrayOutputStream ops = new ByteArrayOutputStream();
			PrintStream stm = new PrintStream(ops);
			e.printStackTrace(stm);
			buildContent(sb, "exception", exps + new String(ops.toByteArray()));
			ret.content = sb.toString();
		}
		ret.e = e;
		return ret;
	}
	private static void buildContent(StringBuilder sb, String name, String value){
		sb.append("<tr><td><b>");
		sb.append(name);
		sb.append("</b></td><td>");
		sb.append(value.trim().replace("\n", "<br>"));
		sb.append("</tr>");
	}
	public void sendBufferableMail(String exps, Exception e,
			ServiceInstanceLocationInfo locationInfo, RpcServiceInfo rpcInfo, MailTo mailTo, Method m, NamedGammaTestCase tc) throws AddressException, IOException {
		MailTitleAndContent mail = getMailContent(exps, e, locationInfo, rpcInfo, m, tc);
		sendBufferableMail(mail, mailTo);
	}
}
