package com.funshion.ucs.test;

import java.util.ArrayList;
import java.util.List;

import com.funshion.ucs.thrift.UcsCondition;


public class TestCase {
	public static List<UcsCondition> conditionList=new ArrayList<UcsCondition>();
	
	static{
		conditionList.add(new UcsCondition ("winpad","1.2.3.1","","",0,"62.185.132.211",0));
		conditionList.add(new UcsCondition("iphone","1.2.3.1","","",0,"62.185.132.211",0));
		
		conditionList.add(new UcsCondition("pc","1.2.3.1","","",0,"192.168.16.212",0));
		conditionList.add(new UcsCondition("iphone","1.2.3.1","","",0,"155.53.3.61",0));
		conditionList.add(new UcsCondition("apad","1.2.3.1","","",0,"155.53.3.61",0) );
		conditionList.add(new UcsCondition("aphone","1.2.3.1","","",0,"155.53.3.61",0));
		conditionList.add(new UcsCondition("ipad","1.2.3.1","","",0,"155.53.3.61",0));
		
		conditionList.add(new UcsCondition("pc","1.2.3.1","111","",(System.currentTimeMillis())/1000-1000,"155.53.3.61",0));
		conditionList.add(new UcsCondition("pc","1.2.3.1","111","",(System.currentTimeMillis())/1000-25920,"155.53.3.61",0));
		conditionList.add(new UcsCondition("iphone","1.2.3.1","111","",(System.currentTimeMillis())/1000-1000,"155.53.3.61",0));
		conditionList.add(new UcsCondition("pc","1.2.3.1","112","",(System.currentTimeMillis())/1000-1000,"155.53.3.61",0));
		conditionList.add(new UcsCondition("pc","1.2.3.1","222","",(System.currentTimeMillis())/1000-1000,"155.53.3.61",0));
		
		conditionList.add(new UcsCondition ("ipad","1.2.3.1","","",0,"5.148.0.15",0));
		conditionList.add(new UcsCondition ("pc","1.2.3.1","","",0,"220.245.0.10",0));
		conditionList.add(new UcsCondition ("ipad","1.2.3.1","","",0,"5.148.16.15",0));
		
		conditionList.add(new UcsCondition ("iphone","1.2.3.1","","70DEE2F207D1",0,"5.148.0.15",0));
		conditionList.add(new UcsCondition ("pc","1.2.3.1","","11:11:11:11:11:FF",0,"220.245.0.10",0));
		conditionList.add(new UcsCondition ("pc","1.2.3.1","","70DEE2F207D1",0,"5.148.16.15",0));
	}
	
}
