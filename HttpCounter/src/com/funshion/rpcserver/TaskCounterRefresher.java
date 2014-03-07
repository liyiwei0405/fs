package com.funshion.rpcserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.server.TThreadPoolServer;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;

public class TaskCounterRefresher extends Thread{
	class ConfigSection{
		public final String section;
		public final Map<String, String>setting;
		public ConfigSection(ConfigReader cr){
			this.section = cr.sectionName;
			this.setting = cr.read2Map();
		}
	}
	class ConfigFile{
		public final File file;
		public List<ConfigSection>configSections = new ArrayList<ConfigSection>();
		public ConfigFile(File file) throws IOException{
			this.file = file;

			List<String> strs = ConfigReader.listSectionsInConfigFile(file);
			for(String x : strs){
				ConfigReader cr = new ConfigReader(file, x, "utf-8");
				ConfigSection sec = new ConfigSection(cr);
				this.configSections.add(sec);
			}
		
		}
	}
	
	private final LogHelper log = new LogHelper("TC");
	protected HashMap<String, MethodTaskCounter>map = new HashMap<String, MethodTaskCounter>();
	protected HashMap<String, AtomicLong>vars_long = new HashMap<String, AtomicLong>();
	protected HashMap<String, String>vars_string = new HashMap<String, String>();
	private TThreadPoolServer.Args serverArgs;
	private long startTime;
	private List<ConfigFile>configs ;
	private TaskCounterRefresher(){}
	
	public static final TaskCounterRefresher instance = new TaskCounterRefresher();
	
	public synchronized void init(int jettyPort, TThreadPoolServer.Args serverArgs, File configFiles[]) throws IOException{
		PropertyConfigurator.configureAndWatch(ConfUtils.getConfFile("log4j.properties").getAbsoluteFile().toString());
		if(startTime == 0){
			startTime = System.currentTimeMillis();
			this.serverArgs = serverArgs;
			this.start();
		}
		List<ConfigFile> readers = new ArrayList<ConfigFile>();
		
		if(configFiles != null){
			for(File f : configFiles){
				readers.add(new ConfigFile(f));
			}
		}
		this.configs = Collections.unmodifiableList(readers);
		Jetty jetty = new Jetty(jettyPort);
		jetty.start();
	}
	
	public Map<String, List<Map<String, Object>>> getMap() {
		Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
		for(Entry<String, MethodTaskCounter> entry : this.map.entrySet()){
			map.put(entry.getKey(), entry.getValue().getTasksAsMap());
		}
		return map;
	}

	public SysInfo getSysInfo(){
		return new SysInfo(this.startTime, this.serverArgs);
	}
	
	public MethodTaskCounter registerCounter(String name, int ...checkItvs){
		MethodTaskCounter ret = map.get(name);
		if(ret != null){
			log.debug("register %s's counter denied, becouse already registered",  name);
			return ret;
		}
		ret = new MethodTaskCounter(name, checkItvs);
		map.put(name, ret);
		return ret;
	}
	
	public MethodTaskCounter getRegisteredTask(String name){
		return this.map.get(name);
	}
	
	public AtomicLong registerVars_long(String name){
		AtomicLong ret = vars_long.get(name);
		if(ret != null){
			log.debug("register %s's counter denied, becouse already registered",  name);
			return ret;
		}
		ret = new AtomicLong();
		vars_long.put(name, ret);
		return ret;
	}
	
	public HashMap<String, AtomicLong> getVars_long() {
		return vars_long;
	}

	public void setVars_long(HashMap<String, AtomicLong> vars1) {
		this.vars_long = vars1;
	}
	
	public long incVars_long(String name, int v){
		AtomicLong var = vars_long.get(name);
		if(var == null){
			var = new AtomicLong(0);
			this.vars_long.put(name, var);
		}
		return var.addAndGet(v);
	}
	
	public AtomicLong putVars_long(String name, AtomicLong v){
		this.vars_long.put(name, v);
		return vars_long.get(name);
	}
	
	public String registerVars_string(String name, String val){
		String ret = vars_string.get(name);
		if(ret != null){
			log.debug("register %s's counter denied, becouse already registered",  name);
			return ret;
		}
		ret = val;
		vars_string.put(name, ret);
		return ret;
	}
	
	public HashMap<String, String> getVars_string() {
		return vars_string;
	}

	public void setVars_string(HashMap<String, String> vars2) {
		this.vars_string = vars2;
	}
	
	public void putVars_string(String name, String val){
		this.vars_string.put(name, val);
	}
	
	private MethodTaskCounter besueExists(String name){
		MethodTaskCounter c = map.get(name);
		if(c == null){
			c = registerCounter(name);
		}
		return c;
	}
	
	public void incOkServed(String name, long usedNanoSec){
		MethodTaskCounter c = this.besueExists(name);
		c.incOkServiced(usedNanoSec);
	}
	
	public void incServed(String name){
		MethodTaskCounter c = this.besueExists(name);
		c.incServiced();
	}
	
	public void incFail(String name, long usedNanoSec){
		MethodTaskCounter c = this.besueExists(name);
		c.incFailServiced(usedNanoSec);
	}
	
	public void run(){
		int precision = 400;
		int itv = precision / 10;
		while(true){
			try {
				Thread.sleep(itv);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long now = System.currentTimeMillis();
			long mod = now % 1000;
			if(mod < precision){
				reset();
				try {
					sleep(precision + 1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	void reset(){
		long nowSeconds = System.currentTimeMillis() / 1000;
		for(MethodTaskCounter c : map.values()){
			c.reset(nowSeconds);
		}
	}

	public List<ConfigFile> getConfigs() {
		return configs;
	}

}
