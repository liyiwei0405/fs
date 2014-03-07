package com.funshion.gamma.atdd.vodInfo.atdd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.funshion.gamma.atdd.ParableInputGennor;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.rds.RDS;

public class GetInfoByHashIDListInputGennor extends ParableInputGennor{
	LogHelper log = new LogHelper("gen");
//	ArrayList<Integer>notPublish = new ArrayList<Integer>();
	ArrayList<String>has = new ArrayList<String>();
	
	int hasSize = 0;
	int notHasSize = 0;
	public GetInfoByHashIDListInputGennor() throws SQLException{
		RDS rds = RDS.getRDSByDefine("corsair_0", "select hashid from fs_playinfo");
		HashSet<String>set = new HashSet<String>();
		ResultSet rs = (ResultSet) rds.load();
		
		while(rs.next()){
			String intg = rs.getString(1);
			set.add(intg);
			has.add(intg);
		}
		hasSize = has.size();
		rds.close();
		while(true){
			String str = "zzzzzzzzz" + Math.random();
			if(set.contains(str)){
				continue;
			}
			has.add(str);
			notHasSize ++;
			if(notHasSize > 10000){
				break;
			}
			
		}
		log.info("gen has %s, not Has %s", hasSize, has.size() - hasSize);
	}
	int hasTest = 0;
	int maxTest = 100000;

	List<String>toQuery = new ArrayList<String>();
	@Override
	public boolean hasNext() {
		hasTest ++;
		if(hasTest > maxTest){
			return false;
		}
		double factor = Math.random() * Math.random();
		toQuery.clear();
		int loadNum = (int) (Math.random() * 800);
		
		for(int x = 0; x < loadNum; x ++){
			double mav = Math.random();
			String toQueryStr;
			if(mav > factor){
				//add has
				int pos = (int) (Math.random() * hasSize);
				toQueryStr = this.has.get(pos);
			}else{
				//add not has
				int pos = (int) (Math.random() * notHasSize);
				toQueryStr = this.has.get(hasSize + pos);
			}
			toQuery.add(toQueryStr);
//			if(toQueryStr == null || toQueryStr.trim().length() == 0){
//				throw new RuntimeException(x + "");
//			}
			
		}
		
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
