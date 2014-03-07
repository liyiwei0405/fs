package com.funshion.ucs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LogHelper;
import com.funshion.ucs.exceptions.ErrorIpFormatException;

public class IpLocation {
	private final LogHelper log = new LogHelper("IpLocation");
	private final File localIpFile = new File("./data/funshion.city.dat");
	private final File foreignIpFile= new File("./data/funshion.country.dat");
	
	private List<IpSection> ipTable; 
	private List<IpSection> ipForeignTable;

	private IpLocation(){}
	public static final IpLocation instance = new IpLocation();
	
	/**
	 * @purpose: 加载IP地址库文件至内存中
	 * @param isForeign
	 * @throws Exception 
	 */
	public void loadIPData(boolean isForeign){
		List<IpSection> ipTable = new ArrayList<IpSection>();
		LineReader lr = null;
		try {
			if (! isForeign) {
				lr = new LineReader(localIpFile, "utf-8");
			}else{
				lr = new LineReader(foreignIpFile, "utf-8");
			}
			String line = "";
			while((line = lr.readLine()) != null){
				String[] fields = line.split(",");
				if(fields.length >= 5){
					try{
						ipTable.add(new IpSection(true, fields[0], fields[1], fields[2], fields[3], fields[4], ""));
					}catch(ErrorIpFormatException e){
						log.error(e.getMessage());
					}
				}else{
					log.error(".dat has fields smaller than 5, pass");
				}
			}
			if(! isForeign){
				this.ipTable = ipTable;
				log.info("load localIpFile done, size: " + this.ipTable.size());
				System.out.println(this.ipTable.get(10));
			}else{
				this.ipForeignTable = ipTable;
				log.info("load foreignIpFile done, size: " + this.ipForeignTable.size());
				System.out.println(this.ipForeignTable.get(11));
			}
		} catch (IOException e) {
			log.fatal(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} finally{
			if(lr != null){
				lr.close();
			}
		}
	}

	/**
	 * @purpose: 根据IP地址从国内IP库中获取相应的IP地域信息
	 * @param ipLong IP地址
	 * @throws Exception 
	 * @returns IpSection
	 */
	public IpSection getIpSection(long ipLong){
		return findIp(ipTable, ipLong);
	}
	
	public IpSection getForeignIpInfo(long ipLong){
		return findIp(ipForeignTable, ipLong);
	}
	
	/**
	 * @purpose: 查找IP地址库方法
	 * @param ipTable 所用IP库
	 * @param ipLong IP地址
	 * @throws Exception 
	 * @returns IpSection
	 */
	private IpSection findIp(List<IpSection> ipTable, long ipLong) {
		int high = ipTable.size() - 1;
		int mid, low = 0;
		IpSection ipSection;

		while (low <= high) {
			mid = Math.round(low + (high - low) / 2);
			// get the object of middle potions of table
			ipSection = ipTable.get(mid);
			if (ipLong < ipSection.ipStart) {
				high = mid - 1;
			} else if (ipLong > ipSection.ipEnd) {
				low = mid + 1;
			} else {
				return ipSection;
			}
		}
		return new IpSection(false);
	}

	public class IpSection {
		public boolean isValid;
		public long ipStart;
		public long ipEnd;
		public String country;
		public String province;
		public String city;
		public String isp;

		public IpSection(boolean isValid, String ipStart, String ipEnd, String country,
				String province, String city, String isp) throws ErrorIpFormatException {
			this.isValid = isValid;
			this.ipStart = Func.ip2Long(ipStart);
			this.ipEnd = Func.ip2Long(ipEnd);
			this.country = country;
			this.province = province;
			this.city = city;
			this.isp = isp;
		}
		public IpSection(boolean isValid){
			this.isValid = isValid;
		}
		
		@Override
		public String toString(){
			return "isValid:" + String.valueOf(this.isValid) + ",ipStart:" + String.valueOf(this.ipStart) + ",ipEnd:" + String.valueOf(this.ipEnd) + ",country:" + this.country + ",province:" + this.province + ",city:" + this.city + ",isp:" + this.isp;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
