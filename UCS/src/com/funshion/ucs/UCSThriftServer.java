package com.funshion.ucs;

import org.apache.thrift.TProcessorFactory;

import com.funshion.search.FsSearchThriftServer;
import com.funshion.search.utils.ConfigReader;


public class UCSThriftServer extends FsSearchThriftServer{
	
	public UCSThriftServer(ConfigReader cr) throws Exception{
		super(cr);
	}
	public UCSThriftServer(int port, int timeout) throws Exception {
		super(port, timeout);
	}

	@Override
	public TProcessorFactory getProcessorFacotry() {
		return UCSProcessorFactory.instance;
	}
}
