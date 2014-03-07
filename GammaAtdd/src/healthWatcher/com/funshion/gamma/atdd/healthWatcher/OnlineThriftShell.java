package com.funshion.gamma.atdd.healthWatcher;

import java.lang.reflect.Method;
import java.util.List;

import com.funshion.gamma.atdd.AbstractThriftService;
import com.funshion.gamma.atdd.MethodAtddCommonThriftClient;
import com.funshion.gamma.atdd.serialize.shell.ThriftClientShell;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;

/*
 * 交互式SHELL，从配置文件中选择一个service及method，与ThriftClientShell区别在于获取当前线上某个实例的地址并生成client，而不是从配置文件读取
 */
public class OnlineThriftShell {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		List<String>list = ConfigReader.listSectionsInConfigFile(MailConfig.cfgFile);
		list.remove("admin");
		list.remove("timingMail");
		System.out.print("available services: ");
		for(String section : list){
			System.out.print("[" + section + "] ");
		}
		System.out.println();
		String cfgSection = Consoler.readString("select service name from above: ");
		//读取选择的section(service)配置信息
		ConfigReader cr = new ConfigReader(MailConfig.cfgFile, cfgSection);
		
		RpcServiceInfo rpcServiceInfo = new RpcServiceInfo(cr);
		//获取该服务其中一个实例的地址
		ServiceInstanceLocationInfo locationInfo = rpcServiceInfo.getServerInstanceLocations().get(0);
		System.out.println("location: " + locationInfo);
		String serviceName = cr.getValue("service-name");
		
		Class<?> clsIface = null;
		String ifaceName = serviceName + AbstractThriftService.IfaceDepector;
		try{
			clsIface = Class.forName(ifaceName);
		}catch(Exception e){
			System.err.println("Fail! Iface not found for svcName:'" + serviceName + "'");
			e.printStackTrace();
		}
		//获取该接口的methods
		Method [] ms = clsIface.getMethods();
		System.out.print("available methods: ");
		for(Method method : ms){
			System.out.println("[" + method.getName() + "]");
		}
		System.out.println();
		String sMethod = Consoler.readString("select method from above: ");
		
		Method method = null;
		for(Method m : ms){
			if(m.getName().equals(sMethod)){
				method = m;
			}
		}
		
		MethodAtddCommonThriftClient client = new MethodAtddCommonThriftClient(locationInfo.getIp(),
				locationInfo.getPort(),
				method,
				serviceName);

		ThriftClientShell shell = new ThriftClientShell(client);
		shell.exe();

	}

}
