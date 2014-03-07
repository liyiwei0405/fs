package com.funshion.rpcserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.funshion.search.utils.LogHelper;

public class Jetty extends Thread {
	private static LogHelper log = new LogHelper("Jetty");
	private int port;
	
	public Jetty(int port){
		this.port = port;
	}

	@Override
	public void run(){
		Server server = null;
		while(true){
			if(server != null){
				server.destroy();
			}
			log.warn("try init new instance for Jetty service");
			try {
				server = new Server(this.port);
				ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
				context.setContextPath("/");
				server.setHandler(context);
				context.addServlet(new ServletHolder(new CounterServlet()), "/counters");
				context.addServlet(new ServletHolder(new VarsServlet()), "/vars");
				server.start();
				log.warn("Jetty started using port " + this.port);
				server.join();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e, "Jetty start failed!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
