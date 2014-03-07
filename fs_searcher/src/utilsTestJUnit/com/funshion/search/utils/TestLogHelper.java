package com.funshion.search.utils;

import org.apache.log4j.PropertyConfigurator;

import com.funshion.search.ConfigUtils;

public class TestLogHelper {

	public static void main(String[] args) {
		PropertyConfigurator.configureAndWatch(ConfigUtils.getBaseConfigFile("log4j.properties"));
		Thread threads[] = new Thread[200];
		LogHelper lh = null;
		for(int x = 0; x < threads.length; x ++){
			final LogHelper log;
			if(lh != null && Math.random() > 0.5){
				log = lh;
			}else{
				int mod = x / 5;
				log = new LogHelper("log" + mod);
				
			}
			threads[x] = new Thread(){
				public void run(){
					Exception ex = new RuntimeException(log.logName + "' exception");
					while(true){
						log.debug("sdfad %s", "debug");
						log.info("sdfad %s", "debug");
						log.warn("sdfad %s", "warn");
						log.error("sdfad %s", "error");
						log.fatal("sdfad %s", "fatal");
						;
						log.warn(ex, "sdfad %s", "warn");
						log.error(ex, "sdfad %s", "error");
						log.fatal(ex, "sdfad %s", "fatal");
					}
				}
			};
		}
		for(Thread t : threads){
			t.start();
		}
	}
}
