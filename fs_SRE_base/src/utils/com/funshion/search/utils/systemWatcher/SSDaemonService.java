package com.funshion.search.utils.systemWatcher;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.transport.TTransportException;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.TchFileTool;
/**
 * the sub-class works as:<br>
 * init service<br>
 * {@link #work()} implements your work here <br>
 * {@link #block()} thread be locked
 */
public abstract class SSDaemonService {
	public final SystemWatcher watcher;
	final String envName;
	public final LogHelper logd;
	public static final String envPropName = "fsDamonService_env";
	public SSDaemonService(int port, String sysName, 
			String log4jConfigPath, boolean asHeartBeatServer) throws IOException, TTransportException { 
		PropertyConfigurator.configureAndWatch(log4jConfigPath);
		logd = new LogHelper(sysName);
		watcher = SystemWatcher.regWatcher(port, sysName + "", asHeartBeatServer);
		String envNameStr = TchFileTool.get("config", "envName");
		this.envName = envNameStr == null ? "unknowEnv_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) : envNameStr;
		logd.info("envName is %s", this.envName);
		System.setProperty(envPropName, envName);
	}
	public String getEnv(){
		return System.getProperty(envPropName);
	}

	public SSDaemonService(int port,
			String sysName, boolean asHeartBeatServer) throws IOException, TTransportException {
		this(port, sysName, ConfUtils.getConfFile("log4j.properties").getAbsolutePath(), asHeartBeatServer);
	}
	public SSDaemonService(int port, String sysName) throws IOException, TTransportException {
		this(port, sysName, ConfUtils.getConfFile("log4j.properties").getAbsolutePath(), false);
	}
	/**
	 * recommended method.
	 * equals:<code>
	 * <br> {@link #work(Object[])}; <br>{@link #block()}<br>
	 * </code>
	 * @param paras
	 * @throws Exception
	 */
	public void startDaemon(Object[]paras) throws Exception{
		startDaemon(paras, true);
	}
	public void startDaemon(Object[]paras, boolean block) throws Exception{
		work(paras);
		if(block){
			block();
		}
	}
	/**
	 * work thread.
	 * instead calling this method,
	 * it is recommended to use {@link #startDamon(Object[])} 
	 * @param paras
	 * @throws Exception
	 */
	protected abstract void work(Object[]paras)throws Exception ;

	public void block() {
		watcher.block();
		//unreachable code, protect toRun not be collected by GC
	}
}
