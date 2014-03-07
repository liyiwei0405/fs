package com.funshion.videoService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.funshion.search.utils.LogHelper;
import com.funshion.videoService.thrift.FileStruct;
import com.funshion.videoService.thrift.VideoletInfo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class VideoletInfoLoader {
	private static final LogHelper log = new LogHelper("DataLoader");
	private static final VideoletInfoLoader loader = new VideoletInfoLoader();
	private VideoletInfo videoletInfo;
	//装载一条记录
	public static VideoletInfo loadDBObject(DBObject dbObject) throws Exception {
		Object oVideoid = dbObject.get("videoid");

		
		int videoid = (oVideoid == null ? -1 :(int)Double.parseDouble(oVideoid.toString()));
		if(videoid >= 0){
			return loader.load(dbObject, videoid);
		}else{
			log.error("A videoid is null or smaller than 0!");
		}
		return null;
	}
	private VideoletInfo load(DBObject dbObject, int videoid) throws Exception{

		videoletInfo = new VideoletInfo();
		videoletInfo.videoId = videoid;
		videoletInfo.mapId = parseInt(dbObject.get("mapid"));
		videoletInfo.vId = parseString(dbObject.get("vid"));
		videoletInfo.source = parseString(dbObject.get("source"));
		videoletInfo.cls = parseString(dbObject.get("class"));
		videoletInfo.title = parseString(dbObject.get("title"));
		videoletInfo.picturePath = parseString(dbObject.get("picturepath"));
		videoletInfo.timeLen = parseInt(dbObject.get("timelen"));
		videoletInfo.plots = parseString(dbObject.get("plots"));
		videoletInfo.types =  parseListString(dbObject.get("types"));
		videoletInfo.tags = parseListString(dbObject.get("tags"));
		videoletInfo.files = parseListFileStruct(dbObject.get("files"));
		videoletInfo.relateVideoIds = parseListInteger(dbObject.get("relate_video_ids"));
		videoletInfo.relateSpecialIds = parseListInteger(dbObject.get("relate_special_ids"));
		videoletInfo.videoEventIds = parseListInteger(dbObject.get("video_event_ids"));
		videoletInfo.score = parseDouble(dbObject.get("score"));
		videoletInfo.scoreNum = parseInt(dbObject.get("scorenum"));
		videoletInfo.commentNum = parseInt(dbObject.get("commentnum"));
		videoletInfo.dayNum = parseInt(dbObject.get("daynum"));
		videoletInfo.weekNum = parseInt(dbObject.get("weeknum"));
		videoletInfo.monthNum = parseInt(dbObject.get("monthnum"));
		videoletInfo.playNum = parseInt(dbObject.get("playnum"));
		videoletInfo.dayIndex = parseInt(dbObject.get("day_index"));
		videoletInfo.weekIndex = parseInt(dbObject.get("week_index"));
		videoletInfo.monthIndex = parseInt(dbObject.get("month_index"));
		videoletInfo.playIndex = parseInt(dbObject.get("play_index"));
		videoletInfo.videoIndexes = parseString(dbObject.get("video_index"));
		videoletInfo.showAd = parseInt(dbObject.get("showad"));
		videoletInfo.votable = parseInt(dbObject.get("votable"));
		videoletInfo.ordering = parseInt(dbObject.get("ordering"));
		videoletInfo.publishFlag = parseString(dbObject.get("publishflag"));
		videoletInfo.createDate = parseInt(dbObject.get("createdate"));
		videoletInfo.modifyDate = parseInt(dbObject.get("modifydate"));
		videoletInfo.lastNum = parseInt(dbObject.get("lastnum"));
		videoletInfo.lastIndex = parseInt(dbObject.get("last_index"));
		videoletInfo.isVideo = parseInt(dbObject.get("isvideo"));
		videoletInfo.recommend = parseInt(dbObject.get("recommend"));
		videoletInfo.isRank = parseInt(dbObject.get("isrank"));
		videoletInfo.playNumInit = parseInt(dbObject.get("playnum_init"));
		videoletInfo.commentNumFunshion = parseMapStringInteger(dbObject.get("comment_num_funshion"));
		videoletInfo.transmitNum = parseInt(dbObject.get("transmit_num"));
		videoletInfo.relateMediaIds = parseListInteger(dbObject.get("video_media_ids"));
		videoletInfo.relateStaffIds = parseListInteger(dbObject.get("video_staff_ids"));
		videoletInfo.userId = parseInt(dbObject.get("userid"));
		videoletInfo.userName = parseString(dbObject.get("username"));
		videoletInfo.moduleId = parseInt(dbObject.get("moduleid"));
		videoletInfo.position = parseString(dbObject.get("position"));
		videoletInfo.picF = parseString(dbObject.get("pic_f"));
		videoletInfo.picV = parseString(dbObject.get("pic_v"));
		videoletInfo.isBottomRecommend = parseInt(dbObject.get("is_bottom_recommend"));
		videoletInfo.recommendDate = parseInt(dbObject.get("recommenddate"));
		videoletInfo.tacticArea = parseMapStringListInteger(dbObject.get("tactic_area"));
		videoletInfo.videoContentClasses = parseMapStringListInteger(dbObject.get("video_content_classes"));
		videoletInfo.tagIds = parseListInteger(dbObject.get("tagids"));
		videoletInfo.hot = parseDouble(dbObject.get("hot"));
		videoletInfo.copyright = parseString(dbObject.get("copyright"));
		videoletInfo.picTitleModifydate = parseInt(dbObject.get("pic_title_modifydate"));
		videoletInfo.extend = parseString(dbObject.get("extend"));
		
		return videoletInfo;
	
	}
	private int parseInt(Object obj) {	//null默认0，不是Number抛异常
		if(obj == null){
			return 0;
		}
		if(obj instanceof Integer){
			return (Integer)obj;
		}
		return (int)parseDouble(obj);
	}
	
	private double parseDouble(Object obj){	//null默认0，不是Number抛异常
		return obj == null ? 0 : Double.parseDouble(obj.toString());	
	}

	private String parseString(Object o){
		return o == null ? "" : o.toString();
	}

	//解析FileStruct型的对象
	private FileStruct parseFileStruct(Object obj) throws Exception{	//null返回null
		if(obj == null){
			return new FileStruct();
		}
		FileStruct file = new FileStruct();
		BasicDBObject basicDBObj = (BasicDBObject)obj;	//if not instanceof BasicDBObject抛异常

		file.setBitRateKbps(parseInt(basicDBObj.get("bitrate")));
		String sClarity = parseString(basicDBObj.get("clarity"));
		if(! (sClarity.isEmpty() 
				|| sClarity.equals("shot") 
				|| sClarity.equals("tv") 
				|| sClarity.equals("dvd") 
				|| sClarity.equals("high-dvd") 
				|| sClarity.equals("super-dvd"))){
			throw new Exception("clarity unknown type: " + sClarity+ " for videoid: " + videoletInfo.videoId);
		}else{
			file.setClarity(sClarity);
		}
		file.setFileFormat(parseString(basicDBObj.get("fileformat")));
		file.setFileName(parseString(basicDBObj.get("filename")));
		file.setFileSizeByte(parseInt(basicDBObj.get("filesize")));
		file.setHashId(parseString(basicDBObj.get("hashid")));
		return file;
	}
	
	@SuppressWarnings("rawtypes")
	private List<Integer> parseListInteger(Object obj) throws Exception{
		if(obj == null){
			return new ArrayList<Integer>(0);
		}
		List<Integer> retList = null;
		if(obj instanceof List){
			List oList = (List)obj;
			retList = new ArrayList<Integer>(oList.size());
			for(Object o : oList){
				try{
					retList.add(parseInt(o));
				}catch(Exception e){
					log.error(e, "error int value '%s' for videoId %s", o, videoletInfo.videoId);
				}
			}
		}else if(obj instanceof BasicDBObject){
			BasicDBObject dbObj = (BasicDBObject)obj;
			Collection<Object> c = dbObj.values();
			retList = new ArrayList<Integer>(c.size());
			for(Object o : c){
				try{
					retList.add(parseInt(o));
				}catch(Exception e){
					log.error(e, "error int value '%s' for videoId %s", o, videoletInfo.videoId);
				}
			}
		}else if(obj instanceof String && ((String)obj).length() == 0){
			return new ArrayList<Integer>(0);
		}else{
			throw new Exception("Unknown data type(should be List): " + obj.getClass() + "  obj: " + obj+ " for videoid: " + videoletInfo.videoId);
		}
		return retList;
	}
	//解析List<String>型的对象，若数据类型与设想不一致抛异常
	@SuppressWarnings("rawtypes")
	private List<String> parseListString(Object obj) throws Exception{
		if(obj == null){
			return new ArrayList<String>(0);
		}
		List<String> retList = null;

		if(obj instanceof List){
			List oList = (List)obj;
			retList = new ArrayList<String>(oList.size());
			for(Object o : oList){
				retList.add(parseString(o));
			}
		}else if(obj instanceof BasicDBObject){
			BasicDBObject dbObj = (BasicDBObject)obj;
			Collection<Object> c = dbObj.values();
			retList = new ArrayList<String>(c.size());
			for(Object o : c){
				retList.add(parseString(o));
			}
		}else if(obj instanceof String){
			retList = new ArrayList<String>(1);
			retList.add(obj.toString());
		}else{
			throw new Exception("Unknown data type(should be List): " + obj.getClass() + "  obj: " + obj+ " for videoid: " + videoletInfo.videoId);
		}
		return retList;
	}

	//解析List<FileStruct>型的对象，若数据类型与设想不一致抛异常
	@SuppressWarnings("rawtypes")
	private List<FileStruct> parseListFileStruct(Object obj) throws Exception{
		if(obj == null){
			return new ArrayList<FileStruct>(0);
		}
		List<FileStruct> retList = null;

		if(obj instanceof List){
			List oList = (List)obj;
			retList = new ArrayList<FileStruct>(oList.size());
			for(Object o : oList){
				retList.add(parseFileStruct(o));
			}
		}else if(obj instanceof BasicDBObject){
			BasicDBObject dbObj = (BasicDBObject)obj;
			Collection<Object> c = dbObj.values();
			retList = new ArrayList<FileStruct>(c.size());
			for(Object o : c){
				retList.add(parseFileStruct(o));
			}
		}else if(obj instanceof String && ((String)obj).length() == 0){
			return new ArrayList<FileStruct>(0);
		}else{
			throw new Exception("Unknown data type(should be List): " + obj.getClass() + "  obj: " + obj+ " for videoid: " + videoletInfo.videoId);
		}
		return retList;
	}
	
	private Map<String,Integer> parseMapStringInteger(Object obj) throws Exception{
		if(obj == null){
			return new HashMap<String, Integer>(0);
		}
		Map<String,Integer> retMap = null;
		
		if(obj instanceof BasicDBObject){
			BasicDBObject basicDBObj = (BasicDBObject)obj;
			retMap = new HashMap<String, Integer>(basicDBObj.size());
			for(Entry<String, Object> entry : basicDBObj.entrySet()){
				retMap.put(parseString(entry.getKey()), parseInt(entry.getValue()));
			}
		}else if(obj instanceof BasicDBList && ((BasicDBList)obj).size() == 0){
			return new HashMap<String, Integer>(0);
		}else{
			throw new Exception("Unknown data type(should be BasicDBObject): " + obj.getClass() + "  obj: " + obj+ " for videoid: " + videoletInfo.videoId);
		}
		return retMap;
	}
	
	private Map<String,List<Integer>> parseMapStringListInteger(Object obj) throws Exception{
		if(obj == null){
			return new HashMap<String,List<Integer>>(0);
		}
		Map<String,List<Integer>> retMap = null;
		
		if(obj instanceof BasicDBObject){
			BasicDBObject basicDBObj = (BasicDBObject)obj;
			retMap = new HashMap<String,List<Integer>>(basicDBObj.size());
			for(Entry<String, Object> entry : basicDBObj.entrySet()){
				retMap.put(parseString(entry.getKey()), parseListInteger(entry.getValue()));
			}
		}else if(obj instanceof BasicDBList && ((BasicDBList)obj).size() == 0){
			return new HashMap<String,List<Integer>>(0);
		}else{
			throw new Exception("Unknown data type(should be BasicDBObject): " + obj.getClass() + "  obj: " + obj + " for videoid: " + videoletInfo.videoId);
		}
		return retMap;
	}
	
	public static boolean isDel(VideoletInfo info){
		return !info.publishFlag.equalsIgnoreCase("published");
	}
}
