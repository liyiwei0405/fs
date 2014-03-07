package com.funshion.ucs;

import java.io.IOException;

import com.funshion.search.ChgExportFS;
import com.funshion.search.DatumFile;
import com.funshion.search.ExportChgHelper;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.MysqlHelper;
import com.funshion.ucs.DAO.Area;
import com.funshion.ucs.DAO.AreaUserClass;
import com.funshion.ucs.DAO.IpSegments;
import com.funshion.ucs.DAO.Mac;
import com.funshion.ucs.DAO.UserClass;

public class UCSUpdateHelper extends ExportChgHelper {
	private static final LogHelper log = new LogHelper("UCSUpdateHelper");
	private static boolean Ready = false;
	private final ConfigReader mysqlCr;
	private final long totalIntervalSec;
	private final long updateIntervalSec;

	public UCSUpdateHelper(ConfigReader cr, ChgExportFS fs) throws IOException{
		super(cr, fs);
		this.mysqlCr = Func.getMysqlCr();
		this.totalIntervalSec = cr.getInt("totalIntervalSec", 24 * 3600);
		this.updateIntervalSec = cr.getInt("updateIntervalSec", 10 * 1000);
		log.warn("totalIntervalSec %smin, updateIntervalSec %smin", totalIntervalSec/60, updateIntervalSec/60);
	}

	public static boolean isReady(){
		return Ready;
	}

	@Override
	public void doExport(boolean totalExport, DatumFile dFile) throws Exception{
		MysqlHelper mysql = null;
		try{
			mysql = new MysqlHelper(this.mysqlCr);
			Area.instance.loadArea(mysql);
			Thread.sleep(100);
			
			UserClass.instance.loadUserClasses(mysql);
			Thread.sleep(100);
			
			AreaUserClass.instance.loadAreaUserClasses(mysql);
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

	@Override
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

	@Override
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
