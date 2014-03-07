package com.funshion.search;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.systemWatcher.SSDaemonService;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;
/**
 * @author liying
 */
public class ChanneledExportChgBootStrap{
	public static void startExportDaemon(final ChanneledExportChgHelper helper, final int daemonPort) throws Exception {
		LogHelper.log.log("daemonPort is %s", daemonPort);
		
		SSDaemonService ssd = new SSDaemonService(daemonPort, "media-daemon", false){
			@Override
			protected void work(Object[] paras) throws Exception {
				helper.start();
				this.watcher.regAction(new WatcherAction("doTotalExport"){

					@Override
					public AnswerMessage run(QueryMessage message)
							throws Exception {
						logd.info("ask for totalExport from %s:%s", this.remoteIp, this.remotePort);
						helper.setMakeATotalUpdate();
						AnswerMessage am = WatcherAction.answerTemplate();
						return am;
					}
				});
				logd.info("deamon thread started!");
			}
		};
		ssd.startDaemon(null, false);
	}

}
