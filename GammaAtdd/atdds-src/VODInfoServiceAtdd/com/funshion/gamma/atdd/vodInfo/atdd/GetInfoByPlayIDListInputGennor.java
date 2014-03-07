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

public class GetInfoByPlayIDListInputGennor extends ParableInputGennor{
	LogHelper log = new LogHelper("gen");
//	ArrayList<Integer>notPublish = new ArrayList<Integer>();
	ArrayList<Integer>has = new ArrayList<Integer>();
	
	int hasSize = 0;
	int notHasSize = 0;
	public GetInfoByPlayIDListInputGennor() throws SQLException{
		RDS rds = RDS.getRDSByDefine("corsair_0", "select playinfoid from fs_playinfo");
		HashSet<Integer>set = new HashSet<Integer>();
		ResultSet rs = (ResultSet) rds.load();
		
		while(rs.next()){
			int intg = rs.getInt(1);
			set.add(intg);
			has.add(intg);
		}
		hasSize = has.size();
		rds.close();
		while(true){
			int x = (int)(Math.random() * Integer.MAX_VALUE);
			if(set.contains(x)){
				continue;
			}
			has.add(x);
			notHasSize ++;
			if(notHasSize > 10000){
				break;
			}
		}
		log.info("gen has %s, not Has %s", hasSize, has.size() - hasSize);
	}
	int hasTest = 0;
	int maxTest = 100000;
	List<Integer>toQuery = new ArrayList<Integer>();
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
			if(mav > factor){
				//add has
				int pos = (int) (Math.random() * hasSize);
				toQuery.add(this.has.get(pos));
			}else{
				//add not has
				int pos = (int) (Math.random() * notHasSize);
				toQuery.add(this.has.get(hasSize + pos));
			}
			
		}
		
		return true;
	}

	@Override
	protected QueryParas genAvsQuery() {
		return QueryParas.instance(this.toQuery);
	}

	@Override
	protected QueryParas genRcsQuery() {
		return QueryParas.instance(this.toQuery);
	}
	public String toString(){
		return "index " + this.hasTest + ", query size " + this.toQuery.size() + ", body = " + this.toQuery;
	}
}
