package com.funshion.updatefs_specailAndfs_auto_recommend;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.rds.RDS;
import com.funshion.videoMergeUpdatefs_media_serials.AbstractUpdator;

public class FsSpecialUpdator extends AbstractUpdator{
	private static String NORMAL_TYPE = 
			"ent_ba, ent_focus_pic, ent_home0, ent_movie, ent_music, ent_prevue, ent_society, ent_star, ent_top, ent_tv, ent_variety, mobile_video, mobile_videospecial, pad_choice_ent, pad_choice_sport, pad_extend_video, phone_choice_vspecia, phone_choice_video, pad_extend_vspecial, sport_focus, sport_special, sport_weektop";
	private static String NEWS_TYPE = 
			"news_domestic, news_fftitle, news_finance, news_funfocus, news_intnal, news_kankan, news_law, news_military, news_opr_slide, news_program, news_shanghai, news_society, mobile_day_hot";

	private static Set<String> normalSet = new HashSet<String>();
	private static Set<String> newsSet = new HashSet<String>();
	static{
		String[] normals = NORMAL_TYPE.replace(" ", "").split(",");
		for(String type : normals){

			System.out.println("normal:" + type);
			normalSet.add(type);
		}
		String[] news = NEWS_TYPE.replace(" ", "").split(",");
		for(String type : news){
			System.out.println("news:" + type);
			newsSet.add(type);
		}
	}
	private static int errCount = 0;

	protected Map<Integer, List<Integer>>recordsMap = new HashMap<Integer, List<Integer>>();

	protected void loadSerial() throws SQLException, IOException{
		RDS rdsLoad = RDS.getRDSByDefine("corsair_0", "select id, relate_type, relate_id from fs_special");
		ResultSet rs = rdsLoad.load();
		String now = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.SSS").format(System.currentTimeMillis());
		LineWriter lw = new LineWriter(new File("fs_special___" + now), true);

		while(rs.next()){
			final int majorKey = rs.getInt(1);
			final String relateType = rs.getString(2);
			final String relateIdStr = rs.getString(3);

			if(relateIdStr == null){
				//				log.info("error! no relatedvideo for %s", serId);
				continue;
			}
			String toMod = relateIdStr.trim();
			if(toMod.length() == 0){
				//				log.info("error! no relatedvideo for %s, len=0", serId);
				continue;
			}

			Map<Integer, Integer> idsMap;
			if(normalSet.contains(relateType)){
				idsMap = this.videoIds;
			}else if(newsSet.contains(relateType)){
				idsMap = this.newsIds;
			}else{
				System.out.println("skip " + relateType);
				continue;
			}
			String tokens[] = toMod.replace(" ", ",").split(",");
			List<Integer>newIdsList = new ArrayList<Integer>();
			boolean canRewrite = true;
			for(String sOldId : tokens){
				sOldId = sOldId.trim();
				if(sOldId.length() == 0)
					continue;
				Integer oldId;
				try{
					oldId = (int)Double.parseDouble(sOldId);
				}catch(RuntimeException e){
					canRewrite = false;
					errCount ++;
					log.error("not number, id: %s, relate_id: %s", majorKey, relateIdStr);
					break;
				}
//				if(oldId > 1000000){
//					newIdsList.add(oldId);
//					continue;
//				}
				Integer newId = idsMap.get(oldId);
				if(newId == null){
					log.error("has no videoId for serials %s with videoid %s", 
							majorKey, oldId);
					continue;
				}
				newIdsList.add(newId);
			}
			if(canRewrite){
				recordsMap.put(majorKey, newIdsList);
				lw.writeLine(majorKey + "=" + relateIdStr);
				lw.flush();
			}
		}
		lw.close();
		rdsLoad.close();
	}

	protected void update() throws SQLException{
		RDS up = RDS.getRDSByDefine("corsair_0", 
				"update fs_special set relate_id = ? where id = ?");
		Iterator<Entry<Integer, List<Integer>>> itr = 
				recordsMap.entrySet().iterator();
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
		loadVideoids(newsIds, "news");
		log.warn("load serial");
		loadSerial();
		log.warn("update");
		update();
		log.warn("done! error total: " + errCount);
	}

	public static void main(String[]args) throws Exception{
		FsSpecialUpdator u = new FsSpecialUpdator();
		u.work();
	}
}
