package com.funshion.gamma.atdd.tacticService;

import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.tacticService.MediaChecker.MediaInfo;
import com.funshion.gamma.atdd.tacticService.UCSChecker.UCS;
import com.funshion.gamma.atdd.tacticService.thrift.MediaIdList;
import com.funshion.gamma.atdd.tacticService.thrift.MediaTactic;
import com.funshion.gamma.atdd.tacticService.thrift.MediaTacticList;

public class UCSInput {
	public final String classTag;
	public final int areaTactic;

	private UCSInput(final String classTag, final int areaTactic){
		this.classTag = classTag;
		this.areaTactic = areaTactic;
	}

	public static UCSInput parseUCSStr(String ucsInputStr){
		ucsInputStr = ucsInputStr.trim();
		String classTag = ucsInputStr.substring(0, 3);
		int areaTactic = Integer.parseInt(ucsInputStr.substring(3, ucsInputStr.length() - 1));
		return new UCSInput(classTag, areaTactic);
	}
	public static void check(String ucsStr, Object ret, List<Integer>mediaIds) throws Exception{
		ret.getClass().getField("retMsg").set(ret, "Ok");
		int retCode = 200;;
		UCS ucs = null;
		UCSInput input = null;
		if(mediaIds.size() > 1000){
			ret.getClass().getField("retMsg").set(ret, "mediaid list is too long");
			retCode = 404;
		}else{
			
			try{
				input = parseUCSStr(ucsStr);
			}catch(Exception e){
				ret.getClass().getField("retCode").setInt(ret, 404);
				ret.getClass().getField("retMsg").set(ret, "Unavilable ucs");
				return;
			}
			if(!MediaChecker.instance().isValidAreaTactic(input.areaTactic)){
				ret.getClass().getField("retMsg").set(ret, "Unavilable ucs");
				retCode = 404;
			}else{
				ucs = UCSChecker.instance().getUcsByClassTag(input.classTag);
				if(ucs == null){
					ret.getClass().getField("retMsg").set(ret, "Unavilable ucs");
					retCode = 404;
				}
			}
		}
		ret.getClass().getField("retCode").setInt(ret, retCode);
		if(retCode != 200){
			return;
		}
		final boolean isMediaIdList;
		if(ret instanceof MediaIdList){
			((MediaIdList)ret).mediaIds = new ArrayList<Integer>();
			isMediaIdList = true;
		}else if(ret instanceof MediaTacticList){
			((MediaTacticList)ret).mediaTactic = new ArrayList<MediaTactic>();
			isMediaIdList = false;
		}else{
			ret.getClass().getField("retCode").setInt(ret, 500);
			ret.getClass().getField("retMsg").set(ret, "invalid queryRetClass " + ret.getClass());
			return;
		}

		for(int index = 0; index < mediaIds.size(); index ++){
			int mediaId = mediaIds.get(index);
			boolean accept = checkAccept(mediaId, ucs, input.areaTactic);
			if(isMediaIdList){
				if(accept){
					((MediaIdList)ret).addToMediaIds(mediaId);
				}
			}else{
				MediaTactic mt = new MediaTactic();
				mt.hasTactic = !accept;
				mt.mediaid = mediaId;
				((MediaTacticList)ret).addToMediaTactic(mt);
			}
		}
	}

	public static boolean checkAccept(int mid, UCS ucs, int areaTactic){
		MediaInfo minfo = MediaChecker.instance().getMediaInfo(mid);
		if(minfo == null){
			return false;
		}else{
			if(!ucs.hasSameMediaClass(minfo.get_mClassSet())){
				return false;
			}else{
				if(ucs.useTactic){
					if(minfo.tacticForbidden(ucs.client, areaTactic)){
						return false;
					}else{
						return true;
					}
				}else{
					return true;
				}
			}
		}
	}
}
