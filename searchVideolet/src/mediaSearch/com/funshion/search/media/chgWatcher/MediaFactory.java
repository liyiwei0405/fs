package com.funshion.search.media.chgWatcher;

import java.io.File;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.funshion.search.utils.ConfigReader;

public class MediaFactory {
	public static final File configFile = new File("./config/VideoletExport/export.conf");
	public static final File mongoClientConfigFile = new File("./config/VideoletExport/mongo.rdf");
	
	public static MediaRecord getRecord(Map<Integer, List<Integer>> mediaToVideolistMap, ResultSet rs) throws Exception{
			return new MediaRecord(mediaToVideolistMap, rs);
	}

	public static ConfigReader getMongoClientConfig(String type) {
		return new ConfigReader(mongoClientConfigFile, type);
	}
}
