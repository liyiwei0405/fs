package com.funshion.search.videolet.chgWatcher;

import java.io.File;

import com.funshion.search.utils.ConfigReader;
import com.mongodb.DBObject;

public class VideoletFactory {
	public static final File configFile = new File("./config/VideoletExport/export.conf");
	public static final File mongoClientConfigFile = new File("./config/VideoletExport/mongo.rdf");
	public static final String UGC = "fs_video_ugc";
	public static final String MICRO_VIDEO = "fs_video";
	
	public static VideoletRecord getRecord(final String type, DBObject cursor){
		if(UGC.equalsIgnoreCase(type)){
			return new UGCVideoRecord(cursor);
		}else if(MICRO_VIDEO.equalsIgnoreCase(type)){
			return new MicroVideoRecord(cursor);
		}else{
			throw new RuntimeException("unknown videolet type " + type);
		}
	}

	public static ConfigReader getMongoClientConfig(String type) {
		return new ConfigReader(mongoClientConfigFile, type);
	}
}
