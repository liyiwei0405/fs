package com.funshion.gamma.atdd.tacticService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.rds.RDS;
import com.funshion.search.utils.rds.SourceDefine;
public class MediaChecker {
	static LogHelper log = new LogHelper("medias");
	private static MediaChecker instance ;
	public synchronized static MediaChecker instance(){
		if(instance == null){
			try{
				instance = new MediaChecker();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	static{
		new Thread(){
			public void run(){
				try {
					sleep(60 * 1000);
					log.info("creating new MediaChecker");
					instance = new MediaChecker();
					log.info("creating new MediaChecker OK");
				} catch (Exception e) {
					log.error(e, "when reloading");
					e.printStackTrace();
				}
			}
		}.start();
	}

	public class MediaInfo {
		public final int mediaId;
		private final HashSet<Integer>_mClassSet = new HashSet<Integer>();
		private final HashSet<String>_tacticSet = new HashSet<String>();

		public MediaInfo(int mediaId){
			this.mediaId = mediaId;
		}
		public void addMediaClassId(int id){
			if(isValidMediaClass(id)){
				_mClassSet.add(id);
			}
		}

		public void addTacticInfo(int tacticId, String client){
			if(isValidAreaTactic(tacticId)){
				_tacticSet.add(client.trim() + tacticId);
			}
		}
		public HashSet<Integer> get_mClassSet() {
			return _mClassSet;
		}
		public boolean tacticForbidden(String client, int areaTactic) {
			return _tacticSet.contains(client.trim() + areaTactic);
		}
	}
	private Map<Integer, MediaInfo> mediaMap;
	private Set<Integer>validAreaTatics;
	private Set<Integer>validMediaClasses;
	MediaChecker() throws SQLException{
		load();
	}

	private void loadMediaMap(Connection conn) throws SQLException{
		final Map<Integer, MediaInfo>map = new HashMap<Integer, MediaInfo>();
		{
			log.info("reloading media map");
			final RDS rds = RDS.getRDSByDefine("corsair_0", "select mediaid from fs_media",
					conn);
			ResultSet rsMedias = rds.load();
			while(rsMedias.next()){
				int mediaId = rsMedias.getInt(1);
				MediaInfo mInfo = new MediaInfo(mediaId);
				map.put(mediaId, mInfo);
			}
			rds.close();
			log.info("reloading fs_media_class_relation");

			RDS rdsMCLoad = RDS.getRDSByDefine("corsair_0", 
					"select * from fs_media_class_relation order by mediaid",
					conn);
			ResultSet rdsMCLoadRs = rdsMCLoad.executeQuery();
			MediaInfo mInfo = null;
			int mid, mclass;
			while(rdsMCLoadRs.next()){
				mid = rdsMCLoadRs.getInt(1);
				mclass = rdsMCLoadRs.getInt(2);
				if(mInfo == null){
					mInfo = map.get(mid);
					if(mInfo != null){
						mInfo.addMediaClassId(mclass);
					}
				}else{
					if(mInfo.mediaId == mid){
						mInfo.addMediaClassId(mclass);
					}else{
						mInfo = map.get(mid);
						if(mInfo != null){
							mInfo.addMediaClassId(mclass);
						}
					}
				}
			}
			rdsMCLoad.close();
		}
		{
			log.info("reloading valid fs_media_area_tactic_relation");
			RDS rdsLoadAreaInfo = RDS.getRDSByDefine("corsair_0", 
					"SELECT mediaid, tactic, client FROM fs_media_area_tactic_relation WHERE tactic = 0 UNION SELECT a.mediaid, a.tactic, a.client FROM fs_media_area_tactic_relation a, fs_area_info b WHERE a.tactic = b.tactic AND b.isvalid > 0 AND b.tacticvalid > 0 AND a.client = b.client",
					conn);
			ResultSet rdsLoadAreaInfoRs = rdsLoadAreaInfo.executeQuery();
			while(rdsLoadAreaInfoRs.next()){
				int mediaId = rdsLoadAreaInfoRs.getInt("mediaid");
				//				System.out.println(mediaId);
				int tactic = rdsLoadAreaInfoRs.getInt("tactic");
				String client = rdsLoadAreaInfoRs.getString("client");
				MediaInfo valueSet = map.get(mediaId);
				if(valueSet != null){
					valueSet.addTacticInfo(tactic, client);
				}
			}
			log.info("reloading valid fs_media_area_tactic_relation OK");
			rdsLoadAreaInfo.close();
		}
		this.mediaMap = map;

	}
	public void load() throws SQLException{
		
		log.info("reloading media map");
		Connection conn = SourceDefine.instance().getDataSourceDefine("corsair_0").getNewConnection();
		try{
			loadValidMediaClasses(conn);
			loadValidAreaTactic(conn);
			loadMediaMap(conn);
		}finally{
			conn.close();
		}
	}
	private void loadValidAreaTactic(Connection conn) throws SQLException {
		log.info("reloading valid areaTactic");
		Set<Integer>validAreaTactics = new HashSet<Integer>();
		RDS rdsLoadValidAreaTactic = RDS.getRDSByDefine("corsair_0", 
				"SELECT DISTINCT(tactic) FROM fs_area_info WHERE (tacticvalid>0 AND isvalid>0) OR tactic=0",
				conn);
		ResultSet rdsLoadValidAreaTacticRs = rdsLoadValidAreaTactic.executeQuery();
		while(rdsLoadValidAreaTacticRs.next()){
			int tactic = rdsLoadValidAreaTacticRs.getInt(1);
			validAreaTactics.add(tactic);
		}
		this.validAreaTatics = validAreaTactics;
	}

	private void loadValidMediaClasses(Connection conn) throws SQLException {
		log.info("reloading valid mediaClass");
		Set<Integer>validMediaClasses = new HashSet<Integer>();
		RDS rdsLoadValidAreaTactic = RDS.getRDSByDefine("corsair_0", 
				"SELECT classid from fs_media_class where isvalid = 1",
				conn);
		ResultSet rdsLoadValidAreaTacticRs = rdsLoadValidAreaTactic.executeQuery();
		while(rdsLoadValidAreaTacticRs.next()){
			int tactic = rdsLoadValidAreaTacticRs.getInt(1);
			validMediaClasses.add(tactic);
		}
		this.validMediaClasses = validMediaClasses;
	}

	public boolean isValidMediaClass(int classid){
		return this.validMediaClasses.contains(classid);
	}
	public boolean isValidAreaTactic(int areaTactic) {
		return validAreaTatics.contains(areaTactic);
	}

	public MediaInfo getMediaInfo(int mid) {
		return this.mediaMap.get(mid);
	}

}
