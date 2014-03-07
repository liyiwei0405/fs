package com.funshion.ucs.DAO;

import java.io.IOException;

import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.ucs.Func;

import redis.clients.jedis.Jedis;

public class MacRfm {
	private final LogHelper log = new LogHelper("MacRfm");
	private ConfigReader cr;
	
	private MacRfm(){
		try{
			this.cr = Func.getRedisCr();
		}catch(IOException e){
			log.fatal(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	public static MacRfm instance = new MacRfm();
	
	public String getRfm(String mac){
		Jedis jedis = null;
		try{
			jedis = new Jedis(cr.getValue("ip"), cr.getInt("port"), cr.getInt("timeout"));
			log.warn("jedis: %s, %s, timeout: %s", cr.getValue("ip"), cr.getInt("port"), cr.getInt("timeout"));
			jedis.connect();
			return jedis.get("ucs:string:mac:" + mac);
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			return null;
		}finally{
			jedis.disconnect();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
