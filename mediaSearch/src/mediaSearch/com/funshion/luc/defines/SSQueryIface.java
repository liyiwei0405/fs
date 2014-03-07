package com.funshion.luc.defines;

import org.apache.thrift.TException;

import com.funshion.retrieve.media.thrift.MediaRetrieveResult;
import com.funshion.retrieve.media.thrift.MediaSearchService.Iface;
import com.funshion.retrieve.media.thrift.RetrieveStruct;
import com.funshion.search.utils.LogHelper;


public class SSQueryIface implements Iface{
	public static final int VERSION = 1;
	public static final LogHelper log = new LogHelper("MediaQueryIface");
	final String rmtIp;
	final int rmtPort;
	public SSQueryIface(String rmtIp, int rmtPort) {
		this.rmtIp = rmtIp;
		this.rmtPort = rmtPort;
	}
	
	//least recent useage
	public static int rand(int max){
		int idx = (int) (System.nanoTime() % max);
		return idx;
	}

	@Override
	public MediaRetrieveResult retrive1(RetrieveStruct qs) throws TException {
		if(qs.ver != VERSION){
			String retMsg = "version mismatch! my version is " + VERSION;
			log.error(retMsg);
			MediaRetrieveResult vrfail = new MediaRetrieveResult();
			vrfail.retCode = 301;
			vrfail.retMsg = retMsg;
			vrfail.usedTime = 0;
			return vrfail;
		}
		SSearcher vs = (SSearcher) SSIndexableFS.getInstance().getSearcher();
		long startMs = System.currentTimeMillis();
		if(vs != null){
			try {
				return vs.query(qs);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e, "when search %s", qs);
				MediaRetrieveResult vrfail = new MediaRetrieveResult();
				vrfail.retCode = 502;
				vrfail.retMsg = e.toString();
				vrfail.usedTime = System.currentTimeMillis() - startMs;
				return vrfail;
			}
		}else{
			String retMsg = "can not get Search instance!";
			log.error(retMsg);
			MediaRetrieveResult vrfail = new MediaRetrieveResult();
			vrfail.retCode = 501;
			vrfail.retMsg = retMsg;
			vrfail.usedTime = System.currentTimeMillis() - startMs;
			return vrfail;
			
		}
	}
}
