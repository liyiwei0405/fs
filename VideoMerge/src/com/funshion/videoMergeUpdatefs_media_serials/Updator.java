package com.funshion.videoMergeUpdatefs_media_serials;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.rds.RDS;

public class Updator extends AbstractUpdator{
	protected Map<Integer, List<Integer>>serIds = new HashMap<Integer, List<Integer>>();

	protected void loadSerial() throws SQLException, IOException{
		RDS rdsLoad = RDS.getRDSByDefine("corsair_0", "select serialid,relatedvideo from fs_media_serials");
		ResultSet rs = rdsLoad.load();
		String now = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.SSS").format(System.currentTimeMillis());
		LineWriter lw = new LineWriter(new File("fs_media_serials___" + now), true);
		while(rs.next()){
			final int serId = rs.getInt(1);
			final String toRelatedOrg = rs.getString(2);
			if(toRelatedOrg == null){
				//				log.info("error! no relatedvideo for %s", serId);
				continue;
			}
			String toMod = toRelatedOrg.trim();
			if(toMod.length() == 0){
				//				log.info("error! no relatedvideo for %s, len=0", serId);
				continue;
			}
			String tokens[] = toMod.replace(" ", ",").split(",");
			List<Integer>lst = new ArrayList<Integer>();
			for(String x : tokens){
				x = x.trim();
				if(x.length() == 0)
					continue;
				Integer itg;
				try{
					itg = (int)Double.parseDouble(x);
				}catch(RuntimeException e){
					log.error("%s error relatedvalues %s", serId, toRelatedOrg);
					lw.close();
					throw e;
				}
				if(itg > 1000000){
					lst.add(itg);
					continue;
				}
				Integer var = videoIds.get(itg);
				if(var == null){
					log.error("has no videoId for serials %s with videoid %s", 
							serId, itg);
					continue;
				}

				lst.add(var);
			}
			serIds.put(serId, lst);
			lw.writeLine(serId + "=" + toRelatedOrg);
			lw.flush();
		}
		lw.close();
		rdsLoad.close();
	}

	protected void update() throws SQLException{
		RDS up = RDS.getRDSByDefine("corsair_0", 
				"update fs_media_serials set relatedvideo = ? where serialid = ?");
		Iterator<Entry<Integer, List<Integer>>> itr = 
				serIds.entrySet().iterator();
		while(itr.hasNext()){
			Entry<Integer, List<Integer>> e = itr.next();
			String toStr = toString(e.getValue());
			up.setString(1, toStr);
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
		Updator u = new Updator();
		u.work();
	}
}
