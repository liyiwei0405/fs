package com.funshion.ucs;

import java.io.IOException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.MysqlHelper;
import com.funshion.ucs.DAO.Area;
import com.funshion.ucs.DAO.AreaUserClass;
import com.funshion.ucs.DAO.IpSegments;
import com.funshion.ucs.DAO.Mac;
import com.funshion.ucs.DAO.UserClass;

public class UCSUpdateHelper extends Thread {
	private static final LogHelper log = new LogHelper("UCSUpdateHelper");
	private static boolean Ready = false;
	
	private final long totalIntervalSec;
	private final long updateIntervalSec;
	private final int checkItvMillionSeconds;
	private final ConfigReader mysqlCr;

	private long lastTotalExportTime = 0;
	private long lastUpdateExportTime = 0;


	public UCSUpdateHelper(ConfigReader cr) throws IOException{
		this.mysqlCr = Func.getMysqlCr();
		this.checkItvMillionSeconds = cr.getInt("checkItvMillionSeconds", 1000);
		this.totalIntervalSec = cr.getInt("totalIntervalSec", 24 * 3600);
		this.updateIntervalSec = cr.getInt("updateIntervalSec", 10 * 1000);
		log.warn("totalIntervalSec %smin, updateIntervalSec %smin", totalIntervalSec/60, updateIntervalSec/60);
	}

	public static boolean isReady(){
		return Ready;
	}
	
	public final void run(){
		IpLocation.instance.loadIPData(false);
		IpLocation.instance.loadIPData(true);
		while(true){
			if(needTotalExport()){
				log.info("doing total-export ....");
				try{
					doExport(true);
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
					doExport(false);
				}catch(Exception e){
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

	public void doExport(boolean totalExport) throws Exception{
		MysqlHelper mysql = null;
		try{
			mysql = new MysqlHelper(this.mysqlCr);
			Area.loadArea(mysql);
			Thread.sleep(100);

			UserClass.loadUserClasses(mysql);
			Thread.sleep(100);

			AreaUserClass.loadAreaUserClasses(mysql);
			Thread.sleep(100);

			Mac.instance.loadMACs(mysql);
			Thread.sleep(100);

			IpSegments.instance.loadIpSegments(mysql);
			Ready = true;
		}finally{
			if(mysql != null){
				mysql.close();
			}
		}
	}

	protected boolean needTotalExport() {
		if(lastTotalExportTime == 0){//do total export at startup
			return true;
		}else{
			long hasPassedSeconds = (System.currentTimeMillis()  - this.lastTotalExportTime) / 1000;
			if(hasPassedSeconds > this.totalIntervalSec){
				return true;
			}
		}
		return false;
	}

	protected boolean needUpdate() {
		//		long hasPassedSeconds = (System.currentTimeMillis()  - this.lastUpdateExportTime) / 1000;
		//		if(hasPassedSeconds > this.updateIntervalSec){
		//			return true;
		//		}
		return false;
	}

	public static void main(String[] args) throws Exception{
		//		ConfigReader cr = new ConfigReader(ConfUtils.getConfFile("cfgLuc.conf"), "service");
	}

}
