package test;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class MongoHelper {
	private static final LogHelper log = new LogHelper("MongoHelper");
	private MongoClient mongoClient;
	private DB db;
	
	public DBCollection col;

	public MongoHelper(String ip, int port, String database, String table) throws Exception{
		this.mongoClient = new MongoClient(ip, port);
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