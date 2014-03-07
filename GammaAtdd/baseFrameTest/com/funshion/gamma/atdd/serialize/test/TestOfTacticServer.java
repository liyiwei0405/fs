package com.funshion.gamma.atdd.serialize.test;

import java.io.IOException;

import com.funshion.gamma.atdd.AbstractThriftService;

public class TestOfTacticServer extends AbstractThriftService{
	public TestOfTacticServer(int servicePort)
			throws Exception {
		super(servicePort, com.funshion.gamma.atdd.tacticService.thrift.TacticService.class,
				new TestOfTacticThriftIFace());
	}

	@Override
	protected void init() throws Exception {
		
	}

	public static void main(String[]args) throws IOException, Exception{
		TestOfTacticServer server = new TestOfTacticServer(30000);
		server.startDaemon(null);
	}
}
