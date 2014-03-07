package com.funshion.gamma.atdd.healthWatcher;

import java.util.List;

public class RpcLocationsFromConfigCenter{

	public static class LocationInfos{
		private List<ServiceInstanceLocationInfo>server;
		public List<ServiceInstanceLocationInfo> getServerInstances() {
			return server;
		}
		public void setServer(List<ServiceInstanceLocationInfo> server) {
			this.server = server;
		}
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			int x = 0;
			for(ServiceInstanceLocationInfo s: server){
				if(x++ > 0){
					sb.append(",\n");	
				}else{
					sb.append('\n');
				}
				sb.append(s);
				
			}
			sb.append('\n');
			sb.append(']');
			return sb.toString();
		}
		
	}

	private int retCode;
	private String retMsg;
	private LocationInfos result;
	public RpcLocationsFromConfigCenter(){}
	public int getRetCode() {
		return retCode;
	}
	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public LocationInfos getResult() {
		return result;
	}
	public void setResult(LocationInfos result) {
		this.result = result;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("retCode = " + retCode);
		sb.append("\nretMsg = " + retMsg);
		sb.append("\nservers:");
		sb.append(this.result);
		
		return sb.toString();
	}
}