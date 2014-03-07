package com.funshion.ucs.DAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.MysqlHelper;

public class AreaUserClass {
	private static final LogHelper log = new LogHelper("AreaUserClass");
	private static Map<String, Map<Integer, List<Integer>>> areaUserClassesMap = new HashMap<String, Map<Integer, List<Integer>>>();
	
	private Map<Integer, List<Integer>> clientAreaUserClasses = null;
	
	public  AreaUserClass(String client){
		this.setClientAreaUserClasses(client);
	}

	public static void loadAreaUserClasses(MysqlHelper mysql) {
		Map<String, Map<Integer, List<Integer>>> areaUserClassesMapTmp = new HashMap<String, Map<Integer, List<Integer>>>();
		try {
			ResultSet rs = mysql.getCursor("SELECT a.user_classid,a.areaid,b.client FROM fs_user_class_area_relation a LEFT JOIN fs_area_info b ON a.areaid = b.areaid");
			while(rs.next()){
				Map<Integer, List<Integer>> areaidMap = areaUserClassesMapTmp.get(rs.getString("client"));
				if(areaidMap == null){
					areaidMap = new HashMap<Integer, List<Integer>>();
					areaUserClassesMapTmp.put(rs.getString("client"), areaidMap);
				}
				List<Integer> userClassidList = areaidMap.get(rs.getInt("areaid"));
				if(userClassidList == null){
					userClassidList = new ArrayList<Integer>();
					areaidMap.put(rs.getInt("areaid"), userClassidList);
				}
				userClassidList.add(rs.getInt("user_classid"));
			}
			areaUserClassesMap = areaUserClassesMapTmp;
			if(log.logger.isInfoEnabled()){
				log.info("loadAreaUserClasses done, areaUserClassesMap' s size: " + areaUserClassesMap.size());
				log.info(areaUserClassesMap.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setClientAreaUserClasses(String client) {
		if (areaUserClassesMap.containsKey("all")) {
			this.clientAreaUserClasses = areaUserClassesMap.get("all");
			if(areaUserClassesMap.containsKey(client)) {
				this.clientAreaUserClasses.putAll(areaUserClassesMap.get(client));
			} 
		}else{
			this.clientAreaUserClasses = areaUserClassesMap.get(client);
		}
	}

	public List<Integer> getUserClassIdArray(int areaId){
		if(this.clientAreaUserClasses == null){
			return null;
		}
		return this.clientAreaUserClasses.get(areaId);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
