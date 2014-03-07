package com.funshion.gamma.atdd.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.funshion.gamma.atdd.QueryParas;
import com.funshion.search.utils.Misc;

@SuppressWarnings("rawtypes")
public class ThriftSerializeTool {
	public static final String IgnoreCompare = "@Igr";
	public static final String IgnoreListOrder = "@IgrListOrder";
	public static final String IgnoreStrCase = "@IgrStrCase";
	public static final String NullDescriptor = "@Null";
	public static final String MethodDefineEnd= "@MethodDefineEnd";
	public static final String MethodDefine = "@Method";
	public static final String FieldDefine = "@Field";
	public static final String ParaDefine = "@Para";
	public static final String NoNameDefine = "@NoName";
	public static final String ParaTotal = "@ParaTotal";
	public static final String ReturnDefine = "@Return";
	public static final String POINTTO = "-->";
	private boolean withData = false;

	private ThriftSerializeTool(boolean withData){
		this.withData = withData;
	}

	public static String objectClassTemplateGen(Class<?>cls) throws Exception{
		StringBuilder sb = new StringBuilder();
		boolean withData = false;
		ThriftSerializeTool st = new ThriftSerializeTool(withData);

		st.serializeObject("", cls, NoNameDefine, sb, null);

		return sb.toString();
	}

	public static String objectGen(Object obj) throws Exception{
		return serializeObjectInner(obj, true);
	}
	private static String serializeObjectInner(Object obj, boolean withData) throws Exception{
		StringBuilder sb = new StringBuilder();

		ThriftSerializeTool st = new ThriftSerializeTool(withData);
		if(java.util.Map.class.isAssignableFrom(withData ? obj.getClass() : (Class<?>)obj)){
			st.serializeMap("", null, NoNameDefine, sb, obj);
		}else if(java.util.List.class.isAssignableFrom(withData ? obj.getClass() : (Class<?>)obj)){
			st.serializeList("", null, NoNameDefine, sb, obj);
		}else{
			st.serializeObject("", obj.getClass(), NoNameDefine, sb, obj);
		}
		return sb.toString();
	}
	/**
	 * 
	 * @param m
	 * @return
	 */
	public static String methodStringName(Method m){
		String ret = m.getName();
		Class<?>[] clses = m.getParameterTypes();
		if(clses == null || clses.length == 0){
			return ret;
		}
		String splt = "-";
		for(Class <?> c : clses){
			ret += splt;
			SerialType ct = SerialType.getType(c);
			if(ct == SerialType.CLASS){
				ret += "class@" + c.getName();
			}else{
				ret += ct;
			}
		}
		return ret;
	}
	public static String methodReturnValueGen(Method m, Object obj) throws Exception{
		StringBuilder sb = new StringBuilder();
		serializeMethodReturn(m, sb, true, obj);
		return sb.toString();
	}
	public static String methodReturnTemplateGen(Method m) throws Exception{
		StringBuilder sb = new StringBuilder();
		serializeMethodReturn(m, sb, false, null);
		return sb.toString();
	}
	public static String methodParameterValueGen(Method method, int paraIndex, Object obj) throws Exception{
		StringBuilder sb = new StringBuilder();
		serializeMethodParameter(method, paraIndex, sb, true, obj);
		return sb.toString();
	}
	public static String methodParameterValueGen(Method method, QueryParas paras) throws Exception{
		Object[] os = paras.getParas();
		Class<?>[] types = method.getParameterTypes();
		if(os.length != types.length){
			throw new Exception("can not serialize methodParameterValueGen for method " + method + ", expect " + types.length +" but real " + os.length);
		}
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < os.length; x ++){
			serializeMethodParameter(method, x, sb, true, os[x]);
		}
		return sb.toString();
	}
	public static String methodParameterTemplateGen(Method method, int paraIndex) throws Exception{
		StringBuilder sb = new StringBuilder();
		serializeMethodParameter(method, paraIndex, sb, false, null);
		return sb.toString();
	}
	public static void fieldValueGen(Field field, StringBuilder sb, Object obj) throws Exception{
		serializeField(field, sb, true, obj);
	}
	public static void fieldTemplateGen(Field field, StringBuilder sb) throws Exception{
		serializeField(field, sb, false, null);
	}
	/**序列化方法返回值
	 * @param method
	 * @param sb
	 * @param withData
	 * @param obj
	 * @throws Exception
	 */
	private  static void serializeMethodReturn(Method method, StringBuilder sb, boolean withData, Object obj) throws Exception{
		if(! withData){
			obj = null;
		}
		Class fType = method.getReturnType();
		//		if(Void.TYPE == method.getReturnType()){
		//			sb.append(ReturnDefine + " Void\n");
		//			return;
		//		}else{
		//			sb.append(ReturnDefine + " " + fType.getName() + "\n");
		//		}
		ThriftSerializeTool st = new ThriftSerializeTool(withData);
		if(fType == java.util.Map.class){
			Type genericType = method.getGenericReturnType();
			st.serializeMap("", genericType, ReturnDefine, sb, obj);
		}else if(fType == java.util.List.class){
			Type genericType = method.getGenericReturnType();
			st.serializeList("", genericType, ReturnDefine, sb, obj);
		}else{
			st.serializeObject("", fType, ReturnDefine, sb, obj);
		}

		sb.append(Misc.lineEnd());
	}

	/**序列化方法参数
	 * @param method
	 * @param paraIndex
	 * @param sb
	 * @param withData
	 * @param obj
	 * @throws Exception
	 */
	private static void serializeMethodParameter(Method method, int paraIndex, StringBuilder sb, boolean withData, Object obj) throws Exception{
		if(!withData){
			obj = null;
		}
		final String paraName = ParaDefine + "" + (1 + paraIndex);
		//		sb.append(paraName + "\n");
		Class fType = method.getParameterTypes()[paraIndex];
		ThriftSerializeTool st = new ThriftSerializeTool(withData);
		if(fType == java.util.Map.class){
			Type genericType = method.getGenericParameterTypes()[paraIndex];
			st.serializeMap("", genericType, paraName, sb, obj);
		}else if(fType == java.util.List.class){
			Type genericType = method.getGenericParameterTypes()[paraIndex];
			st.serializeList("", genericType, paraName, sb, obj);
		}else{
			st.serializeObject("", fType, paraName, sb, obj);
		}
		sb.append(Misc.lineEnd());
	}


	/**序列化field
	 * @param field
	 * @param sb
	 * @param withData
	 * @param obj
	 * @throws Exception
	 */
	private static void serializeField(Field field, StringBuilder sb, boolean withData, Object obj) throws Exception{
		if(! withData){
			obj = null;
		}
		sb.append(Misc.lineEnd());
		sb.append(FieldDefine);
		sb.append(field);
		sb.append(Misc.lineEnd());

		Class fType = field.getType();
		ThriftSerializeTool st = new ThriftSerializeTool(withData);
		if(fType == java.util.Map.class){
			Type genericType = field.getGenericType();
			st.serializeMap("", genericType, field.getName(), sb, obj);
		}else if(fType == java.util.List.class){
			Type genericType = field.getGenericType();
			st.serializeList("", genericType, field.getName(), sb, obj);
		}else{
			st.serializeObject("", fType, field.getName(), sb, obj);
		}
		sb.append(Misc.lineEnd());
	}

	private void serializeObject(final String prefix, Class cls, String name, StringBuilder sb, Object obj) throws Exception{
		SerialType serialType = SerialType.getType(cls);

		String myPrifix = prefix;

		if(serialType.isRawType){
			sb.append(myPrifix +  serialType.toString(cls)  + " " + name + ": ");
			if(this.withData){
				String synData;
				if(obj == null){
					synData = NullDescriptor;
				}else{
					if(obj instanceof String){
						obj = obj.toString().replace("\r", "").replace("\n", "");
					}
					synData = String.valueOf(obj);
				}
				sb.append(synData);
			}
			sb.append(Misc.lineEnd());
		}else if(cls.isEnum()){
			sb.append(myPrifix +  serialType.toString(cls)  + " " + name + ": ");
			if(this.withData){
				sb.append(obj.toString());
			}
			sb.append(Misc.lineEnd());
		}else{//其他类
			if(obj == null && this.withData == true){
				sb.append(myPrifix + serialType.toString(cls)  + " " + name  + ": " + NullDescriptor + Misc.lineEnd());
				return;
			}
			sb.append(myPrifix + serialType.toString(cls) + " " + name  + "{");
			sb.append(Misc.lineEnd());
			Field[] fields = cls.getFields();
			for(Field field : fields){
				if( Modifier.isStatic(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())){
					continue;
				}
				Class<?> fieldClass = field.getType();
				Object fieldValue = null;
				if(obj != null){
					fieldValue = field.get(obj);
				}
				if(fieldClass == Map.class){//是List
					Type genericType = field.getGenericType();
					serializeMap(myPrifix + "\t", genericType, field.getName(), sb, fieldValue);
				}else if(fieldClass == List.class){//是List
					Type genericType = field.getGenericType();
					serializeList(myPrifix + "\t", genericType, field.getName(), sb, fieldValue);
				}else{//如果是其他类
					serializeObject(myPrifix + "\t", fieldClass, field.getName(), sb, fieldValue);
				}
			}
			sb.append(prefix +"}");
			sb.append(Misc.lineEnd());
		}
	}
	private void serializeMap(final String prefix, Type genericType, String fieldName, StringBuilder sb, Object oMap) throws Exception{
		System.out.println("----------0" );
		if(oMap == null && this.withData == true){
			sb.append(prefix + SerialType.LIST + " " +  fieldName + ": " + NullDescriptor + Misc.lineEnd());
			return;
		}
		Map map = (Map)oMap;
		sb.append(prefix + SerialType.MAP + " " +  fieldName + Misc.lineEnd());
		sb.append(prefix + "[" + Misc.lineEnd());
		Class argumentClass[] = null;
		if(genericType != null && (genericType instanceof ParameterizedType)){
			ParameterizedType pt = (ParameterizedType) genericType;
			Type[] t = pt.getActualTypeArguments();
			
			if(t[0] instanceof Class && t[1] instanceof Class){
				argumentClass = new Class[2];
				argumentClass[0] = (Class)( t[0]);
				argumentClass[1] = (Class)( t[1]);
			}
		}

		if(this.withData){
			@SuppressWarnings("unchecked")
			Iterator<Map.Entry<Object, Object>> itr = map.entrySet().iterator();
			while(itr.hasNext()){
				Entry<Object, Object> e = itr.next();
				serializeObject(prefix + "\t", e.getKey() ==  null? (argumentClass == null? Object.class : argumentClass[0]): e.getKey().getClass(), "", sb, e.getKey());
				sb.append(prefix + "\t" + POINTTO );
				sb.append(Misc.lineEnd());
				serializeObject(prefix + "\t", e.getValue() ==  null? (argumentClass == null? Object.class : argumentClass[1]): e.getValue().getClass(), "", sb, e.getValue());
				sb.append(Misc.lineEnd());
			}
		}else{
			if(argumentClass != null){
				serializeObject(prefix + "\t", argumentClass[0], "", sb, null);
				sb.append(prefix + "\t" + POINTTO);
				sb.append(Misc.lineEnd());
				serializeObject(prefix + "\t", argumentClass[1], "", sb, null);
			}else{
				//nothing to do
			}
		}
		System.out.println("----------2" );
		sb.append(prefix + "]");
		sb.append(Misc.lineEnd());
	}
	/**
	 * 序列化一个对象
	 * @param prefix
	 * @param cls
	 * @param name
	 * @param sb
	 * @throws Exception
	 */
	//序列化一个List
	private void serializeList(final String prefix, Type genericType, String fieldName, StringBuilder sb, Object oList) throws Exception{
		if(oList == null && this.withData == true){
			sb.append(prefix + SerialType.LIST + " " +  fieldName + ": " + NullDescriptor + Misc.lineEnd());
			return;
		}
		List list = (List)oList;
		sb.append(prefix + SerialType.LIST + " " +  fieldName + Misc.lineEnd());
		sb.append(prefix + "[" + Misc.lineEnd());
		Class argumentClass = null;
		if(genericType != null && (genericType instanceof ParameterizedType)){
			ParameterizedType pt = (ParameterizedType) genericType;   
			Type argumentType = pt.getActualTypeArguments()[0];  	//得到泛型里的参数类型
			argumentClass = (Class)argumentType; 
		}

		if(this.withData){//循环打印
			for(Object o : list){
				serializeObject(prefix + "\t", o ==  null? (argumentClass == null? Object.class : argumentClass): o.getClass(), "", sb, o);
			}
		}else{
			if(argumentClass != null){
				serializeObject(prefix + "\t", argumentClass, "", sb, null);
			}else{
				//nothing to do
			}
		}

		sb.append(prefix + "]");
		sb.append(Misc.lineEnd());
	}

	public static String methodParameterTemplateGen(Method m) throws Exception {
		int len = m.getParameterTypes().length;
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < len;  x ++){
			sb.append(methodParameterTemplateGen(m, x));
		}
		return sb.toString();
	}
}
