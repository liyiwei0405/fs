package com.funshion.ucs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funshion.search.utils.LogHelper;
import com.funshion.ucs.DAO.Area;
import com.funshion.ucs.DAO.AreaRow;
import com.funshion.ucs.DAO.AreaUserClass;
import com.funshion.ucs.DAO.IpSegments;
import com.funshion.ucs.DAO.Mac;
import com.funshion.ucs.DAO.MacRfm;
import com.funshion.ucs.DAO.UserClass;
import com.funshion.ucs.exceptions.ErrorIpFormatException;
import com.funshion.ucs.thrift.UcsCondition;

public class Operator {
	private static final LogHelper log = new LogHelper("Operator");
	private static final long channelValidTime = 86400 * 3;
	public static final Map<String, Map<String, String>> common = new HashMap<String, Map<String, String>>();
	static{
		Map<String, String> defaultTags = new HashMap<String, String>(4);
		defaultTags.put("ipChanged", "black");
		defaultTags.put("noClassMatch", "default");
		defaultTags.put("noMacMatch", "black");
		defaultTags.put("noAreaMatch", "loyal");
		common.put("defaultTags", defaultTags);
	}
	
	private final UcsCondition ucsCondition;
	private long ipLong;
	private Area area;
	private UserClass userClass;
	private AreaUserClass areaUserClass;

	public Operator(final UcsCondition ucsCondition){
		this.ucsCondition = ucsCondition;
		try{
			this.ipLong = Func.ip2Long(ucsCondition.ipaddr);
		}catch(ErrorIpFormatException e){
			log.error(e.getMessage());
			this.ipLong = -1;
		}
		this.area = new Area(ucsCondition.clientType);
		this.userClass = new UserClass(ucsCondition.clientType);
		this.areaUserClass = new AreaUserClass(ucsCondition.clientType);
	}

	public AreaTagAndUCRec getAreaTagAndUCRec(){
		IpLocation.IpSection ipSection = null;
		if(this.ipLong > 0){
			ipSection = IpLocation.instance.getIpSection(this.ipLong);
			if(ipSection.isValid == false){
				ipSection = IpLocation.instance.getForeignIpInfo(this.ipLong);
			}
		}

		Area.AreaContainer areaContainer = null;
		if(ipSection.isValid == false){
			areaContainer = new Area.AreaContainer("CN", "北京", "北京", "");
		}else{
			areaContainer = new Area.AreaContainer(ipSection.country, ipSection.province, ipSection.city, "");
		}

		int areaTag = 0, areaId = 0;
		AreaRow areaRow = this.area.getAreaTacticObj(areaContainer);
		if(areaRow == null) {
			//未命中地域则返回0
			areaTag = 0;
			areaId = 0;
		}else{
			if(areaRow.getUseTactic() == 0){
				areaTag = 0;
			}else{
				areaTag = areaRow.getTactic();
			}
			areaId = areaRow.getAreaId();
		}

		// 以下开始依次尝试从不同维度获取用户的分类
		UserClassRec userClassRec = null;

		// 尝试从MAC地址获取
		userClassRec = getMacUC();
		if(userClassRec != null){
			return new AreaTagAndUCRec(areaTag, userClassRec);
		}

		// 尝试从IP获取
		userClassRec = getIPUC();
		if(userClassRec != null){
			return new AreaTagAndUCRec(areaTag, userClassRec);
		}

		// 尝试从渠道获取
		userClassRec = getChannelUC();
		if(userClassRec != null){
			return new AreaTagAndUCRec(areaTag, userClassRec);
		}

		// 尝试从用户所在地区和rfm值获取
		//获取用户的rfm
		int userRfm = getRfm();
		userClassRec = getAreaUC(areaId, userRfm);
		if(userClassRec != null){
			return new AreaTagAndUCRec(areaTag, userClassRec);
		}

		// 尝试通过用户ID获取
		userClassRec = getUidUC();
		if(userClassRec != null){
			return new AreaTagAndUCRec(areaTag, userClassRec);
		}

		// 均失败则按默认处理
		userClassRec = this.userClass.getUCByType(common.get("defaultTags").get("noClassMatch"));
		// 如果未发现默认策略，返回错误
		if (userClassRec == null) {
			userClassRec = new UserClassRec("err", 1);
		}

		return new AreaTagAndUCRec(areaTag, userClassRec);
	}

	/**
	 * 根据用户所在地区获取用户分类
	 * @param int areaId 用户地区ID
	 * @throws ClientSetException 
	 * @returns mixed 分类成功时返回用户标识，否则返回null
	 */
	private UserClassRec getAreaUC(int areaId, int userRfm) {
		// areaId为0，表示其他地区，返回忠实用户分类
		if (areaId == 0) {
			return this.userClass.getUCByType(common.get("defaultTags").get("noAreaMatch"));
		}
		// 获取地区对应的用户分类ID数组
		List<Integer> userClassIdArray = this.areaUserClass.getUserClassIdArray(areaId);
		// 无匹配数据则返回false
		if(userClassIdArray == null || userClassIdArray.size() < 1){
			return null;
		}

		if(userRfm >= 0){
			return this.userClass.getUCByRFMSecWithIdArray(userRfm, userClassIdArray);
		}else{
			return this.userClass.getMaxWeightUC(userClassIdArray);
		}
	}

	/**
	 * 根据用户的MAC获取用户的分类
	 * @return mixed 当分类失败时返回null，否则返回用户分类字符串
	 */
	private UserClassRec getMacUC() {
		// 获取用户的MAC
		if (this.ucsCondition.mac == null || this.ucsCondition.mac.isEmpty()) {
			return null;
		}
		// 获取用户的分类ID
		List<Integer> userClassIdArray = Mac.instance.getUserClassId(this.ucsCondition.mac);
		// 分类ID为空表示该MAC不在MAC列表中，返回false
		if (userClassIdArray == null) {
			return null;
		}
		// 分类ID不为空则获取用户分类TAG并返回
		return this.userClass.getMaxWeightUC(userClassIdArray);
	}

	/**
	 * 根据用户的IP获取用户的分类
	 * @return mixed 当分类失败时返回null，否则返回用户分类字符串
	 */
	private UserClassRec getIPUC() {
		List<Integer> userClassIdArray = IpSegments.instance.getUserClassIdArray(this.ipLong);
		if(userClassIdArray == null){
			return null;
		}
		return this.userClass.getMaxWeightUC(userClassIdArray);
	}

	/**
	 * 根据客户端渠道获取用过的分类
	 * 
	 * @return mixed 当分类失败时返回false，否则返回用户分类字符串
	 */
	private UserClassRec getChannelUC() {
		int channel = 0;
		try{
			channel = Integer.parseInt(this.ucsCondition.channel);
		}catch(Exception e){
			log.error(e.getMessage());
			return null;
		}
		// 安装超出3天则返回null,渠道失效
		if(this.ucsCondition.installtime <= 0 
				|| (this.ucsCondition.installtime != 0 && this.ucsCondition.installtime < (System.currentTimeMillis() / 1000 - channelValidTime))){
			return null;
		}
		return this.userClass.getUCByChannelId(channel);
	}

	/**
	 * 获取用户的RFM评分
	 */
	private int getRfm() {
		if(this.ucsCondition.mac == null || this.ucsCondition.mac.isEmpty()){
			return -1;
		}
		String rfm = MacRfm.instance.getRfm(this.ucsCondition.mac);
		if(rfm == null || rfm.isEmpty()){
			return -1;
		}
		try{
			int iRfm = Integer.parseInt(rfm);
			return iRfm;
		}catch(Exception e){
			log.error(e.getMessage());
			return -1;
		}
	}

	/**
	 * 根据用户ID获取用户分类
	 */
	private UserClassRec getUidUC() {
		return null;
	}

	public static class AreaTagAndId{
		private int areaTag;
		private int areaId;

		public AreaTagAndId(int areaTag, int areaId) {
			super();
			this.areaTag = areaTag;
			this.areaId = areaId;
		}

		public int getAreaTag() {
			return areaTag;
		}
		public void setAreaTag(int areaTag) {
			this.areaTag = areaTag;
		}
		public int getAreaId() {
			return areaId;
		}
		public void setAreaId(int areaId) {
			this.areaId = areaId;
		}
	}

	public static class UserClassRec{
		private String classTag;
		private int useTactic;

		public UserClassRec(String classTag, int useTactic) {
			super();
			this.classTag = classTag;
			this.useTactic = useTactic;
		}

		public String getClassTag() {
			return classTag;
		}
		public void setClassTag(String classTag) {
			this.classTag = classTag;
		}
		public int getUseTactic() {
			return useTactic;
		}
		public void setUseTactic(int useTactic) {
			this.useTactic = useTactic;
		}
	}

	public static class AreaTagAndUCRec{
		private int areaTag;
		private UserClassRec ucRec;

		public AreaTagAndUCRec(int areaTag, UserClassRec ucRec) {
			super();
			this.areaTag = areaTag;
			this.ucRec = ucRec;
		}

		public int getAreaTag() {
			return areaTag;
		}
		public void setAreaTag(int areaTag) {
			this.areaTag = areaTag;
		}
		public UserClassRec getUcRec() {
			return ucRec;
		}
		public void setUcRec(UserClassRec ucRec) {
			this.ucRec = ucRec;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
