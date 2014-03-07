package com.funshion.search.media.chgWatcher;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;

public class MysqlExportHelper {
	private ConfigReader cr;
	LogHelper log = new LogHelper("export");
	private int exportNum = 0;
	final LineWriter writeTo;
	
	public Map<Integer, Set<Integer>> mediaToIshds = null;
	public Map<Integer, Set<Integer>> mediaToPreids = null;
	public Map<Integer, Set<Integer>> mediaToClassids = null;
	public Map<Integer, Set<String>> mediaToTacticsClients = null;
	public Map<Integer, Set<Integer>> mediaToIsdisplays = null;
	
	String driver = "com.mysql.jdbc.Driver";

	public MysqlExportHelper(LineWriter writeTo) throws IOException{
		this.cr = new ConfigReader(ConfUtils.getConfFile("MediaExport/mysql.rdf"), "fs_media");
		this.writeTo = writeTo;
	}

	public void export(Map<Integer, List<Integer>> mediaToRelatedVideos) throws Exception{
		log.warn("exporting");
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(cr.getValue("url"), cr.getValue("user"), cr.getValue("password"));

			Statement statement = conn.createStatement();

			String sql_fs_publish = "SELECT mediaid, ishd FROM fs_publish";
			ResultSet rs_fs_publish = statement.executeQuery(sql_fs_publish);
			this.mediaToIshds = genMap(rs_fs_publish, "ishd");
			
			String sql_fs_prevue = "SELECT mediaid, id FROM fs_media_prevue_info WHERE publishflag = 'published'";
			ResultSet rs_fs_prevue = statement.executeQuery(sql_fs_prevue);
			this.mediaToPreids = genMap(rs_fs_prevue, "id");
			
			String sql_fs_media_class_relation = "SELECT mediaid, media_classid FROM fs_media_class_relation";
			ResultSet rs_fs_media_class_relation = statement.executeQuery(sql_fs_media_class_relation);
			this.mediaToClassids = genMap(rs_fs_media_class_relation, "media_classid");
			
			String sql_fs_media_area_tactic_relation = "SELECT mediaid, tactic, client FROM fs_media_area_tactic_relation WHERE tactic = 0 UNION SELECT a.mediaid, a.tactic, a.client FROM fs_media_area_tactic_relation a, fs_area_info b WHERE a.tactic = b.tactic AND b.isvalid > 0 AND b.tacticvalid > 0 AND a.client = b.client";
			ResultSet rs_fs_media_area_tactic_relation = statement.executeQuery(sql_fs_media_area_tactic_relation);
			this.mediaToTacticsClients = genAreaTacticMap(rs_fs_media_area_tactic_relation);
				
			String sql_fs_media_client = "SELECT mediaid, client, isplay, isdisplay FROM fs_media_client";
			ResultSet rs_fs_media_client = statement.executeQuery(sql_fs_media_client);
			this.mediaToIsdisplays = genIsdisplayMap(rs_fs_media_client);
			
			String sql_fsmedia = "SELECT a.mediaid, a.name_cn, a.name_en, a.name_ot, a.name_sn, a.displaytype, a.imagefilepath, a.coverpicid, a.plots, a.country, a.releasedate, a.releaseinfo, a.tag4editor, a.tactics, a.isplay, a.webplay, a.supporttype, a.ordering, a.issue, a.ta_0, a.ta_1, a.ta_2, a.ta_3, a.ta_4, a.ta_5, a.ta_6, a.ta_7, a.ta_8, a.ta_9, a.copyright, b.playnum, b.playafternum, b.karma, b.votenum, b.wantseenum, a.program_type " +
					"FROM fs_media a LEFT JOIN fs_media_stat b ON a.mediaid = b.mediaid WHERE a.deleted=0";
			ResultSet rs_fsmedia = statement.executeQuery(sql_fsmedia);

			collect(rs_fsmedia, mediaToRelatedVideos, this.mediaToIshds, this.mediaToPreids, this.mediaToClassids, this.mediaToTacticsClients, this.mediaToIsdisplays);
		} catch(Exception e) {
			log.error("mysql error: %s", e.getMessage());
			e.printStackTrace();
		} finally{
			if(conn != null)
				conn.close();
		}
	}
	
	private Map<Integer, Set<Integer>> genIsdisplayMap(ResultSet rs) throws Exception {
		Map<Integer, Set<Integer>> mediaToIsdisplays = new HashMap<Integer, Set<Integer>>();
		while(rs.next()){
			int mediaid = rs.getInt("mediaid");
			String client = rs.getString("client");
			if((rs.getInt("isplay") & rs.getInt("isdisplay")) > 0 && ! client.isEmpty()){
				Set<Integer> valueSet = mediaToIsdisplays.get(mediaid);
				if(valueSet == null){
					valueSet = new HashSet<Integer>();
					mediaToIsdisplays.put(mediaid, valueSet);
				}
				if(client.equals("pc")){
					valueSet.add(1);
				}else if(client.equals("winphone")){
					valueSet.add(2);
				}else if(client.equals("aphone")){
					valueSet.add(3);
				}else if(client.equals("iphone")){
					valueSet.add(4);
				}else if(client.equals("apad")){
					valueSet.add(5);
				}else if(client.equals("ipad")){
					valueSet.add(6);
				}else if(client.equals("mweb")){
					valueSet.add(7);
				}else if(client.equals("third_part")){
					valueSet.add(8);
				}else{
					log.error("unknown client type : %s", client);
				}
			}
		}
		return mediaToIsdisplays;
	}

	private Map<Integer, Set<Integer>> genMap(ResultSet rs, String valueName) throws Exception{
		Map<Integer, Set<Integer>> mediaMap = new HashMap<Integer, Set<Integer>>();
		while(rs.next()){
			int mediaId = rs.getInt("mediaid");
			Set<Integer> valueSet = mediaMap.get(mediaId);
			if(valueSet == null){
				valueSet = new HashSet<Integer>();
				mediaMap.put(mediaId, valueSet);
			}
			valueSet.add(rs.getInt(valueName));
		}
		return mediaMap;
	}
	
	private Map<Integer, Set<String>> genAreaTacticMap(ResultSet rs) throws Exception{
		Map<Integer, Set<String>> mediaMap = new HashMap<Integer, Set<String>>();
		while(rs.next()){
			int mediaId = rs.getInt("mediaid");
			int tactic = rs.getInt("tactic");
			String client = rs.getString("client");
			String mix = client + String.valueOf(tactic);
			Set<String> valueSet = mediaMap.get(mediaId);
			if(valueSet == null){
				valueSet = new HashSet<String>();
				mediaMap.put(mediaId, valueSet);
			}
			valueSet.add(mix);
		}
		return mediaMap;
	}
	
	private void collect(ResultSet rs, Map<Integer, List<Integer>> mediaToRelatedVideos, Map<Integer, Set<Integer>> mediaToIshds, Map<Integer, Set<Integer>> mediaToPreids, Map<Integer, Set<Integer>> mediaToClassids, Map<Integer, Set<String>> mediaToTactics, Map<Integer, Set<Integer>> mediaToIsdisplays) throws Exception{
		while(rs.next()){
			MediaRecord record = null;
			try {
				record = new MediaRecord(rs, mediaToRelatedVideos, mediaToIshds, mediaToPreids, mediaToClassids, mediaToTactics, mediaToIsdisplays);
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
