package com.funshion.gamma.atdd.vodInfo.atdd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import com.funshion.gamma.atdd.ParableInputGennor;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.rds.RDS;

public class GetInfoByMediaIDInputGennor extends ParableInputGennor{
	public static class MediaOrder{
		public final int mediaId;
		MediaOrder(int mediaId){
			this.mediaId = mediaId;
		}
		public String toString(){
			return "mediaId = " + mediaId ;
		}
	}
	LogHelper log = new LogHelper("gen");
//	ArrayList<Integer>notPublish = new ArrayList<Integer>();
	ArrayList<Integer>has = new ArrayList<Integer>();
	
	int hasSize = 0;
	int notHasSize = 0;
	public GetInfoByMediaIDInputGennor() throws SQLException{
		RDS rds = RDS.getRDSByDefine("corsair_0", "select distinct(mediaid) from fs_playinfo");
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
			int str = (int) (Integer.MAX_VALUE * Math.random());
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
	MediaOrder toQuery;
	@Override
	public boolean hasNext() {
		hasTest ++;
		if(hasTest > maxTest){
			return false;
		}
		int loadNum = (int) (Math.random() * (hasSize + notHasSize));
		int mediaId = this.has.get(loadNum);
		
		boolean asc = (int) (Math.random() * Integer.MAX_VALUE) % 2 == 0;
		toQuery = new MediaOrder(mediaId);
		return true;
	}

	@Override
	protected QueryParas genAvsQuery() {
		return QueryParas.instance(this.toQuery.mediaId);
	}

	@Override
	protected QueryParas genRcsQuery() {
		return QueryParas.instance(this.toQuery.mediaId);
	}

	public String toString(){
		return "index " + this.hasTest  + ", body = " + this.toQuery;
	}
}
