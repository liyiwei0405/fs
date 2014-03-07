//package com.funshion.videoService.test;
//
//import java.io.File;
//
//import org.apache.log4j.PropertyConfigurator;
//
//import com.funshion.search.ChgExportFS;
//import com.funshion.search.ConfUtils;
//import com.funshion.search.ExportChgBootStrap;
//import com.funshion.search.utils.ConfigReader;
//import com.funshion.search.utils.LogHelper;
//import com.funshion.videoService.VideoletExportHelper;
//
//public class TestVideoExport {
//
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		PropertyConfigurator.configureAndWatch(ConfUtils.getConfFile("log4j.properties").getAbsoluteFile().toString());
//		File cfgFile = ConfUtils.getConfFile("videoService.conf");
//		
//		ConfigReader cr = new ConfigReader(cfgFile, "video-service");
//		final int chgWatcherDaemonPort = cr.getInt("chgWatcherDaemonPort");
//		final int chgWatcherTransPort = cr.getInt("chgWatcherTransPort");
//		final ChgExportFS fs = new ChgExportFS(true, cr);
//		final VideoletExportHelper helper = new VideoletExportHelper(cr, fs);
//		ExportChgBootStrap.startExportDaemon(helper, chgWatcherDaemonPort, chgWatcherTransPort);
//		LogHelper.log.log("daemonPort is %s, chgTransPort is %s", chgWatcherDaemonPort, chgWatcherTransPort);	
//	}
//
//}
