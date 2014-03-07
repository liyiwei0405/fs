package com.funshion.gamma.atdd;

import java.util.List;

import com.funshion.gamma.atdd.serialize.DefineIterator;
import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;
import com.funshion.gamma.atdd.serialize.ThriftDeserializeTool;

public abstract class QueryParas {

	public QueryParas(){};
	/**
	 * get paras as "method-paras" configuration
	 * @return
	 */
	public abstract Object[] getParas();

	@Override
	public String toString(){
		Object[] os = getParas();
		StringBuilder sb = new StringBuilder();
		for(Object o : os){
			if(o == null){
				sb.append("NULL, ");
			}else{
				if(o instanceof List){
					sb.append("list size " + ((List<?>)o).size());
					sb.append(":" + o);
					sb.append(", ");
				}else{
					sb.append(o);
				}
			}
		}
		return sb.toString();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static QueryParas instance(List args){
		Object []os = new Object[args.size()];
		args.toArray(os);
		return new IQueryParas(args);
	};
	public static QueryParas instance(final Object ...args){
		return new IQueryParas(args);
	};

	public static class IQueryParas extends QueryParas{
		final Object[]args;
		public IQueryParas(final Object ...args){
			this.args = args;
		}
		@Override
		public Object[] getParas() {
			return args;
		}
	}
	public static QueryParas fromStringList(List<String> lst) throws Exception{
		List<DeserializedFieldInfo> fis = ThriftDeserializeTool.deserializeObjects(new DefineIterator(lst));
		return fromDeserializedFieldInfoList(fis);
	}
	public static QueryParas fromDeserializedFieldInfoList(
			List<DeserializedFieldInfo> lst){
		final Object[]arr =
				new Object[lst.size()];
		for(int x = 0; x < lst.size(); x ++){
			arr[x] = lst.get(x).value;
		}
		QueryParas ret = new QueryParas(){

			@Override
			public Object[] getParas() {
				return arr;
			}

		};

		return ret;
	}
}
