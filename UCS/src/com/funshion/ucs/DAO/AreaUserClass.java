package com.funshion.ucs.DAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.search.utils.MysqlHelper;

public class AreaUserClass {
	private Map<String, Map<Integer, List<Integer>>> areaUserClassesMap = new HashMap<String, Map<Integer, List<Integer>>>();
	private Map<Integer, List<Integer>> clientAreaClasses = null;

	private AreaUserClass(){}
	public static final AreaUserClass instance = new AreaUserClass();

	public void loadAreaUserClasses(MysqlHelper mysql) {
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
			this.areaUserClassesMap = areaUserClassesMapTmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTypeClasses(String client) {
		if (this.areaUserClassesMap.containsKey("all")) {
			this.clientAreaClasses = this.areaUserClassesMap.get("all");
		}
		if(this.areaUserClassesMap.containsKey(client)) {
			this.clientAreaClasses = this.areaUserClassesMap.get(client);
		} 
	}
	
	public List<Integer> getUserClassIdArray(int areaId){
		return this.clientAreaClasses.get(areaId);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
