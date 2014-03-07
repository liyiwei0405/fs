package com.funshion.search.videolet.dataCollector;

import java.io.File;

import com.funshion.search.utils.ConfigReader;
import com.mongodb.DBObject;

public class VideoletFactory {
	public static final File configFile = new File("./config/collector.conf");
	
	public static VideoletRecord getRecord(DBObject cursor){
		return new VideoletRecord(cursor);
	}

	public static ConfigReader getConfig() {
		return new ConfigReader(configFile, "fs_video");
	}
}
