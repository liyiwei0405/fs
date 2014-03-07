package com.funshion.gamma.basicTest.atdd;

import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.ParableInputGennor;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.gamma.basicTest.UserProfile;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Misc;

public class GetInfoByHashIDListInputGennor extends ParableInputGennor{
	static UserProfile instance = new UserProfile(1, "Mark Slee", "I'll find something to put here.");
	LogHelper log = new LogHelper("gen");
//	ArrayList<Integer>notPublish = new ArrayList<Integer>();
	int hasTest = 0;
	int maxTest = 100000;

	final List<UserProfile> toQuery;
	
	
	public GetInfoByHashIDListInputGennor(){
		int num = 1;
		String strNum = System.getProperty("userNum");
		try{
			num = Integer.parseInt(strNum.trim());
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("num is " + num);
		 toQuery = new ArrayList<UserProfile>(num);
		for(int x = 0; x < num; x ++){
			toQuery.add(instance);
		}
	}
	@Override
	public boolean hasNext() {
		hasTest ++;
		return true;
	}

	@Override
	protected QueryParas genAvsQuery() {
		return QueryParas.instance(toQuery);
	}

	@Override
	protected QueryParas genRcsQuery() {
		return QueryParas.instance(toQuery);
	}

	public String toString(){
		return "index " + this.hasTest + ", query size " + this.toQuery.size() + ", body = " + this.toQuery;
	}
}
