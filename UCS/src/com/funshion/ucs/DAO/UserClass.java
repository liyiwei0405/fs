package com.funshion.ucs.DAO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.funshion.search.utils.MysqlHelper;
import com.funshion.ucs.Operator;
import com.funshion.ucs.Operator.UserClassRec;

public class UserClass {
	private Map<String, Map<Integer, UserClassRow>> userClassesMap = new HashMap<String, Map<Integer, UserClassRow>>();
	private Map<String, Map<String, List<UserClassRow>>> typeClassesMap = new HashMap<String, Map<String, List<UserClassRow>>>();
	private Map<Integer, UserClassRow> clientClasses = null;
	private Map<String, List<UserClassRow>> typeClasses = null;

	private UserClass(){}
	public static final UserClass instance = new UserClass();

	public void loadUserClasses(MysqlHelper mysql) {
		Map<String, Map<Integer, UserClassRow>> userClassesMapTmp = new HashMap<String, Map<Integer, UserClassRow>>();
		Map<String, Map<String, List<UserClassRow>>> typeClassesMapTmp = new HashMap<String, Map<String, List<UserClassRow>>>();
		try {
			long timeStamp = System.currentTimeMillis() / 1000;
			ResultSet rs = mysql.getCursor("SELECT classid,classtag,client,rfmsec,userids,channelids,ctype,weight,use_tactic " +
					"FROM fs_user_class WHERE validtime>0 AND validtime < " + timeStamp);
			while(rs.next()){
				UserClassRow ucRow = new UserClassRow(rs.getInt("classid"), 
						rs.getString("classtag"), rs.getString("rfmsec"),
						rs.getString("userids").indexOf(",") == -1 ? new ArrayList<String>(0) : Arrays.asList(rs.getString("userids").split(",")), 
								rs.getString("channelids").indexOf(",") == -1 ? new ArrayList<String>(0) : Arrays.asList(rs.getString("channelids").split(",")), 
										rs.getString("ctype"), 	
										rs.getInt("weight"),
										rs.getInt("use_tactic"));
				Map<Integer, UserClassRow> classidMap = userClassesMapTmp.get(rs.getString("client"));
				if(classidMap == null){
					classidMap = new HashMap<Integer, UserClassRow>();
					userClassesMapTmp.put(rs.getString("client"), classidMap);
				}
				classidMap.put(rs.getInt("classid"), ucRow);

				Map<String, List<UserClassRow>> ctypeMap = typeClassesMapTmp.get(rs.getString("client"));
				if(ctypeMap == null){
					ctypeMap = new HashMap<String, List<UserClassRow>>();
					typeClassesMapTmp.put(rs.getString("client"), ctypeMap);
				}
				List<UserClassRow> ucRowList = ctypeMap.get(rs.getString("ctype"));
				if(ucRowList == null){
					ucRowList = new ArrayList<UserClassRow>();
					ctypeMap.put(rs.getString("ctype"), ucRowList);
				}
				ucRowList.add(ucRow);
			}
			this.userClassesMap = userClassesMapTmp;
			this.typeClassesMap = typeClassesMapTmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据整体当前客户端的分类集合
	 * @param string client
	 */
	public void setClientClasses(String client) {
		if (this.userClassesMap.containsKey("all")) {
			this.clientClasses = this.userClassesMap.get("all");
		}
		if(this.userClassesMap.containsKey(client)) {
			this.clientClasses = this.userClassesMap.get(client);
		} 
	}
	
	public void setTypeClasses(String client) {
		if (this.typeClassesMap.containsKey("all")) {
			this.typeClasses = this.typeClassesMap.get("all");
		}
		if(this.typeClassesMap.containsKey(client)) {
			this.typeClasses = this.typeClassesMap.get(client);
		} 
	}
	
	/**
	 * 根据类型获取用户分类标识
	 */
	public UserClassRec getUCByType(String type) {
		if(this.typeClasses == null || ! this.typeClasses.containsKey(type)){
			return null;
		}
		List<Integer> userClassIdArray = new ArrayList<Integer>();
		for(UserClassRow row : this.typeClasses.get(type)){
			userClassIdArray.add(row.getClassId());
		}
		return getMaxWeightUC(userClassIdArray);
	}
	
	/**
	 * 计算传入分类ID数组中对应用户分类集合的最大权重分类，并返回分类标识
	 * @param array classIdArray
	 * @return string
	 */
	public Operator.UserClassRec getMaxWeightUC(List<Integer> classIdArray) {
		if(classIdArray == null) {
			classIdArray = new ArrayList<Integer>(this.clientClasses.keySet().size());
			classIdArray.addAll(this.clientClasses.keySet());
		}
		int curWeight = -1;
		UserClassRow finalUCRow = null;
		for(Integer cid : classIdArray) {
			if(! this.clientClasses.containsKey(cid)) {
				continue;
			}
			if(this.clientClasses.get(cid).getWeight() > curWeight) {
				curWeight = this.clientClasses.get(cid).getWeight();
				finalUCRow = this.clientClasses.get(cid);
			}
		}
		if(finalUCRow != null){
			return new Operator.UserClassRec(finalUCRow.getClassTag(), finalUCRow.getUseTactic());
		}
		return null;
	}
	
	public UserClassRec getUCByRFMSecWithIdArray(int rfm,
			List<Integer> classIdArray) {
		List<Integer> classIds = new LinkedList<Integer>();
		for(Integer userClassId : classIdArray){
			UserClassRow ucRow = getClassRecById(userClassId);
			if(ucRow == null){
				continue;
			}
			if(ucRow.getRfmSec().indexOf(',') != -1){
				String[] RFMSegs = ucRow.getRfmSec().split(",");
				if(rfm >= Integer.parseInt(RFMSegs[0]) && rfm <= Integer.parseInt(RFMSegs[1])) {
					classIds.add(userClassId);
				}
			}else{
				//该记录里没有rfm字段值
				classIds.add(userClassId);
			}
		}
		if(classIds.size() < 1) {
			return null;
		}
		return getMaxWeightUC(classIds);
	}

	private UserClassRow getClassRecById(Integer classId) {
		if(classId == 0) {
			return null;
		}
		if(this.clientClasses == null || ! clientClasses.containsKey(classId)) {
			return null;
		}
		return clientClasses.get(classId);
	}
	
	/**
	 * 根据渠道ID获取用户分类标识
	 * @param int channelid
	 * @return mixed 分类失败时返回null,否则返回对应分类标识
	 */
	public UserClassRec getUCByChannelId(int channelid) {
		List<Integer> userClassIdArray = new ArrayList<Integer>();
		for(UserClassRow row : this.clientClasses.values()){
			if(row.getChannelIds().contains(channelid)){
				userClassIdArray.add(row.getClassId());
			}
		}
		if(userClassIdArray.size() < 1){
			return null;
		}
		return getMaxWeightUC(userClassIdArray);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
