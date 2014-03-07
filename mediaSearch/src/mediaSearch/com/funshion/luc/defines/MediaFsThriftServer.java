package com.funshion.luc.defines;

import org.apache.thrift.TProcessorFactory;

import com.funshion.search.FsSearchThriftServer;

public class MediaFsThriftServer extends FsSearchThriftServer{

	public MediaFsThriftServer(int port, int timeout) throws Exception {
		super(port, timeout);
	}

	@Override
	public TProcessorFactory getProcessorFacotry() {
		return SSQueryIfaceProcessorFactory.instance;
	}
}
