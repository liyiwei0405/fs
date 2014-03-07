package com.funshion.search;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;

public abstract class ExportChgHelper extends Thread{
	public DatumFile dFile;
	protected final ConfigReader cr;
	protected final ChgExportFS fs;
	protected LogHelper log = new LogHelper("chgExp");
	protected long lastTotalExportTime;
	protected long lastUpdateExportTime = 0;
	protected boolean makeATotalUpdate = false;
	final int checkItvMillionSeconds;
	public ExportChgHelper(ConfigReader cr, ChgExportFS fs) throws IOException{
		this.cr = cr;
		this.fs = fs;
		this.checkItvMillionSeconds = cr.getInt("checkItvMillionSeconds", 10000);
		//FIXME default value too small
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
	
	public abstract void doExport(boolean totalExport, DatumFile dFile) throws Exception;
	public final void run(){
		while(true){
			if(needTotalExport() || makeATotalUpdate || !fs.currentDatumDirValid()){
				//FIXME check totalExport Itv
				makeATotalUpdate = false;
				log.info("doing total-export ....");
				try{
					File iFile = fs.getNewSeqFile(true);
					this.dFile = new DatumFile(iFile, true);
					doExport(true, this.dFile);
				}catch(Exception e){
					log.error(e, "total export error!");
					e.printStackTrace();
				}finally{
					this.lastTotalExportTime = System.currentTimeMillis();
					this.lastUpdateExportTime = this.lastTotalExportTime;
				}
			}else if(needUpdate()){
				log.info("doing update-export ....");
				try{
					File iFile = fs.getNewSeqFile(false);
					DatumFile targetDfile = new DatumFile(iFile);
					doExport(false, targetDfile);
				}catch(Exception e){
					log.error(e, "update export error!");
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
