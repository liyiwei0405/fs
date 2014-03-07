package com.funshion.search.media.chgWatcher;

import java.io.File;

import com.funshion.search.ConfUtils;

public class MediaFactory {
	// FIXME
	public static final File configFile = ConfUtils.getConfFile("MediaExport/export.conf");
	public static final File mongoClientConfigFile = ConfUtils.getConfFile( "MediaExport/mongo.rdf");
	public static final File mysqlClientConfigFile = ConfUtils.getConfFile("MediaExport/mysql.rdf");
	
}
