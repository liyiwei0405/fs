package com.funshion.search.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlHelper {
	private String driver = "com.mysql.jdbc.Driver";
	private Connection conn = null;
	private LogHelper log = new LogHelper("mysql");

	public MysqlHelper(ConfigReader cr) throws Exception{
		Class.forName(driver);
		conn = DriverManager.getConnection(cr.getValue("url"), cr.getValue("user"), cr.getValue("password"));
	}

	public MysqlHelper(String url, String user, String password) throws Exception{
		Class.forName(driver);
		conn = DriverManager.getConnection(url, user, password);
	}

	public ResultSet getCursor(String sql){
		ResultSet rs = null;
		try{
			Statement statement = conn.createStatement();
			rs = statement.executeQuery(sql);
		}catch(Exception e){
			log.error(e.getMessage());
		}
		return rs;
	}

	public void close(){
		if(conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("close connection to mysql failed : " + e.getMessage());
			}
	}

}
