package com.funshion.ucs;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;

import com.funshion.ucs.Operator.AreaTagAndUCRec;
import com.funshion.ucs.Operator.UserClassRec;
import com.funshion.ucs.DAO.Area;
import com.funshion.ucs.DAO.UserClass;
import com.funshion.ucs.thrift.AreaTacticResult;
import com.funshion.ucs.thrift.RetCode;
import com.funshion.ucs.thrift.Tactics;
import com.funshion.ucs.thrift.UCS.Iface;
import com.funshion.ucs.thrift.UcsCondition;
import com.funshion.ucs.thrift.UcsObjectResult;
import com.funshion.ucs.thrift.UcsStringResult;
import com.funshion.ucs.thrift.UserClassResult;

public class UCSImpl implements Iface{
	public static Map<String, String> clientMap = new HashMap<String, String>(6);
	//	public static Set<String> clientSet = new HashSet<String>(10);
	static{
		clientMap.put("ott", "apad");
		clientMap.put("360app", "mweb");
		clientMap.put("baiduapp", "mweb");
		clientMap.put("jmapp", "mweb");
		clientMap.put("91app", "mweb");
		clientMap.put("wdjapp", "mweb");

		//		clientSet.add("pc");
		//		clientSet.add("web");
		//		clientSet.add("mweb");
		//		clientSet.add("ipad");
		//		clientSet.add("iphone");
		//		clientSet.add("apad");
		//		clientSet.add("aphone");
		//		clientSet.add("winpad");
		//		clientSet.add("winphone");
		//		clientSet.add("third_part");
	}

	@Override
	public UcsObjectResult getUcsObject(UcsCondition ucsCondition)
			throws TException {
		if(! UCSUpdateHelper.isReady()){
			return new UcsObjectResult(RetCode.SrvUnavail, "service not ready", null);
		}
		if(! paramCondition(ucsCondition)){
			return new UcsObjectResult(RetCode.BadReq, "bad request", null);
		}
		Operator operator = new Operator(ucsCondition);
		AreaTagAndUCRec tagAndUCRec = operator.getAreaTagAndUCRec();

		if(tagAndUCRec.getUcRec() == null){
			tagAndUCRec.setUcRec(new UserClassRec("err", 1));
		}
		Tactics ucsObject = new Tactics(tagAndUCRec.getUcRec().getClassTag(), tagAndUCRec.getAreaTag(), tagAndUCRec.getUcRec().getUseTactic());
		UcsObjectResult result = new UcsObjectResult(RetCode.OK, "OK", ucsObject);

		return result;
	}

	@Override
	public UcsStringResult getUcsString(UcsCondition ucsCondition)
			throws TException {
		if(! UCSUpdateHelper.isReady()){
			return new UcsStringResult(RetCode.SrvUnavail, "service not ready", "");
		}
		if(! paramCondition(ucsCondition)){
			return new UcsStringResult(RetCode.BadReq, "bad request", "");
		}

		Operator operator = new Operator(ucsCondition);
		AreaTagAndUCRec tagAndUCRec = operator.getAreaTagAndUCRec();

		if(tagAndUCRec.getUcRec() == null){
			tagAndUCRec.setUcRec(new UserClassRec("err", 1));
		}
		UcsStringResult result = new UcsStringResult(RetCode.OK, "OK", getEncStr(tagAndUCRec));

		return result;
	}

	@Override
	public UserClassResult getUserDefaultClassTag(String clientType)
			throws TException {
		if(! UCSUpdateHelper.isReady()){
			return new UserClassResult(RetCode.SrvUnavail, "service not ready", "");
		}
		
		UserClassRec ucRec = new UserClass(clientType).getUCByType(Operator.common.get("defaultTags").get("noClassMatch"));
		String classTag = null;
		if(ucRec != null){
			classTag = ucRec.getClassTag();
		}else{
			classTag = "err";
		}
		return new UserClassResult(RetCode.OK, "OK", classTag);
	}

	@Override
	public AreaTacticResult getAreaTacticId(String clientType, String area)
			throws TException {
		if(! UCSUpdateHelper.isReady()){
			return new AreaTacticResult(RetCode.SrvUnavail, "service not ready", 0);
		}
		return new AreaTacticResult(RetCode.OK, "OK", new Area(clientType).getAreaTacticByName(area));
	}

	private String getEncStr(AreaTagAndUCRec tagAndUCRec) {
		String classTag = tagAndUCRec.getUcRec().getClassTag();
		String result = "";
		for(int i = classTag.length()-1; i >= 0; i --){
			result += getFiveChars() + classTag.charAt(i);
		}
		result += getFiveChars() + tagAndUCRec.getAreaTag() + getFiveChars();
		result += tagAndUCRec.getUcRec().getUseTactic() == 0 ? '0' : '1';
		result += getFiveChars();

		return result;
	}

	private String getFiveChars() {
		int len = 5;
		String chars = "abcdefhijkmnprstwxyz012345678";	// 默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1
		int maxPos = chars.length();
		String pwd = "";
		for(int i = 0; i < len; i ++){
			pwd += chars.charAt((int)Math.floor(Math.random() * maxPos));
		}
		return pwd;
	}

	private boolean paramCondition(UcsCondition ucsCondition){
		if(ucsCondition.clientType == null 
				//				|| ! clientSet.contains(ucsCondition.clientType.trim())
				){
			return false;
		}else{
			if(clientMap.containsKey(ucsCondition.clientType)){
				ucsCondition.clientType = clientMap.get(ucsCondition.clientType);
			}
			ucsCondition.clientType = ucsCondition.clientType.trim();
		}
		if(ucsCondition.mac != null){
			ucsCondition.mac = ucsCondition.mac.toUpperCase();
		}
		if(ucsCondition.ipaddr == null){
			ucsCondition.ipaddr = "";
		}
		return true;
	}

}
