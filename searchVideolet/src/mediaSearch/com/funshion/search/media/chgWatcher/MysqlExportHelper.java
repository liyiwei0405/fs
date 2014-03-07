package com.funshion.search.media.chgWatcher;

import java.sql.*;

import java.util.List;
import java.util.Map;

import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;

public class MysqlExportHelper {
	LogHelper log = new LogHelper("export");
	private int exportNum = 0;
	final LineWriter writeTo;
	
	String driver = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://192.168.16.163:3306/corsair_0";
	String user = "root"; 
	String password = "123456";

	public MysqlExportHelper(LineWriter writeTo){
		this.writeTo = writeTo;
	}

	public void export(Map<Integer, List<Integer>> mediaToVideolistMap) throws Exception{
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, password);

			if(!conn.isClosed()) 
				System.out.println("Succeeded connecting to the Database!");

			Statement statement = conn.createStatement();

			String sql = "SELECT a.mediaid, a.name_cn, a.name_en, a.name_ot, a.name_sn, a.displaytype, a.imagefilepath, a.coverpicid, a.plots, a.country, a.releasedate, a.releaseinfo, a.tag4editor, a.tactics, a.isplay, a.webplay, a.supporttype, a.ordering, crc32(a.issue) as issue, 0 as deleted, a.ta_0, a.ta_1, a.ta_2, a.ta_3, a.ta_4, a.ta_5, a.ta_6, a.ta_7, a.ta_8, a.ta_9, a.copyright, b.playnum, b.playafternum, b.karma, b.votenum, b.wantseenum, a.program_type " +
					"FROM fs_media a LEFT JOIN fs_media_stat b ON a.mediaid = b.mediaid";

			ResultSet rs = statement.executeQuery(sql);

			collect(mediaToVideolistMap, rs);

			rs.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}

	private void collect(Map<Integer, List<Integer>> mediaToVideolistMap, ResultSet rs) throws Exception{
		while(rs.next()){
			MediaRecord record = null;
			try {
				record = MediaFactory.getRecord(mediaToVideolistMap, rs);
			} catch (Exception e) {
				log.warn("skip record! becouse " + e);
				e.printStackTrace();
				continue;
			}
			record.flushTo(writeTo);
			writeTo.append('\n');
			this.exportNum++;
		}
		rs.close();
	}
	
	public int getExportNum() {
		return exportNum;
	}

}
