package com.funshion.search.utils.systemWatcher;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;

public class RemoteCmdLine {
	/**
	 * @param args[0] config_path<br>
	 * @param args[1] cmd_value<br>
	 * if args[1] not set, check config, if still no cmd got,
	 * ask user enter cmd
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[]args) throws IOException {
		PropertyConfigurator.configureAndWatch(ConfUtils.getConfFile("log4j.properties").getAbsolutePath());
		File argFile = null;
		String cmd = null;
		for(String x : args){
			if(x.startsWith("-f")){
				File f = new File(x.substring(2));
				if(!f.exists()){
					System.out.println("argFile is not exists:" + f.getCanonicalFile().getAbsolutePath());
				}else if(f.isDirectory()){
					System.out.println("argFile is Directory:" + f.getCanonicalFile().getAbsolutePath());
				}else if(!f.canRead()){
					System.out.println("can not read argFile:" + f.getCanonicalFile().getAbsolutePath());
				}else{
					argFile = f;
				}
			}else if(x.startsWith("-c")){
				cmd = x.substring(2);
			}
		}
		try {
			executeCmd(argFile, cmd);
		} catch (Exception e) {
			System.out.println("ERROR:" + e);
		}
	}
	static void executeCmd(File argFile, String cmd) throws IOException, TException {
		String addr = null;
		int port = 0;
		File conf;
		if(argFile != null) {
			conf= argFile;
		}else {
			conf= ConfUtils.getConfFile("watcher/remoteInfo.cfg");
		}
		ConfigReader cr = null;

		if(!conf.exists() || conf.isDirectory() || !conf.canRead()) {
			LogHelper.log.warn("not found config %s", conf);
		}else {
			cr = new ConfigReader(conf, "main");
		}

		if(cr != null) {
			addr = cr.getValue("ip");
		}

		if(addr == null) {
			addr = Consoler.readString("remote host(no input as 127.0.0.1):");
			if(addr.length() == 0) {
				addr = "127.0.0.1";
			}
		}
		if(cr != null) {
			port = cr.getInt("port");
		}
		while(true) {
			if(port != 0) {
				break;
			}
			port = Consoler.readInt("port : ", 0);
		}

		if(cmd == null && cr != null) {
			cmd = cr.getValue("cmd");
		}
		String cmdLine = cmd;
		final boolean interactiveModel = cmd == null;
		while(true){
			if(cmd == null) {
				cmdLine = Consoler.readString("cmd(^ as CR):");
			}
			cmdLine = cmdLine.trim();
			if(cmdLine.length() == 0) {
				System.out.println("no CMD to execute!");
				return;
			}
			MessageClient clt = new MessageClient(addr, 
					port);

			//System.out.println("connected!");
			AnswerMessage ret = clt.queryMsg(cmdLine.split("\\^"));
			System.out.println("<message start>");
			System.out.println("serverSeq : " + ret.serverSeq);
			System.out.println("answerStatus : " + ret.answerStatus);
			System.out.println("actionStatus : " + ret.actionStatus);
			System.out.println("messageBody  :");
			if(ret.answerBody != null){
				int idx = 0;
				for(String x : ret.answerBody){
					System.out.println("LINE" + (++idx) + ":'" + x +"'");
				}
			}

			System.out.println("<message end>");
			System.out.println();

			if(!interactiveModel){//if cmd is not set, 
				break;
			}
		}
	}
}