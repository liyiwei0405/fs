package com.funshion.updatefs_specailAndfs_auto_recommend;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.rds.RDS;
import com.funshion.videoMergeUpdatefs_media_serials.AbstractUpdator;

public class FsAutoRecommendUpdator extends AbstractUpdator{

	protected Map<Integer, Integer>recordsMap = new HashMap<Integer, Integer>();

	protected void loadSerial() throws SQLException, IOException{
		RDS rdsLoad = RDS.getRDSByDefine("corsair_0", "select id, type, mediaid from fs_auto_recommend");
		ResultSet rs = rdsLoad.load();
		String now = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.SSS").format(System.currentTimeMillis());
		LineWriter lw = new LineWriter(new File("fs_auto_recommend___" + now), true);

		while(rs.next()){
			final int majorKey = rs.getInt(1);
			final String type = rs.getString(2);
			if(! type.equals("manual_intervene_video")){
				continue;
			}
			final int oldId = rs.getInt(3);

			if(oldId > 1000000){
				continue;
			}
			Integer newId = videoIds.get(oldId);
			if(newId == null){
				log.debug("has no videoId for serials %s with videoid %s", 
						majorKey, oldId);
				continue;
			}

			recordsMap.put(majorKey, newId);
			lw.writeLine(majorKey + "=" + newId.toString());
			lw.flush();
		}
		lw.close();
		rdsLoad.close();
	}

	protected void update() throws SQLException{
		RDS up = RDS.getRDSByDefine("corsair_0", 
				"update fs_auto_recommend set mediaid = ? where id = ?");
		Iterator<Entry<Integer, Integer>> itr = 
				recordsMap.entrySet().iterator();
		while(itr.hasNext()){
			Entry<Integer, Integer> e = itr.next();
			up.setInt(1, e.getValue());
			up.setInt(2, e.getKey());
			up.execute();
		}
		up.close();
	}

	protected void work() throws Exception{
		log.warn("load videoids");
		loadVideoids(videoIds, "normal");
		log.warn("load serial");
		loadSerial();
		log.warn("update");
		update();
		log.warn("done!");
	}

	public static void main(String[]args) throws Exception{
		FsAutoRecommendUpdator u = new FsAutoRecommendUpdator();
		u.work();
	}
}
