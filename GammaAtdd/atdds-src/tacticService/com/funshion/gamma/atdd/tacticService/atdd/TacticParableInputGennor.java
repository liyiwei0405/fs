package com.funshion.gamma.atdd.tacticService.atdd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.funshion.gamma.atdd.ParableInputGennor;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Md5;
import com.funshion.search.utils.Misc;
import com.funshion.search.utils.rds.RDS;

public class TacticParableInputGennor extends ParableInputGennor{
	public static final int maxLoadIds = 100;
	LogHelper log = new LogHelper("gen");
//	ArrayList<Integer>notPublish = new ArrayList<Integer>();
	ArrayList<Integer>has = new ArrayList<Integer>();
	
	int hasSize = 0;
	int notHasSize = 0;
	ArrayList<String>tactics = new ArrayList<String>();
	ArrayList<Integer>area = new ArrayList<Integer>();
	public TacticParableInputGennor() throws SQLException{
		RDS rds = RDS.getRDSByDefine("corsair_0", "select distinct(mediaid) from fs_media");
		HashSet<Integer>set = new HashSet<Integer>();
		ResultSet rs = (ResultSet) rds.load();
		
		while(rs.next()){
			int intg = rs.getInt(1);
			set.add(intg);
			has.add(intg);
		}
		hasSize = has.size();
		
		RDS rdsLoadTactic = RDS.getRDSByDefine("corsair_0", "select classtag from fs_user_class",
				rds.getMyConnection());
		ResultSet rsLoadTactic = rdsLoadTactic.load();
		while(rsLoadTactic.next()){
			String str = rsLoadTactic.getString(1);
			tactics.add(str);
		}
		int realTac = tactics.size();
		for(int x = 0; x < realTac; x ++){
			String md5 = Md5.md5(("" + Math.random()).getBytes());
			tactics.add(md5.substring(0, 3));
		}
		
		RDS rdsLoadAreaInfo = RDS.getRDSByDefine("corsair_0", 
				"SELECT tactic FROM fs_media_area_tactic_relation WHERE tactic = 0 UNION SELECT a.tactic FROM fs_media_area_tactic_relation a, fs_area_info b WHERE a.tactic = b.tactic AND b.isvalid > 0 AND b.tacticvalid > 0 AND a.client = b.client",
				rds.getMyConnection());
		ResultSet rdsLoadAreaInfoRs = rdsLoadAreaInfo.executeQuery();
		while(rdsLoadAreaInfoRs.next()){
			area.add(rdsLoadAreaInfoRs.getInt(1));
			
		}
		for(int x = 0; x < 10; x ++){
			int v = (int) (Math.random() * 200);
			area.add(v);
		}
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
	int maxTest = 1000000;
	class TacticQueryList{
		String ucs;
		boolean isGetMediaTactic = false;
		List<Integer>list = new ArrayList<Integer>();
		public String toString(){
			return "UCS:" + ucs + ",isGetMediaTactic:" + isGetMediaTactic + ", list:" + list;
		}
		public QueryParas toQueryParas(){
			return new QueryParas(){

				@Override
				public Object[] getParas() {
					return new Object[]{
							list, ucs	
					};
				}
			
			};
		}
	}
	TacticQueryList toQuery;
	@Override
	public boolean hasNext() {
		hasTest ++;
		if(hasTest > maxTest){
			return false;
		}
		toQuery = new TacticQueryList();
		toQuery.ucs = getUCS();
		
		double factor = Math.random() * Math.random();
		
		int factorValue = (int) (factor*Integer.MAX_VALUE);
		toQuery.isGetMediaTactic = factorValue % 2 == 0;
		toQuery.list.clear();
		int loadNum = (int) (Math.random() * maxLoadIds);
		
		for(int x = 0; x < loadNum; x ++){
			double mav = Math.random();
			if(mav > factor){
				//add has
				int pos = (int) (Math.random() * hasSize);
				toQuery.list.add(this.has.get(pos));
			}else{
				//add not has
				int pos = (int) (Math.random() * notHasSize);
				toQuery.list.add(this.has.get(hasSize + pos));
			}
			
		}
		
		return true;
	}

	private String getUCS() {
		return "" + rand(tactics) + rand(this.area) + "1";
	}
	private Object rand(List<?> o){
		return o.get(Misc.randInt(0, o.size()));
	}
	@Override
	protected QueryParas genAvsQuery() {
		return toQuery.toQueryParas();
	}

	@Override
	protected QueryParas genRcsQuery() {
		return toQuery.toQueryParas();
	}
	
	public String toString(){
		if(this.toQuery == null){
			return "NULL query";
		}
		return toQuery.toString();
	}
	
	public static void main(String[]args) throws Exception{
		TacticParableInputGennor gen = new TacticParableInputGennor();
		while(gen.hasNext()){
			QueryParas paras = gen.nextAvsQuery();
			System.out.println(paras);
		}
	}
}
