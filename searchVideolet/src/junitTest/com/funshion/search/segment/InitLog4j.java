package com.funshion.search.segment;

import org.apache.log4j.PropertyConfigurator;

public class InitLog4j {

	public static void init(){
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
	}
}
