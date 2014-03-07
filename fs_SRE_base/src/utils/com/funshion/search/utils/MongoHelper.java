package com.funshion.search.utils;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class MongoHelper {
	private static final LogHelper log = new LogHelper("MongoHelper");
	private MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
	private MongoClient mongoClient;
	private DB db;
	
	public DBCollection col;

	public MongoHelper(String ip, int port, String database, String table) throws Exception{
		this.builder.socketTimeout(3000000);
		MongoClientOptions options = builder.build();
		this.mongoClient = new MongoClient(new ServerAddress(ip, port), options);
		this.db = mongoClient.getDB(database);
		this.col = db.getCollection(table);
		log.warn("mongo connect to %s, %s, %s, %s", ip, port, database, table);
	}
	
	public MongoHelper(ConfigReader configReader) throws Exception {
		this(configReader.getValue("ip"), configReader.getInt("port"), configReader.getValue("database"), configReader.getValue("table"));
	}

	public void close(){
		this.mongoClient.close();
	}
}