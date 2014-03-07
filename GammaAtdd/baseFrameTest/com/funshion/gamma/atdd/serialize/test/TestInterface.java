package com.funshion.gamma.atdd.serialize.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TestInterface {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[]args)throws Exception{
		String svcName = "com.funshion.gamma.atdd.tacticService.thrift.TacticService";
		String cltName = svcName + "$Client";
		Class c = Class.forName(cltName);
		Constructor cst = c.getConstructor(org.apache.thrift.protocol.TProtocol.class);
		Object clientInstance = cst.newInstance((org.apache.thrift.protocol.TProtocol)null);
		System.out.println("instance :" + clientInstance);

		String ifaceName = svcName + "$Iface";
		Class clsIface = Class.forName(ifaceName);

		System.out.println("Iface class" + clsIface);

		Method [] ms = clsIface.getMethods();
		for(Method mtd : ms){
			System.out.println(mtd);
		}
		for(int idx = 0; idx < ms.length; idx ++){
			Method m = ms[idx];
			System.out.println();
			System.out.println("index " + idx + "'s method:" + m);
			Type t = m.getGenericReturnType();
			if(t instanceof ParameterizedType){
				ParameterizedType pt = (ParameterizedType) t;
				System.out.println("generic:" + pt.getActualTypeArguments());
			}else{
				System.out.println("NOT generic:" );
			}
			
			System.out.println("parameters:");
			
			Type[] paras = m.getGenericParameterTypes();
			for(int y = 0; y < paras.length; y ++){
				Type para = paras[y];
				
				System.out.println();
				System.out.println("index " + y + "'s para:" + para);
				if(para instanceof ParameterizedType){
					ParameterizedType pt = (ParameterizedType) para;
					System.out.println("generic:" + pt.getActualTypeArguments()[0]);
				}else{
					System.out.println("NOT generic:" + para);
				}
				
			}
			System.out.println("----------------------------------------------------" );
		}
	}

}
