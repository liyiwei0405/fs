package com.funshion.gamma.atdd.vodInfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import com.funshion.gamma.atdd.vodInfo.thrift.PlayInfoList;
import com.funshion.gamma.atdd.vodInfo.thrift.PlayInfoRec;
import com.funshion.gamma.atdd.vodInfo.thrift.SerialInfo;
import com.funshion.gamma.atdd.vodInfo.thrift.SerialInfoRec;
import com.funshion.gamma.atdd.vodInfo.thrift.VodInfoService.Iface;;

public class VodInfoServiceImp implements Iface{
	public static final VodInfoServiceImp instance = new VodInfoServiceImp();
	@Override
	public PlayInfoList getInfoByPlayIDList(List<Integer> playIDs)
			throws TException {
		PlayInfoList ret = new PlayInfoList();
		if(playIDs.size() > 500 || playIDs.size() == 0 || playIDs == null){
			ret.retCode = 400;
			ret.retMsg = "idlist was null/empty or length exceeds limit";
			ret.data = null;
			return ret;
		}

		ret.retCode = 200;
		ret.retMsg = "OK";
		int addCnt = 0;
		for(int x : playIDs){
			PlayInfoRec rec = PlayInfoMap.nowInstance().mapByPlayInfoId.get(x);
			if(ret.data == null){
				ret.data = new ArrayList<PlayInfoRec>(playIDs.size());
			}
			if(rec == null){
				ret.data.add(new PlayInfoRec());
			}else{
				addCnt ++;
				ret.data.add(rec);
			}
		}

		if(addCnt == 0){
			ret.retCode = 404;
			ret.retMsg = "got no result for the id(s)";
			ret.data = null;
		}

		return ret;
	}

	@Override
	public PlayInfoList getInfoByHashIDList(List<String> hashIDs)
			throws TException {
		PlayInfoList ret = new PlayInfoList();
		if(hashIDs.size() > 500 || hashIDs.size() == 0 || hashIDs == null){
			ret.retCode = 400;
			ret.retMsg = "idlist was null/empty or length exceeds limit";
			ret.data = null;
			return ret;
		}

		ret.retCode = 200;
		ret.retMsg = "OK";
		int addCnt = 0;
		for(String x : hashIDs){
			PlayInfoRec rec = PlayInfoMap.nowInstance().mapByHashId.get(x);
			if(ret.data == null){
				ret.data = new ArrayList<PlayInfoRec>(hashIDs.size());
			}
			if(rec == null){
				ret.data.add(new PlayInfoRec());
			}else{
				addCnt ++;
				ret.data.add(rec);
			}
		}

		if(addCnt == 0){
			ret.retCode = 404;
			ret.retMsg = "got no result for the id(s)";
			ret.data = null;
		}

		return ret;
	}

	@Override
	public PlayInfoList getInfoByMediaID(int mediaId)
			throws TException {
		List<PlayInfoRec> lst = PlayInfoMap.nowInstance().mapByMediaId.get(mediaId);

		PlayInfoList ret = new PlayInfoList();
		ret.retCode = 200;
		ret.retMsg = "OK";

		if(lst == null){
			ret.retCode = 404;
			ret.retMsg = "got no result for the id(s)";
			ret.data = null;
		}else{
			//			if(ascOrder){
			ret.data = lst;
			//			}else{
			//				ArrayList<PlayInfoRec>lst2 = new ArrayList<PlayInfoRec>(lst.size());
			//				for(int x = lst.size() - 1; x >= 0; x --){
			//					lst2.add(lst.get(x));
			//				}
			//				ret.data = lst2;
			//			}
		}
		return ret;
	}

	@Override
	public SerialInfo getSerialInfoByMediaID(int mediaID)
			throws TException {
		List<SerialInfoRec> lst = PlayInfoMap.nowInstance().mapSerByMediaId.get(mediaID);

		SerialInfo ret = new SerialInfo();
		ret.retCode = 200;
		ret.retMsg = "OK";

		if(lst == null){
			ret.retCode = 404;
			ret.retMsg = "got no result for the id(s)";
			ret.data = null;
		}else{
			ret.data = lst;
			//			}else{
			//				ArrayList<SerialInfoRec>lst2 = new ArrayList<SerialInfoRec>(lst.size());
			//				for(int x = lst.size() - 1; x >= 0; x --){
			//					lst2.add(lst.get(x));
			//				}
			//				ret.data = lst2;
			//			}
		}
		return ret;
	}
}
