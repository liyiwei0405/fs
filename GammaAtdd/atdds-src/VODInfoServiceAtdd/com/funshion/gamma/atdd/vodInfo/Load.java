package com.funshion.gamma.atdd.vodInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.funshion.gamma.atdd.vodInfo.thrift.PlayInfoRec;
import com.funshion.gamma.atdd.vodInfo.thrift.SerialInfoRec;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.rds.RDS;

public class Load {

	static final LogHelper log = new LogHelper("load");
	static PlayInfoMap load() throws SQLException{
		log.info("reloading....");
		RDS rdsLoadSerial = RDS.getRDSByDefine("corsair_0", "select serialID, number, description, picid, mediaId from fs_media_serials");
		try{
			log.info("reload fs_media_serials");
			PlayInfoMap map = new PlayInfoMap();
			ResultSet serRs = rdsLoadSerial.executeQuery();
			while(serRs.next()){
				SerialInfoRec sRec = getSerialInfoRec(serRs);
				map.addSerialRecord(sRec);
				map.putMediaSer(serRs.getInt(5), sRec);
			}
			serRs.close();
			log.info("reload fs_playinfo");
			
			RDS rds = RDS.getRDSByDefine("corsair_0", 
					"select playinfoid, mediaid, serialid, hashid, cid, edittaskname, taskname, localfilepath, torrentfiles, fileformat, filesize, timelength, clarity, subtitle, byterate, dub_one, dub_two from fs_playinfo where publishflag='published' and source != 'web'",
					rdsLoadSerial.getMyConnection());

			ResultSet rs = rds.load();
			while(rs.next()){
				PlayInfoRec rec = getRecord(rs);
				map.addPlayInfoRecord(rec);
			}
			rds.close();
			
			map.optimizePlayInfo();
			log.info("reload ok....");
			return map;
		}finally{
			rdsLoadSerial.close();
		}
		
	}

	private static SerialInfoRec getSerialInfoRec(ResultSet serRs) throws SQLException {
		SerialInfoRec ret = new SerialInfoRec();
		ret.serialID = serRs.getInt(1);
		ret.serialNo = serRs.getInt(2);
		ret.serialName = serRs.getString(3);
		ret.serialPicID = serRs.getInt(4);
		
		return ret;
	}

	private static PlayInfoRec getRecord(ResultSet rs) throws SQLException {
		PlayInfoRec rec = new PlayInfoRec();
		rec.playInfoID = rs.getInt("playinfoid");
		rec.mediaID = rs.getInt("mediaid");
		rec.serialID = rs.getInt("serialid");
		rec.hashID = rs.getString("hashid");
		rec.cID = rs.getString("cid");
		rec.editorTaskName = rs.getString("edittaskname");
		rec.taskName = rs.getString("taskname");
		rec.localFilePath = rs.getString("localfilepath");
		rec.torrentFiles = rs.getString("torrentfiles");
		rec.fileFormat = rs.getString("fileformat");
		rec.fileSize = rs.getLong("filesize");
		rec.timeLength = rs.getInt("timelength");
		rec.clarity = rs.getString("clarity");
		rec.subtitle = rs.getString("subtitle");
		rec.byteRate = rs.getShort("byterate");
		rec.dubOne = rs.getString("dub_one");
		rec.dubTwo = rs.getString("dub_two");
		return rec;
	}
}
