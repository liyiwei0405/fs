package com.funshion.search.videolet.dataCollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.FileBasedLock;
import com.funshion.search.utils.LineWriter;
/**
 * @author liying
 *
 */
public class VideoletAllExportBootStrap extends Thread{
	static String sphinxIp;
	static int sphinxPort;
	public void del(ArrayList<Integer>ids) throws SphinxException{
		SphinxClient clt = new SphinxClient();
		long[][]var = new long[ids.size()][];
		for(int x = 0; x < var.length; x ++){
			var[x] = new long[2];
			var[x][0] = ids.get(x);
			var[x][1] = 1;
		}
		clt.setServer (sphinxIp, sphinxPort);
		int retVideoLet = clt.updateAttributes("videolet", new String[]{VideoletRecord.FIELD_IS_DEL}, var);
		log.warn("del from videolet ret " + retVideoLet);
	}

	final Charset charset = Charset.forName("utf-8");
	final int rotateItvSeconds;
	final int checkItvMillionSeconds;
	final int updateItvSeconds;
	private final File updateExportXmlFile;
	private final File totalExportXmlFile;
	private final File exeTotalFile;
	private final File exeUpdateFile;

	final int rotateAtHour;
	private long lastTotalExportTime;
	private long lastUpdateExportTime = 0;
	Logger log = Logger.getLogger("strap");
	private long maxModifyTime;

	private VideoletAllExportBootStrap() throws IOException{
		ConfigReader cr = new ConfigReader(VideoletFactory.configFile, "main");
		sphinxIp = cr.getValue("sphinxIp", "127.0.0.1");
		sphinxPort = cr.getInt("sphinxPort", 3618);

		this.rotateItvSeconds = cr.getInt("rotateItvSeconds", 3600);
		this.checkItvMillionSeconds = cr.getInt("checkItvMillionSeconds", 100);
		this.updateItvSeconds = cr.getInt("updateItvSeconds", 10);
		this.exeTotalFile = new File(cr.getValue("exeTotalFile", "unknown-no-default.desc"));
		if(!exeTotalFile.exists() || !exeTotalFile.canRead() || !exeTotalFile.canExecute()){
			throw new IOException("exetotalFile is not exists or can not read or can not be executed, file is:" + exeTotalFile.getAbsolutePath());
		}

		this.exeUpdateFile = new File(cr.getValue("exeUpdateFile", "unknown-no-default.desc"));
		if(!exeUpdateFile.exists() || !exeUpdateFile.canRead() || !exeUpdateFile.canExecute()){
			throw new IOException("exeUpdateFile  is not exists or can not read or can not be executed, file is:" + exeUpdateFile.getAbsolutePath());
		}

		totalExportXmlFile = new File(cr.getValue("totalXmlFile", "unknown-no-default.desc"));
		if(!totalExportXmlFile.getParentFile().exists()){
			if(!totalExportXmlFile.getParentFile().mkdirs()){
				throw new IOException("can not create par file or par file can not read ");
			}
		}
		updateExportXmlFile = new File(cr.getValue("updateXmlFile", "unknown-no-default.desc"));
		if(!updateExportXmlFile.getParentFile().exists()){
			if(!updateExportXmlFile.getParentFile().mkdirs()){
				throw new IOException();
			}
		}

		rotateAtHour = cr.getInt("rotateAtHour", -1);
		if(rotateAtHour > -1){
			log.warn("rotate at hour " + this.rotateAtHour + ", and rotateItvSeconds disabled");
		}
	}
	public void run(){
		while(true){
			if(needTotalExport()){
				log.info("doing total-export ....");
				try{
					doExport(true);
				}catch(Exception e){
					log.error("total export error!", e);
				}finally{
					this.lastTotalExportTime = System.currentTimeMillis();
					this.lastUpdateExportTime = this.lastTotalExportTime;
				}
			}else if(needUpdate()){
				log.info("doing update-export ....");
				try{
					doExport(false);
				}catch(Exception e){
					log.error("update export error!", e);
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
	public void doExport(boolean totalExport) throws Exception{

		int tot = 0;
		final File xmlFile;
		if(totalExport){
			xmlFile = totalExportXmlFile;
		}else{
			xmlFile = updateExportXmlFile;
		}
		File tmpFile = prepareTmpFile(xmlFile);

		LineWriter lw = new LineWriter(tmpFile, false, charset);
		try{
			lw.writeLine(VideoletRecord.XML_DOCSET_HEAD);
			ArrayList<Integer>toDel = new ArrayList<Integer>();
			//TODO merge fragment below into a method?
			long lastTime = 0;
			if(!totalExport){
				lastTime = this.maxModifyTime;
			}
			VideoletExportor vExp = new VideoletExportor(lastTime, lw);
			long st = System.currentTimeMillis();
			vExp.export();
			if(vExp.lstToDel != null){
				toDel.addAll(vExp.lstToDel);
			}
			tot += vExp.getExportNum();
			long ed = System.currentTimeMillis();	
			log.info("dump Video use ms " + (ed - st));


			lw.write(VideoletRecord.XML_DOCSET_TAIL);
			lw.close();
			if(tot > 0){
				if(!tmpFile.renameTo(xmlFile)){
					log.error("can not rename tmp file to " + xmlFile);
					//FIXME what should we do? exit or only throw a exception?
					throw new Exception("we can not rename tmpFile '"+ tmpFile.getCanonicalPath() + "'  to '" + xmlFile.getCanonicalFile() + "'");
				}
				log.info("xml gen ok: " + xmlFile);


				File exeFile;
				if(totalExport){
					exeFile = exeTotalFile;
				}else{
					exeFile = exeUpdateFile;
				}
				Process proc = Runtime.getRuntime().exec(
						exeFile.getCanonicalFile().getAbsolutePath());
				BufferedReader read = new BufferedReader(new InputStreamReader(
						proc.getInputStream()));
				try {
					proc.waitFor();
					while (read.ready()) {//TODO where to write?
						System.out.println(read.readLine());
					}
					//TODO should we check shell's output?
					if(totalExport){
						this.maxModifyTime = vExp.getMaxModifyValue();
					}
					//first try to delete old ids, the logic may change with totalExport FIXME
					if(toDel.size() > 0){
						log.warn("try to delete ids " + toDel);
						try{
							del(toDel);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				} catch (InterruptedException e) {
					log.error("when wait shell exit", e);
					throw e;
				}

			}else{
				log.warn("export 0 records! for " + (totalExport ? "totalExport" : "updateExport"));
			}

		}finally{
			if(lw != null){
				lw.close();
			}
		}

	}
	private boolean needTotalExport() {
		if(lastTotalExportTime == 0){//do total-index at startup
			return true;
		}else{
			if(rotateAtHour > -1){//at this hour to all-export
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				if(hour == this.rotateAtHour){
					long hasPassedSeconds = (System.currentTimeMillis() - lastTotalExportTime) / 1000 ;
					if(hasPassedSeconds > 600){
						return true;
					}
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
	private boolean needUpdate() {
		long passedSeconds = (System.currentTimeMillis() - this.lastUpdateExportTime) / 1000;
		return passedSeconds > this.updateItvSeconds;
	}


	static File prepareTmpFile(File xmlFile) throws Exception{
		if(xmlFile.isDirectory()){
			throw new Exception("is a directory! " + xmlFile);
		}
		File parFile = xmlFile.getParentFile();

		//check if par can write
		if(!parFile.exists()){
			if(!parFile.mkdirs()){
				throw new Exception("can not create par file " + parFile);
			}
		}else{
			if(!parFile.canWrite()){
				throw new Exception("can not write par file :" + parFile);

			}
		}
		if(xmlFile.exists()){//bak the old file to name-bak
			File bakFile = new File(parFile, xmlFile.getName() + "-bak");
			if(bakFile.exists()){
				bakFile.delete();
			}
			if(!xmlFile.renameTo(bakFile)){
				if(xmlFile.delete()){
					throw new Exception("can not delete old " + xmlFile);
				}

			}
		}
		File tmpFile = new File(parFile, xmlFile.getName() + "-tmp");
		if(tmpFile.exists()){
			if(!tmpFile.delete()){
				throw new Exception("can not delete old tmp file " + tmpFile);
			}
		}
		return tmpFile;
	}

	public static void main(String[]args) throws IOException{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
		Logger log = Logger.getLogger("main");

		log.info("check if there is other instance running...");

		FileBasedLock fbl = null;
		try{
			fbl = new FileBasedLock("exportor.lck");
			fbl.tryLock();
		}catch(Exception e){
			log.error("can not lock lockFile " + fbl.getFile() + ", be sure I can write at this dir, and there is no other instance running");
			//we can not lock a file, maybe another instance is running
			System.exit(0);
		}
		log.info("there is no other instances running, start loading datas...");
		VideoletAllExportBootStrap bs = new VideoletAllExportBootStrap();
		bs.start();
		log.info("deamon thread started!");
		fbl.block();
	}
}
