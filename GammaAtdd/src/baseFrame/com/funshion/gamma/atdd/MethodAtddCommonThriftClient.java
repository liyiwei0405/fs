package com.funshion.gamma.atdd;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.serialize.SerialType;
import com.funshion.search.utils.ConfigReader;

/*
 * 具有一个method的CommonThriftClientInfo
 */
@SuppressWarnings("rawtypes")
public class MethodAtddCommonThriftClient extends CommonThriftClientInfo{
	public final Method method;
	final Class[] parameterTypes ;

	public MethodAtddCommonThriftClient(
			String host, int port, Method m, String svcName) throws Exception{
		super(host, port, svcName);
		this.parameterTypes = m.getParameterTypes();
		method = m;
	}
	public MethodAtddCommonThriftClient(ConfigReader cr, String avs_rcs) throws Exception{
		super(
				cr.getValue(avs_rcs + "-host"),  
				cr.getInt(avs_rcs + "-port"), 
				cr.getValue(avs_rcs + "-service-name"));

		String methodParas = cr.getValue(avs_rcs + "-method-paras");
		String methodName = cr.getValue(avs_rcs + "-method-name");
		String tokens [] = methodParas.split(",");
		List<Class>clss = new ArrayList<Class>();
		for(String x : tokens){
			x = x.trim();
			if(x.length() == 0){
				continue;
			}
			SerialType t = SerialType.valueOfString(x);
			Class c;
			if(t.isRawType){
				c = t.getRealClass();
			}else if(t == SerialType.LIST){
				c = java.util.List.class;
			}else{
				c = SerialType.className(t, x);
			}
			clss.add(c);
		}
		this.parameterTypes = new Class[clss.size()];
		for(int x = 0; x < this.parameterTypes.length; x ++){
			Class c = clss.get(x);
			this.parameterTypes[x] = c;
		}
		method = this.serviceClientClass.getMethod(methodName, this.parameterTypes);

	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", for Method ");
		sb.append(method);
		sb.append(" with types as:");
		for(Class c: this.parameterTypes){
			sb.append("\n\t");
			sb.append(c);
		}
		return sb.toString();
	}
	/**
	 * besuer cInfo is not null
	 * @param paras
	 * @param cInfo
	 * @return
	 * @throws Exception
	 */
	private Object queryInner(QueryParas paras, ClientInfo cInfo) throws Exception {
		Object paras2[] = paras.getParas();
//		for(Object o : paras2){
//			System.out.println(o.getClass() + " -- > " + o);
//		}
		Object o = method.invoke(cInfo.client, paras2);
		return o;
	}
	public Object query(QueryParas paras) throws Exception {
		return query(paras, null);
	}
	public Object query(QueryParas paras, ClientInfo cInfo) throws Exception {
		boolean toClose = false;
		if(cInfo == null){
			toClose = true;
			cInfo = super.getNewClient();
		}
		try{
			return queryInner(paras, cInfo);
		}finally{
			if(toClose){
				cInfo.close();
			}
		}
	}
}
