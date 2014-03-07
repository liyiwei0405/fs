package com.funshion.search.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogHelper {
	static{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
	}
	public static final LogHelper log = new LogHelper("log");
	final Logger logger;
	public final String logName;
	private static boolean printException = false;
	public LogHelper(String logName){
		this.logName = logName;
		logger = Logger.getLogger(logName);
	}
	public void log(String why, Object...args){
		info(why, args);
	}

	/**
	 * add a logInfo at level<code>lev</code>
	 * @param who
	 * @param why
	 * @param lev
	 */
	public void info(String why, Object...args){
		if(logger.isInfoEnabled()){
			logger.info(fmt(why, args));
		}
	}

	public void debug(String why, Object...args){
		if(logger.isDebugEnabled()){
			logger.debug(fmt(why, args));
		}
	}
	public void warn(String why, Object...args){
		logger.warn(fmt(why, args));
	}
	public void error(String why, Object...args){
		logger.error(fmt(why, args));
	}
	public void warn(Throwable e, String why, Object...args){
		logger.warn(fmt(why, args) + ", Exception = " + e);
	}
	public void error(Throwable e, String why, Object...args){
		logger.error(fmt(why, args) + ", Exception = " + e);
	}

	public void fatal(Throwable e, String why, Object...args){
		logger.fatal(fmt(why, args) + ", Exception = " + e);
	}
	public void fatal(String why, Object...args){
		logger.fatal(fmt(why, args));
	}
	public static final String fmt(String why, Object ...args){
		if(args == null || args.length == 0){
			return why;
		}
		try{
			return String.format(why, args);
		}catch(Exception e){
			e.printStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append(why);
			for(Object x : args){
				sb.append(", ");
				sb.append(x);
			}
			return sb.toString();
		}
	}

	public static boolean isPrintException() {
		return printException;
	}

	public static void setPrintException(boolean printException) {
		LogHelper.printException = printException;
	}
}
