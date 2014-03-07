package com.funshion.gamma.atdd.tacticService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.rds.RDS;
public class UCSChecker {
	LogHelper log = new LogHelper("ucs");
	private static UCSChecker instance ;
	static{
		new Thread(){
			public void run(){
				try {
					sleep(60000);
					instance = new UCSChecker();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public synchronized static UCSChecker instance(){
		if(instance == null){
			try{
			instance = new UCSChecker();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	
	public class UCS{
		final int classId;
		final String classTag;
		final boolean useTactic;
		final Set<Integer>mcids = new HashSet<Integer>();
		public final String client;
		public UCS(ResultSet rs) throws SQLException{
			this.classId = rs.getInt("classid");
			this.classTag = rs.getString("classtag");
			useTactic = rs.getInt("use_tactic") == 1;
			client = rs.getString("client");
			String media_classids = rs.getString("media_classids");
			if(media_classids != null){
				String tokens[] = media_classids.split(",");
				for(String x : tokens){
					x = x.trim();
					if(x.length() > 0){
						try{
							int v = Integer.parseInt(x);
							if(MediaChecker.instance().isValidMediaClass(v)){
								mcids.add(v);
							}else{
								log.warn("skip invalid class id %s", v);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}

		}
		public boolean hasSameMediaClass(HashSet<Integer> mClassSet) {
			for(int x : this.mcids){
				if(mClassSet.contains(x)){
					return true;
				}
			}
			return false;
		}
	}
	HashMap<String, UCS>ucsMap;

	public UCS getUcsByClassTag(String classTag){
		return ucsMap.get(classTag);
	}
	 UCSChecker() throws SQLException{
		ucsMap = reload();
	}

	HashMap<String, UCS> reload() throws SQLException{
		log.info("reloading UCS map");
		RDS rds = RDS.getRDSByDefine("corsair_0", "select * from fs_user_class where validtime > 0");
		HashMap<String, UCS>map = new HashMap<String, UCS>();
		try{
			ResultSet rs = rds.load();
			while(rs.next()){
				UCS ucs = new UCS(rs);
				map.put(ucs.classTag, ucs);
			}
		}finally{
			rds.close();
		}
		log.info("reloaded UCS map : " + map);
		return map;
	}
}
