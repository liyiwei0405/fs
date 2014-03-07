package com.funshion.search.utils.systemWatcher.innerAction;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.funshion.search.utils.systemWatcher.SystemWatcher;
import com.funshion.search.utils.systemWatcher.WatcherAction;
import com.funshion.search.utils.systemWatcher.message.AnswerMessage;
import com.funshion.search.utils.systemWatcher.message.QueryMessage;

public final class ShutDownAction extends WatcherAction{
	public class ShutdownHook implements Runnable{
		final Object synObject = new Object();
		boolean distroy = false;

		public ShutdownHook(){ }

		public void distroy(){
			synchronized(synObject){
				distroy = true;
				synObject.notifyAll();
			}
			synchronized(canBlockStop){
				stopHookQueue.remove(this);
			}
		}
		@Override
		public void run() {
			try {
				SystemWatcher.wLog.warn("waiting for stop hook signal, I'm %s",
						this);
				synchronized (synObject) {
					if(distroy){
						return;
					}
					synObject.wait();
				}
				SystemWatcher.wLog.warn("finishing stopping, I'm %s",
						this);
			} catch (Throwable e) {
				SystemWatcher.wLog.error(e,
						"stop signal got or stop timeoutted, I'm %s",
						this);
			}
		}
	}

	public static final String CMD = "forcestop";
	private AtomicBoolean canBlockStop = new AtomicBoolean(true);
	public ShutDownAction() {
		super(CMD);
	}

	LinkedList<ShutdownHook> stopHookQueue = new LinkedList<ShutdownHook>();
	public ShutdownHook regStopHook() {
		synchronized(canBlockStop){
			if(!canBlockStop.get()){
				return null;
			}
			ShutdownHook hook = new ShutdownHook();
			stopHookQueue.add(hook);
			return hook;
		}
	}
	@Override
	public AnswerMessage run(QueryMessage message) throws Exception {
		synchronized(canBlockStop){
			canBlockStop.set(false);

			AnswerMessage ret = super.answerTemplate();
			SystemWatcher.wLog.warn("stopping system by cmd from %s:%s", this.remoteIp, this.remotePort);

			for(ShutdownHook r : stopHookQueue) {
				if(!r.distroy) {
					SystemWatcher.wLog.warn("running stop hook %s", r);
					r.run();
				}else {
					SystemWatcher.wLog.warn("skip distroyed stop hook");
				}
			}

			Thread shutThread = new Thread(){
				public void run(){
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.exit(0);
				}
			};
			shutThread.start();
			ret.actionStatus = ACTION_STATUS_OK;
			return ret;
		}
	}
}