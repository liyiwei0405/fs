package com.funshion.search.media.chgWatcher;

import java.io.IOException;

import com.funshion.search.ChgExportFS;
import com.funshion.search.ExportChgBootStrap;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
/**
 * @author liying
 */
public class MediaExportBootStrap extends Thread{
	public static void main(String[]args) throws IOException, Exception{//testing
		final ConfigReader cr = new ConfigReader(MediaFactory.configFile, "main");
		final int daemonPort = cr.getInt("daemonPort");
		final int chgTransPort = cr.getInt("chgTransPort");
		final ChgExportFS fs = new ChgExportFS(true);
		final MediaExportHelper helper = new MediaExportHelper(cr, fs);
		ExportChgBootStrap.startExportDaemon(helper, daemonPort, chgTransPort);
		LogHelper.log.log("daemonPort is %s, chgTransPort is %s", daemonPort, chgTransPort);	
	}
}
