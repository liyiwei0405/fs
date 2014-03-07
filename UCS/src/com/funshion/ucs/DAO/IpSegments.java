package com.funshion.ucs.DAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.MysqlHelper;
import com.funshion.ucs.Func;

public class IpSegments {
	private List<IpSegmentsRow> ipSegmentsList = new LinkedList<IpSegmentsRow>();
	private final LogHelper log = new LogHelper("IpSegments");
	
	private IpSegments(){}
	public static final IpSegments instance = new IpSegments();

	public void loadIpSegments(MysqlHelper mysql) {
		List<IpSegmentsRow> ipSegmentsListTmp = new LinkedList<IpSegmentsRow>();
		try {
			ResultSet rs = mysql.getCursor("SELECT segid,ipstart,ipend,user_classid FROM fs_ip_segments WHERE isvalid = 1");
			while(rs.next()){
				this.ipSegmentsList.add(new IpSegmentsRow(Func.ip2Long(rs.getString("ipstart")), Func.ip2Long(rs.getString("ipend")), rs.getInt("user_classid")));
			}
			this.ipSegmentsList = ipSegmentsListTmp;
			if(log.logger.isInfoEnabled()){
				log.info("loadIpSegments done, areaDataMap' s size: " + this.ipSegmentsList.size());
				log.info(this.ipSegmentsList.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据IP地址获取用户对应的分类ID
	 * @param string ipAddr
	 * @return mixed 有命中时返回整数ID，否则返回false
	 */
	public List<Integer> getUserClassIdArray(long ipLong){
		List<Integer> userClassIdArray = new ArrayList<Integer>();
		for(IpSegmentsRow row : this.ipSegmentsList){
			if(ipLong > row.getIpStart() && ipLong < row.getIpEnd()){
				userClassIdArray.add(row.getUserClassId());
			}
		}
		if(userClassIdArray.size() < 1){
			return null;
		}
		return userClassIdArray;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
