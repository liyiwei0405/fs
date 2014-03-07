package com.funshion.search;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;

public abstract class ChanneledExportChgHelper extends Thread{
	protected final ConfigReader cr;
	protected final IndexChannel channel;
	protected LogHelper log = new LogHelper("chgExp");
	protected long lastTotalExportTime;
	protected long lastUpdateExportTime = 0;
	protected boolean makeATotalUpdate = false;
	final int checkItvMillionSeconds;
	public ChanneledExportChgHelper(ConfigReader cr, IndexChannel channel) throws IOException{
		this.cr = cr;
		this.channel = channel;
		this.checkItvMillionSeconds = cr.getInt("checkItvMillionSeconds", 1000);
	}
	
	protected LineWriter newLineWriter(File tmpFile) throws IOException {
		return new LineWriter(tmpFile, false, Charset.forName("utf-8"));
	}
	
	public final void setMakeATotalUpdate(){
		if(!this.makeATotalUpdate){
			this.makeATotalUpdate = true;
		}
	}
	protected abstract boolean needUpdate();
	protected abstract boolean needTotalExport();
	//FIXME change to two method
	public abstract void doExport(boolean totalExport, IndexableRecordQueue rChannel) throws Exception;
	protected boolean totalExportisReady = false;
	public final void run(){
		while(true){
			if(needTotalExport() || makeATotalUpdate){
				makeATotalUpdate = false;
				log.info("doing total-export ....");
				
				try{
					IndexableRecordQueue indexableQueue = channel.openChannel(true);
					doExport(true, indexableQueue);
					channel.rotateIndex(indexableQueue);
					totalExportisReady = true;
				}catch(Throwable e){
					log.error(e, "total export error!");
					e.printStackTrace();
				}finally{
					this.lastTotalExportTime = System.currentTimeMillis();
					this.lastUpdateExportTime = this.lastTotalExportTime;
				}
			}else if(needUpdate()){
				log.info("doing update-export ....");
				
				try{
					IndexableRecordQueue updateChannel = channel.openChannel(false);
					doExport(false, updateChannel);
				}catch(Throwable e){
					log.error(e, "update export error!");
					e.printStackTrace();
				}finally{
					this.lastUpdateExportTime = System.currentTimeMillis();
				}
			}
			try {
				sleep(checkItvMillionSeconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
