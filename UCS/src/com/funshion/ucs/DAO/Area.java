package com.funshion.ucs.DAO;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.funshion.search.utils.MysqlHelper;
import com.funshion.ucs.Func;

public class Area {
	private Map<String, Map<String, AreaRow>> areaDataMap = new HashMap<String, Map<String, AreaRow>>();
	private Map<String, AreaRow> clientAreas = null;

	private Area(){}
	public static final Area instance = new Area();

	public void loadArea(MysqlHelper mysql) {
		Map<String, Map<String, AreaRow>> areaDataMapTmp = new HashMap<String, Map<String, AreaRow>>();
		try {
			long timeStamp = System.currentTimeMillis() / 1000;
			ResultSet rs = mysql.getCursor("SELECT areaid,client,name,atype,tactic,use_tactic FROM fs_area_info WHERE isvalid=1 AND tacticvalid>0 AND tacticvalid < " + timeStamp);
			while(rs.next()){
				AreaRow areaRow = new AreaRow(rs.getInt("areaid"), rs.getString("atype"), rs.getInt("tactic"), rs.getInt("use_tactic"));

				Map<String, AreaRow> ctypeMap = areaDataMapTmp.get(rs.getString("client"));
				if(ctypeMap == null){
					ctypeMap = new HashMap<String, AreaRow>();
					areaDataMapTmp.put(rs.getString("client"), ctypeMap);
				}
				ctypeMap.put(rs.getString("name"), areaRow);
			}
			this.areaDataMap = areaDataMapTmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setClientAreas(String client){
		if(areaDataMap.containsKey("all")) {
			this.clientAreas = this.areaDataMap.get("all");
		}
		if(areaDataMap.containsKey(client)) {
			this.clientAreas = this.areaDataMap.get(client);
		}
	}

	/**
	 * 通过地域名获取地域策略标识
	 * @param string area
	 * @return string
	 */
	public int getAreaTacticByName(String area) {
		int tactic = 0;
		if(this.clientAreas.containsKey(area)) {
			AreaRow areaRow = this.clientAreas.get(area);
			if(areaRow.getUseTactic() == 0) {
				tactic = 0;
			} else {
				tactic = areaRow.getTactic();
			}
		} else {
			//未命中地域则返回0
			tactic = 0;
		}
		return tactic;
	}

	/**
	 * 通过area对象获取地域策略标识
	 * @param AreaContainer areaContainer
	 */
	public AreaRow getAreaTacticObj(AreaContainer areaContainer){
		if (areaContainer.country.indexOf("香港") > -1) {
			areaContainer.country = "HK";
		}
		if (areaContainer.country.indexOf("台湾") > -1) {
			areaContainer.country = "TW";
		}
		if (areaContainer.country.indexOf("澳门") > -1) {
			areaContainer.country = "AM";
		}
		if (areaContainer.country.indexOf("中国") > -1) {
			areaContainer.country = "CN";
		}
		if (areaContainer.country.indexOf("美国") > -1) {
			areaContainer.country = "US";
		}
		areaContainer.country = areaContainer.country.toUpperCase();
		//香港、澳门、台湾地区将国家设置为CN
		if(areaContainer.country == "HK" || areaContainer.country == "TW" || areaContainer.country == "AM") {
			areaContainer.country = "CN";
		}
		//非国内、美国则将地区设置为海外
		if(areaContainer.country != "CN" && areaContainer.country != "US") {
			areaContainer.area = "海外";
		}
		//去掉省、市等后缀
		areaContainer.province = Func.rtrim(areaContainer.province, '省');
		areaContainer.city = Func.rtrim(areaContainer.city, '市');
		if(areaContainer.country == "CN") {
			areaContainer.country = "中国";
		}
		if(areaContainer.country == "US"){
			areaContainer.country = "美国";
		}

		//依次判断选取地域对象
		if(areaContainer.city != "" && clientAreas.containsKey(areaContainer.city)) {
			return clientAreas.get(areaContainer.city);
		} else if(areaContainer.province != "" && clientAreas.containsKey(areaContainer.province)) {
			return clientAreas.get(areaContainer.province);
		} else if(areaContainer.country != "" && clientAreas.containsKey(areaContainer.country)) {
			return clientAreas.get(areaContainer.country);
		} else if(areaContainer.area != "" && clientAreas.containsKey(areaContainer.area)) {
			return clientAreas.get(areaContainer.area);
		}
		return null;
	}

	public static class AreaContainer{
		private String country;
		private String province;
		private String city;
		private String area;

		public AreaContainer(String country, String province, String city, String area) {
			super();
			this.country = country;
			this.province = province;
			this.city = city;
			this.area = area;
		}

		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getArea() {
			return area;
		}
		public void setArea(String area) {
			this.area = area;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
