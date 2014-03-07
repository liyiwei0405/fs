package com.funshion.gamma.atdd.tacticService;

import java.util.List;

import org.apache.thrift.TException;

import com.funshion.gamma.atdd.tacticService.thrift.MediaIdList;
import com.funshion.gamma.atdd.tacticService.thrift.MediaTacticList;
import com.funshion.gamma.atdd.tacticService.thrift.TacticService.Iface;

public class TacticThriftIFace implements Iface{

	@Override
	public MediaIdList getAvailableMedia(List<Integer> mediaIds, String ucs)
			throws TException {
		MediaIdList ret = new MediaIdList();
		try{
			UCSInput.check(ucs, ret, mediaIds);
		}catch(Exception e){
			ret.retCode = 500;
			ret.retMsg = "innerError: " + e;
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public MediaTacticList getMediaTactic(List<Integer> mediaIds, String ucs)
			throws TException {
		MediaTacticList ret = new MediaTacticList();
		try{
			UCSInput.check(ucs, ret, mediaIds);
		}catch(Exception e){
			ret.retCode = 500;
			ret.retMsg = "innerError: " + e;
		}
		return ret;
	}

}
