package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;

public class MysqlHelper {
	private String driver = "com.mysql.jdbc.Driver";
	private Connection conn = null;
	private LogHelper log = new LogHelper("mysql");

	public MysqlHelper() throws IOException{
		ConfigReader cr = new ConfigReader(ConfUtils.getConfFile("searchHint.conf"), "mysql");
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(cr.getValue("url"), cr.getValue("user"), cr.getValue("password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MysqlHelper(ConfigReader cr) throws IOException{
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(cr.getValue("url"), cr.getValue("user"), cr.getValue("password"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MysqlHelper(String url, String user, String password) throws IOException{
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
