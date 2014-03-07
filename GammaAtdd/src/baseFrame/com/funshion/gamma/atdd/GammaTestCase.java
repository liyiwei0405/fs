package com.funshion.gamma.atdd;

import com.funshion.gamma.atdd.serialize.DeserializedFieldInfo;
import com.funshion.gamma.atdd.serialize.ThriftSerializeTool;

public class GammaTestCase {
	final QueryParas paras;
	final Object expect;
	public final DeserializedFieldInfo cmpMask;
	public GammaTestCase(QueryParas paras, QueryReturnValue retValue){
		this(paras, retValue.expect, retValue.cmpMask);
	}
	
	public GammaTestCase(QueryParas paras,
			Object expect, DeserializedFieldInfo mask){
		this.paras = paras;
		this.expect = expect;
		cmpMask = mask;
	}
	public QueryParas queryParas() {
		return paras;
	}
	public Object expectResult() {
		return expect;
	}

	@Override
	public String toString(){
		try{
			StringBuilder sb = new StringBuilder();
			Object para[] = paras.getParas();
			sb.append("#GammaTetCase, total para : " + para.length + "\n");
			int index = 0;
			for(Object o : para){
				sb.append("#para " + ++ index +"\n");
				String ret = ThriftSerializeTool.objectGen(o);
				sb.append(ret);
				sb.append("\n");
			}

			sb.append("#return\n");
			String ret = ThriftSerializeTool.objectGen(expect);
			sb.append(ret);
			sb.append("\n");

			return sb.toString();
		}catch(Exception e){
			return "when toString for GammaTestCase:" + e.getMessage();
		}
	}
}
