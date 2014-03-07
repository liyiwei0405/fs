package com.funshion.ucs.DAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.MysqlHelper;

public class Mac {
	private Map<String, List<Integer>> macsMap = new HashMap<String, List<Integer>>();
	private final LogHelper log = new LogHelper("Mac");
	
	private Mac(){}
	public static final Mac instance = new Mac();

	public void loadMACs(MysqlHelper mysql) {
		Map<String, List<Integer>> macsMapTmp = new HashMap<String, List<Integer>>();
		try {
			ResultSet rs = mysql.getCursor("SELECT mac,user_classid FROM fs_mac_user_class_relation WHERE isvalid = 1");
			while(rs.next()){
				List<Integer> userClassidList = macsMapTmp.get(rs.getString("mac"));
				if(userClassidList == null){
					userClassidList = new ArrayList<Integer>();
					macsMapTmp.put(rs.getString("mac"), userClassidList);
				}
				userClassidList.add(rs.getInt("user_classid"));
			}
			this.macsMap = macsMapTmp;
			if(log.logger.isInfoEnabled()){
				log.info("loadMACs done, areaDataMap' s size: " + this.macsMap.size());
				log.info(this.macsMap.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> getUserClassId(String mac){
		return this.macsMap.get(mac);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
