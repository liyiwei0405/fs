package com.funshion.gamma.atdd.healthWatcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.mail.Address;
import javax.mail.internet.AddressException;

import com.funshion.gamma.atdd.ResultComparatorBase;
import com.funshion.gamma.atdd.TestStatistics;
import com.funshion.gamma.atdd.healthWatcher.RpcServerInstance.MethodInfo;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.MunitesTimeMail;
import com.funshion.search.utils.SmtpClient.MailHandle;

public class MailConfig extends Thread{
	public static final String timingMailSection = "timingMail";
	public static final String adminSection = "admin";
	public static final File cfgFile = new File("./config/HealthWatcher.conf");

	public final LogHelper log = new LogHelper("MailConfig");
	MailTo adminMailAddress;
	private String envName;
	private URL gitBaseUrl;
	private File testCaseFolder;
	private URL configCenterBaseUrl;
	private List<RpcServiceInfo>services;
	private WatcherTimingMail timingMail;
	long minSendItv = 10 * 60 * 1000;
	public static final MailConfig instance = new MailConfig();
	private MailConfig(){
		ResultComparatorBase.disablePrintResult();
		timingMail = new WatcherTimingMail();
		try {
			reloadBasciInfo();
		} catch (Exception e) {
			log.error("can not load configs! system exists...");
			e.printStackTrace();
			System.exit(0);
		}
		timingMail.start();
	}

	@Override
	public void run(){
		while(true){
			log.debug("(re)loading services");
			try {
				reloadServices();
			} catch (Exception e) {
				log.error(e, "when reload services");
				e.printStackTrace();
			}
			try {
				reloadBasciInfo();
			} catch (Exception e1) {
				log.error(e1, "when reload BasciInfo");
				e1.printStackTrace();
			}
			try {
				sleep(60 * 1000);//parable
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public MailTo getAdminMailAddress() {
		return adminMailAddress;
	}

	private void reloadBasciInfo() throws Exception{
		ConfigReader cr = new ConfigReader(
				cfgFile, adminSection);
		adminMailAddress = MailTo.get(cr);
		envName = cr.getValue("envName", "innerTest");
		this.gitBaseUrl = new URL(cr.getValue("gitBaseUrl"));
		this.testCaseFolder = new File(cr.getValue("testCaseFolder"));
		this.configCenterBaseUrl = new URL(cr.getValue("configCenterBaseUrl"));
		this.timingMail.setTime(cr.getValue("dailyMailTime"));
		minSendItv = cr.getInt("minSendItvSecond", 10 * 60) * 1000;
	}
	public void reloadServices() throws IOException{
		List<RpcServiceInfo>newList =  loadRpcSerivces();
		if(services == null){
			services = newList;

			for(RpcServiceInfo svc : this.services){
				svc.start();//let RpcServiceInfo to boot RpcServerInstance
				log.warn("start new service %s", svc);
			}
		}else{
			List<RpcServiceInfo>toDel = new ArrayList<RpcServiceInfo>();
			List<RpcServiceInfo>toAdd = new ArrayList<RpcServiceInfo>();
			for(RpcServiceInfo rpc : this.services){
				if(!newList.contains(rpc)){
					toDel.add(rpc);
				}
			}

			for(RpcServiceInfo rpc : newList){
				if(!this.services.contains(rpc)){
					toAdd.add(rpc);
				}
			}
			for(RpcServiceInfo rpc :toDel){
				this.services.remove(rpc);
				rpc.stop();
				log.warn("remove not configed rpc %s", rpc);
			}

			for(RpcServiceInfo rpc :toAdd){
				this.services.add(rpc);
				rpc.start();
				log.warn("add new rpc %s", rpc);
			}
		}
	}


	public List<RpcServiceInfo> loadRpcSerivces() throws IOException{
		List<String>list = ConfigReader.listSectionsInConfigFile(cfgFile);
		list.remove(adminSection);
		list.remove(timingMailSection);
		List<RpcServiceInfo>lstService = new Vector<RpcServiceInfo>();
		for(String x : list){
			ConfigReader subCr = new ConfigReader(cfgFile, x);
			if(!validConfigSection(subCr)){
				continue;
			}
			try {
				RpcServiceInfo info = new RpcServiceInfo(subCr);
				lstService.add(info);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("when load section " + x);
			}
		}
		return lstService;
	}

	boolean validConfigSection(ConfigReader subCr){
		if(subCr.getInt("inuse", 1) == 0){
			log.debug("skip config section %s, not INUSE", subCr.sectionName);
			return false;
		}
		String serviceClass = subCr.getValue("service-name", null);
		if(serviceClass == null){
			log.error("has not service-name in %s, SKIP", subCr.sectionName);
			return false;
		}
		try{
			Class.forName(serviceClass);
		}catch(Exception e){
			log.error(e, "can not find class '%s' in section '%s'", serviceClass, subCr.sectionName);
			return false;
		}

		return true;
	}


	public String getEnvName() {
		return envName;
	}

	public URL getGitBaseUrl() {
		return gitBaseUrl;
	}

	public File getTestCaseFolder(){
		return testCaseFolder;
	}
	public void setGitBaseUrl(URL gitBaseUrl) {
		this.gitBaseUrl = gitBaseUrl;
	}

	public URL getConfigCenterBaseUrl() {
		return configCenterBaseUrl;
	}

	public void setConfigCenterBaseUrl(URL configCenterBaseUrl) {
		this.configCenterBaseUrl = configCenterBaseUrl;
	}

	public class WatcherTimingMail extends MunitesTimeMail{
		public WatcherTimingMail() {
			super(60);
		}

		@Override
		public String getMailTitle(Calendar c) {
			return "DailyReport[" + new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + "] of HealthWatcher";
		}
		void appendTr2(StringBuilder sb, String key, Object value){
			String line = "<tr><td><b>" + key + "</b></td><td>" + value + " </td></tr>\n";
			sb.append(line);
		}

		public String getStrInfo(TestStatistics statistic, int maxTimeLine, StringBuilder sb,
				Method m, RpcServerInstance svcInfo) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			sb.append("<table border = 1>");
			appendTr2(sb, "Location", "<font color = green>" + svcInfo.locationInfo + "</font>");
			appendTr2(sb, "Method", "<font color = green>" + m.getName() + "</font><br>(" + m + ")");
			appendTr2(sb, "RPC service", "<font color = green>" + svcInfo.rpcInfo.serviceNameInConfigCenter + "</font>");
			appendTr2(sb, "RPC Server",  svcInfo.rpcInfo.fullServiceClassName);
			appendTr2(sb, "时间", sdf.format(statistic.getStartTime()) + " - " +  sdf.format(System.currentTimeMillis()) 
					+ "("  + statistic.usedMs() / 1000.0 + " seconds)");
			//appendTr2(sb, "rps", statistic.rps() + " req/sec");
			appendTr2(sb, "thread Num", statistic.threads);
			appendTr2(sb, "sucessTestNum", statistic.getOktest());
			appendTr2(sb, "compareFail", statistic.getCompFail());
			appendTr2(sb, "exeFail", statistic.getExeFail());
			sb.append("</table>\n");
			sb.append("<table border = 1>");

			sb.append("<tr><td colspan=50>usedTimeMs | times(Px%s)</td></tr>" );
			int exeTimes[] = new int[statistic.exeTimes.length];
			int sum = 0;
			for(int x = 0; x < statistic.exeTimes.length; x ++){
				exeTimes[x] = statistic.exeTimes[x];
				sum += exeTimes[x];
			}
			if(sum == 0){
				sb.append("<tr><td colspan=50>exetimes: 0</td></tr>" );
			}else{
				int partSum = 0;
				sb.append("<tr>");
				for(int x = 0; x < Math.min(exeTimes.length, maxTimeLine); x ++){
					sb.append("<td>" +(1 + x) + "</td>");
				}
				sb.append("</tr>\n<tr>");
				for(int x = 0; x < Math.min(exeTimes.length, maxTimeLine); x ++){
					partSum += exeTimes[x];
					long factor = 1000L * partSum / sum;
					sb.append("<td>" + partSum + "<br>" + factor/10.0 + "%</td>");
				}
				sb.append("</tr>\n");
			}
			sb.append("</table>");

			return sb.toString();
		}
		@Override
		public String getMailContent(Calendar cld) {
			List<RpcServiceInfo> svcs = MailConfig.instance.getRpcServices();
			StringBuilder sb = new StringBuilder();
			sb.append("<table border = 1>");
			for(RpcServiceInfo rsi : svcs){
				sb.append("<tr><td>" + rsi.serviceNameInConfigCenter + "</td><td>");
				List<RpcServerInstance>  instanceList = rsi.getInstances();
				for(RpcServerInstance ins : instanceList){
					Map<Method, MethodInfo> map = ins.getMethodMap();
					Iterator<Entry<Method, MethodInfo>> itr = map.entrySet().iterator();
					while(itr.hasNext()){
						sb.append("<table border = 1>");
						Entry<Method, MethodInfo> e = itr.next();
						sb.append("<tr><td>Method</td><td>" + e.getKey().getName() + "</td></tr><tr><td>");
						MethodInfo mi = e.getValue();
						if(!mi.testRunnor.isRun){
							sb.append("<font color=blue>NOT RUN!</font>");
						}else{
							TestStatistics statistic = mi.testRunnor.swapStatics();
							getStrInfo(statistic, 50, sb, e.getValue().method, ins);
						}
						sb.append("</td></tr></table>\n");
					}
				}
				sb.append("</td></tr>");
			}
			sb.append("</table>");
			return sb.toString();
		}

		@Override
		public MailHandle getHandler() throws AddressException, IOException {
			MailHandle hdl = new MailHandle(new ConfigReader(cfgFile, timingMailSection));
			for(Address[] mailto : adminMailAddress.mailTo){
				hdl.addMailto(mailto);
			}
			return hdl;
		}
	}
	public MailHandle getMailHandler() throws IOException, AddressException {
		return new MailHandle(new ConfigReader(cfgFile, timingMailSection));
	}
	public List<RpcServiceInfo> getRpcServices(){
		return this.services;
	}
	public long getMinSendItv() {
		return minSendItv;
	}
}
