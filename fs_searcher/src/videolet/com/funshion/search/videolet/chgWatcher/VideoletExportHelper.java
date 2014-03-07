package com.funshion.search.videolet.chgWatcher;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.funshion.search.DatumFile;
import com.funshion.search.ChgExportFS;
import com.funshion.search.ExportChgHelper;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
/**
 * @author liying
 */
public class VideoletExportHelper extends ExportChgHelper{
	
	final int rotateItvSeconds;
	final int updateItvSeconds;

	final int rotateAtHour;
	private int totalExpDayOfyear = -1;
	private long maxModify_UGC;
	private long maxModify_microVideo;

	final boolean loadUGC;
	final boolean loadMicroVideo;
	VideoletExportHelper(ConfigReader cr, ChgExportFS fs) throws IOException{
		super(cr, fs);
		this.rotateItvSeconds = cr.getInt("rotateItvSeconds", 3600);
		this.updateItvSeconds = cr.getInt("updateItvSeconds", 10);

		//rotateAtHour, if valid value is set, the indexes will be total rotate at this hour, 
		//if valid value is set, rotateItvSeconds will be overrided
		rotateAtHour = cr.getInt("rotateAtHour", -1);
		if(rotateAtHour > -1){
			log.warn("rotate at hour %s, and rotateItvSeconds disabled", this.rotateAtHour);
		}
		loadUGC = cr.getInt("loadUGC", 1) == 1;
		if(!loadUGC){
			log.warn("UGC load is forbidden");
		}
		loadMicroVideo = cr.getInt("loadMicroVideo", 1) == 1;
		if(!loadMicroVideo){
			log.warn("loadMicroVideo load is forbidden");
		}
		log.warn("modules load info: loadUGC = %s, loadMicroVideo=%s", loadUGC, loadMicroVideo);
	}
	
	public void doExport(boolean totalExport, DatumFile dFile) throws Exception{
		long maxModify_UGC_new = 0;
		long maxModify_microVideo_new = 0;

		int tot = 0;
		File tmpFile = fs.prepareTmpFile();

		LineWriter lw = newLineWriter(tmpFile);
		try{
			if(this.loadMicroVideo){//export microVideo
				long lastTime = 0;
				if(!totalExport){
					lastTime = this.maxModify_microVideo;
				}
				MongoExportHelper vExp = new MongoExportHelper(lastTime, VideoletFactory.MICRO_VIDEO, lw);
				long st = System.currentTimeMillis();
				vExp.export();
				tot += vExp.getExportNum();
				long ed = System.currentTimeMillis();	
				log.info("export Video use ms %s", (ed - st));
				maxModify_microVideo_new = vExp.getMaxModifyValue();
			}
			if(this.loadUGC){//export UGC
				long lastTime = 0;
				if(!totalExport){
					lastTime = this.maxModify_UGC;
				}
				MongoExportHelper vExp = new MongoExportHelper(lastTime, VideoletFactory.UGC, lw);
				long st = System.currentTimeMillis();
				vExp.export();
				tot += vExp.getExportNum();
				long ed = System.currentTimeMillis();	
				log.info("export ugc use ms %s", (ed - st));
				maxModify_UGC_new = vExp.getMaxModifyValue();
			}
			lw.close();
			if(tot > 0){
				if(!tmpFile.renameTo(dFile.file)){
					log.error("can not rename tmp file to %s", dFile.file);
					throw new Exception("we can not rename tmpFile '"+ tmpFile.getCanonicalPath() + "'  to '" + dFile.file.getCanonicalFile() + "'");
				}
				dFile.md5Bytes();
				log.info("new chg gen ok: %s", dFile);
				if(dFile.isMain){
					fs.switchChgDir(dFile);
				}else{
					fs.putDatumFile(dFile);
				}
				if(this.loadMicroVideo){//export microVideo
					this.maxModify_microVideo = maxModify_microVideo_new;
				}
				if(this.loadUGC){//export UGC
					this.maxModify_UGC = maxModify_UGC_new;
				}
			}else{
				tmpFile.delete();
				log.info("export 0 records! for %s", (totalExport ? "totalExport" : "updateExport"));
			}

		}finally{
			if(lw != null){
				lw.close();
			}
		}
	}

	protected boolean needTotalExport() {
		if(lastTotalExportTime == 0){//do total-index at startup
			return true;
		}else{
			if(rotateAtHour > -1){//at this hour to all-export
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int day = c.get(Calendar.DAY_OF_YEAR);
				if(hour == this.rotateAtHour && (totalExpDayOfyear != day)){
					totalExpDayOfyear = day;
					return true;
				}
			}else{//set not daily total-rotate, then check rotate inteval
				long hasPassedSeconds = (System.currentTimeMillis()  - lastTotalExportTime) / 1000;
				if(hasPassedSeconds > this.rotateItvSeconds){
					return true;
				}
			}
			return false;
		}
	}
	protected boolean needUpdate() {
		long passedSeconds = (System.currentTimeMillis() - this.lastUpdateExportTime) / 1000;
		return passedSeconds > this.updateItvSeconds;
	}

}
