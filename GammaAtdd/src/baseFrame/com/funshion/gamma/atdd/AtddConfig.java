package com.funshion.gamma.atdd;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.funshion.gamma.atdd.CommonThriftClientInfo.ClientInfo;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LogHelper;

@SuppressWarnings("rawtypes")
public class AtddConfig {
	static LogHelper log = new LogHelper("cfg");
	public final ParableInputGennor inputGennor;
	public final ResultComparator resultComparator;
	public final MethodAtddCommonThriftClient atddClient, serverClient;

	public PressTestHoster pHost;
	final ConfigReader cr;
	final boolean checkRetCode;
	final boolean checkRetMsg;
	public final boolean isForTestPress;
	public AtddConfig(ConfigReader cr) throws Exception{
		this.cr = cr;
		checkRetCode = cr.getInt("checkRetCode", 1 ) == 1;
		checkRetMsg = cr.getInt("checkRetMsg", 1 ) == 1;

		String inputGennorName = cr.getValue("inputGennor");

		Class inputGennorClass = Class.forName(inputGennorName);
		ParableInputGennor inputGennor = (ParableInputGennor) inputGennorClass.newInstance();
		isForTestPress = cr.getInt("isForTestPress", 0) == 1;
		if(isForTestPress){
			log.warn("press test mode!!!");
		}else{
			log.warn("function test mode!!!");
		}
		String onlyTo = cr.getValue("onlyGenAvsOrRcs");
		if(onlyTo != null){
			if(onlyTo.equalsIgnoreCase("avs")){
				ParableInputGennorOnlyGenOneType genor = new ParableInputGennorOnlyGenOneType(false);
				genor.setGennor(inputGennor);
				this.inputGennor = genor;

			}else if(onlyTo.equalsIgnoreCase("rcs")){
				ParableInputGennorOnlyGenOneType genor = new ParableInputGennorOnlyGenOneType(true);
				genor.setGennor(inputGennor);
				this.inputGennor = genor;
			}else{
				throw new Exception("unkown only gen Type");
			}
			log.warn("[------------------>InputGennor only gen %s test cases<----------------------]", onlyTo.toUpperCase());
		}else{
			this.inputGennor = inputGennor;
		}

		String resultComparatorName = cr.getValue("resultComparator", "");
		if(resultComparatorName.length() == 0){
			resultComparator = new ResultCompareDefault();
		}else{
			Class resultComparatorClass = Class.forName(resultComparatorName);
			resultComparator = (ResultComparator) resultComparatorClass.newInstance();
		}
		this.atddClient = new MethodAtddCommonThriftClient(cr, "avs");
		this.serverClient = new MethodAtddCommonThriftClient(cr, "rcs");
		int timeoutMs = cr.getInt("timeoutMs", 1000);
		log.info("timeout %sms", timeoutMs);
		this.atddClient.setConnectTimeout(timeoutMs);
		this.atddClient.setReadTimeout(timeoutMs);
		this.serverClient.setConnectTimeout(timeoutMs);
		this.serverClient.setReadTimeout(timeoutMs);
		log.warn("load test config ok: %s", this);
	}

	public void functionalTest(boolean interactive) {
		int runnedQuery = 0, exceptionQuery = 0;
		while(inputGennor.hasNext()){
			runnedQuery ++;
			try {
				query(interactive);
			} catch (Exception e) {
				exceptionQuery ++;
				e.printStackTrace();
				if(interactive){
					try {
						Consoler.readString("press Enter to continue...");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				}
			}
			if(runnedQuery % 100 == 0){
				log.warn("run query " + runnedQuery + ", exception " + exceptionQuery + ", fail Exe" + ((int)(exceptionQuery * 100000.0) / runnedQuery)/ 1000.0 + "%");
			}
		}
		log.info("finally ...");
		if(runnedQuery % 100 == 0){
			log.warn("run query " + runnedQuery + ", exception " + exceptionQuery + ", fail Exe" + ((int)(exceptionQuery * 100000.0) / runnedQuery)/ 1000.0 + "%");
		}
	}

	public void query(boolean interactive) throws Exception{
		QueryParas  q = this.inputGennor.nextAvsQuery();
		Object oAtdd = atddClient.query(q);
		Object oServer = serverClient.query(this.inputGennor.nextRcsQuery());
		//FIXME support compareMask?
		try{
			this.resultComparator.compare(inputGennor, oServer, oAtdd, null);
		}catch(Exception e){
			if(interactive)
			if(oAtdd != null && oServer != null){
				CompareTool.compare(q, oServer, oAtdd);
			}
			throw e;
		}
	}
	@Override
	public String toString(){
		String format = "AtddConfig for section %s\n" +
				" serverHost = %s\n" +
				" atddConfig= %s\n" +
				" inputGennor = %s\n" +
				" resultComparator = %s";

		return String.format(format, 
				cr.sectionName,
				this.serverClient,
				this.atddClient,
				inputGennor,
				resultComparator);
	}

	public void pressTest() throws Exception {

		List<GammaTestCase> testCases = getSomeTestCase(cr.getInt("testCaseNum", 10000));
		log.warn("get test cases %s", testCases.size());
		pHost = new PressTestHoster(testCases, serverClient, resultComparator, cr);
		pHost.startTest();
		log.info("pressTest started");
	}

	private List<GammaTestCase> getSomeTestCase(int i) throws Exception {
		List<GammaTestCase> ret = new ArrayList<GammaTestCase> ();
		HashSet<Integer>set = new HashSet<Integer>();
		ClientInfo ci = atddClient.getNewClient();
		try{
			while(true){
				if(inputGennor.hasNext()){
					QueryParas parasAtdd = inputGennor.nextAvsQuery();
					QueryParas parasService = inputGennor.nextRcsQuery();
					Object oAtdd = atddClient.query(parasAtdd, ci);
					if(checkRetCode){
						int retCode = retCode(oAtdd);
						if(retCode != 200){
							if(set.contains(retCode)){
								continue;
							}else{
								set.add(retCode);
							}
						}
					}
					//FIXME support CompareMask?
					GammaTestCase gtc = new GammaTestCase(parasService, oAtdd, null);
					ret.add(gtc);
					if(ret.size() >= i){
						break;
					}
				}else{
					break;
				}
			}
		}finally{
			ci.close();
		}
		log.info("load testCases %s", ret.size());
		Consoler.readString("testCases' ready! press Enter to continue...");

		return ret;
	}

	int retCode(Object o) throws Exception{
		int ret = (Integer) o.getClass().getField("retCode").get(o);
		return ret;
	}
	/**
	 * read config from main method, portable for shells
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static ConfigReader getConfig(String[]args) throws Exception{
		String cfgFile = null;
		String section = null;
		for(String x : args){
			if(x.toLowerCase().startsWith("-c")){
				cfgFile = x.substring(2);
			}else if(x.toLowerCase().startsWith("-s")){
				section = x.substring(2);
			}else{
				if(!x.startsWith("-")){
					if(section != null){
						section = x;
					}
				}
			}
		}
		if(cfgFile == null || section == null){
			log.warn("usage: -cConfigFilePath -sSectionInConfig");
			log.warn("if not set args for configSection, input it by consoler");
		}
		
		if(cfgFile == null){
			while(true){
				cfgFile = Consoler.readString("configFile:");
				File f = new File(cfgFile);
				if(!f.exists()){
					System.out.println("not exists! " + f);
					continue;
				}
				if(f.isDirectory()){
					System.out.println("is directory! " + f);
					continue;
				}
				break;
			}
		}

		File f = new File(cfgFile);
		if(!f.exists() || f.isDirectory() || !f.canRead()){
			log.warn("cfgFile can not accessable! file: %s",
					f.getAbsolutePath());
			System.exit(0);
		}
		log.warn("cfgFile: %s", f.getAbsolutePath());
		List<String> set = ConfigReader.listSectionsInConfigFile(f);
		while(true){
			if(section != null && set.contains(section)){
				break;
			}else{
				log.warn("available sections %s for file %s list", set, f);
				section = Consoler.readString("section in configFile:");
			}
		}
		log.warn("section %s in cfgFile: %s", section, f.getAbsolutePath());
		ConfigReader cr = new ConfigReader(f, section);
		return cr;
	}

}
