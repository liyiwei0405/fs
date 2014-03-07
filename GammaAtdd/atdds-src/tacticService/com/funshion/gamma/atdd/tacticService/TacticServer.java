package com.funshion.gamma.atdd.tacticService;

import java.io.File;
import java.io.IOException;

import com.funshion.gamma.atdd.AbstractThriftService;
import com.funshion.search.utils.ConfigReader;

public class TacticServer extends AbstractThriftService{
	public TacticServer(int servicePort)
			throws Exception {
		super(servicePort, com.funshion.gamma.atdd.tacticService.thrift.TacticService.class,
				new TacticThriftIFace());
	}

	@Override
	protected void init() throws Exception {
		UCSChecker.instance();
		MediaChecker.instance();
	}

	public static void main(String[]args) throws IOException, Exception{
		File cfg = new File("./config/tactic.atdd.conf");
		ConfigReader cr = new ConfigReader(cfg, "service");
		TacticServer server = new TacticServer(cr.getInt("servicePort"));

		server.startDaemon(null);
	}
}
