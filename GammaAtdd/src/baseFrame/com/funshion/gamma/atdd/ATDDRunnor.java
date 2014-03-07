package com.funshion.gamma.atdd;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.systemWatcher.SSDaemonService;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public class ATDDRunnor extends SSDaemonService{
	final AtddConfig cfg;

	public ATDDRunnor(ConfigReader cr)
			throws Exception {
		super(cr.getInt("daemonPort", 60666), "ATDDRunnor");
		
		cfg = new AtddConfig(cr);
		WatcherAction getNewestStatusAction = new WatcherAction("ns"){

			@Override
			public AnswerMessage run(QueryMessage message) throws Exception {
				AnswerMessage am = answerTemplate();
				int para = message.messageBody.size();
				int lineNums = 20;
				String prmt = null;
				if(para > 0){
					try{
						lineNums = Integer.parseInt(message.messageBody.get(0));
						
					}catch(Exception e){
						prmt = e.toString();
						e.printStackTrace();
					}
				}
				if(prmt == null){
					prmt = "show lines:" + lineNums;
				}
				if(cfg.pHost == null){
					am.answerBody.add("not press Test? not found pressTest instance");
				}else{
					am.addToAnswerBody(prmt);
					am.answerBody.add(cfg.pHost.getSysInfo(lineNums));
				}
				return am;
			}
			
		};
		this.watcher.regAction(getNewestStatusAction);
	}
	@Override
	protected void work(Object[] paras) throws Exception {

		if(cfg.isForTestPress){
			cfg.pressTest();
		}else{
			cfg.functionalTest(true);
		}

	}
	
	public static void main(String[]args) throws Exception{
		ConfigReader cr = AtddConfig.getConfig(args);
		ATDDRunnor r = null;
		try{
			r = new ATDDRunnor(cr);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("boot error???? maybe another instance is running");
			System.exit(0);
		}
		r.startDaemon(null);
		
	}



}
