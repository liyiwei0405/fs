package com.funshion.ucs;

import java.io.IOException;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
import com.funshion.ucs.exceptions.ErrorIpFormatException;

public class Func {
	public static ConfigReader getMysqlCr() throws IOException{
		return new ConfigReader(ConfUtils.getConfFile("ucs.conf"), "mysql");
	}
	public static ConfigReader getRedisCr() throws IOException{
		return new ConfigReader(ConfUtils.getConfFile("ucs.conf"), "redis");
	}

	/**
	 * @purpose: IP地址转换成整数方法
	 * @return int
	 * @throws Exception 
	 */
	public static long ip2Long(String ipStr) throws ErrorIpFormatException{
		if(ipStr == null || ipStr.length() < 7){
			throw new ErrorIpFormatException(ipStr);
		}
		String[] parts = ipStr.trim().split("\\.");
		if(parts.length != 4){
			throw new ErrorIpFormatException(ipStr);
		}
		return (segment2Int(parts[0]) * 16777216L) + (segment2Int(parts[1]) << 16) + (segment2Int(parts[2]) << 8) + (segment2Int(parts[3]));
	}

	private static int segment2Int(String str) throws ErrorIpFormatException{
		int ret = 0;
		try{
			ret = Integer.parseInt(str);
		}catch(Exception e){
			throw  new ErrorIpFormatException(str);
		}
		if(ret < 0 || ret > 255){
			throw new ErrorIpFormatException(str);
		}
		return ret;
	}

	/**
	 * 删除字符串右侧空格
	 * @param str {string} 源字符串
	 * @param char {string} 要一并删除的字符
	 * @return {string}
	 */
	public static String rtrim(String str, char c){
		if(str.length() < 1) {
			return "";
		}
		int i;
		for (i = str.length() - 1; i >= 0; i --) {
			if (str.charAt(i) != c) {
				break;
			}
		}
		return str.substring(0, i + 1);
	}

	public static void main(String[] args) throws IOException {
		while(true){
			String x = Consoler.readString("ip:");
			try{
				long var = ip2Long(x);
				System.out.println(var + ":" + ((int)var));
			}catch(Exception e){
				e.printStackTrace();
			}

		}

	}
}
