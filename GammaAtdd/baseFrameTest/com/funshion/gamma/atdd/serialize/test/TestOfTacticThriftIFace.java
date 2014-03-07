package com.funshion.gamma.atdd.serialize.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;

import com.funshion.gamma.atdd.tacticService.thrift.MediaIdList;
import com.funshion.gamma.atdd.tacticService.thrift.MediaTacticList;
import com.funshion.gamma.atdd.tacticService.thrift.TacticService.Iface;
import com.funshion.gamma.atdd.tacticService.thrift.*;

public class TestOfTacticThriftIFace implements Iface{

	@Override
	public MediaIdList getAvailableMedia(List<Integer> mediaIds, String ucs)
			throws TException {
		ArrayList<Integer> lst = new ArrayList<Integer>();
		lst.add(1);
		lst.add(22);
		lst.add(333);
		lst.add(4444);
		lst.add(55555);
		lst.add(666666);
		MediaIdList ret = new MediaIdList();
		ret.retCode = 200;
		ret.retMsg = "OK";
		ret.mediaIds = lst;
		return ret;
	}

	@Override
	public MediaTacticList getMediaTactic(List<Integer> mediaIds, String ucs)
			throws TException {
		List<MediaTactic> lst= new LinkedList<MediaTactic>();
		lst.add(new MediaTactic(0055555123, false));
		lst.add(new MediaTactic(2033333336, false));
		lst.add(new MediaTactic(1444444489, true));
		lst.add(new MediaTactic(1111111111, true));
		lst.add(new MediaTactic(0222222222, false));
		lst.add(new MediaTactic(0000777777, true));
		lst.add(new MediaTactic(0000765432, false));
		lst.add(new MediaTactic(2145000067, true));
		
		MediaTacticList ret = new MediaTacticList();
		ret.retCode = 200;
		ret.retMsg = "OK";
		ret.mediaTactic = lst;
		return ret;
	}

}
