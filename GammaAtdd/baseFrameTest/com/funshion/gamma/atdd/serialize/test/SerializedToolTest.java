package com.funshion.gamma.atdd.serialize.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.serialize.DefineIterator;
import com.funshion.gamma.atdd.serialize.ThriftDeserializeTool;
import com.funshion.gamma.atdd.serialize.ThriftSerializeTool;

public class SerializedToolTest {

	public class C1{
		public String myName;
		public List<Integer>girlFriendlst;

	}
	public class BoyList{
		public List<String>toil;
		public int age;
		public boolean isMy;
		public List<C1>boys;
	}
	public class T{
		public List<BoyList>boys;

	}
	private SerializedToolTest(){}

	public static void main(String[] args) throws Exception {
		StringBuilder sb = new StringBuilder();

		//		//		gen(RequestStruct.class);
		//		//		System.out.println("-----------------");
		//		//		gen2(HintResult.class);
		//		System.out.println("-----------------");
		//		//		gen2(RequestStruct.class, "");

		//		HintResult hr = new HintResult();
		//		hr.retCode = 100;
		//		hr.retMsg = "OK";
		//		HintRecord record1 = new HintRecord();
		//		record1.hint = "张三";
		//		HintRecord record2 = new HintRecord();
		//		record2.hint = "李四";
		//		List<HintRecord> list = new LinkedList<HintRecord>();
		//		list.add(record1);
		//		list.add(record2);
		//		hr.records = list;
		//
		//		HintResult hr2 = new HintResult();
		//		SerializedTool st = new SerializedTool(hr, HintResult.class);
		//		st.serializedObject("", HintResult.class, hr);

//				V2 v2 = new V2();
//				V1 v1 = new V1();
//				v1.s = "abc";
//				List<String> sub1 = new ArrayList<String>();
//				sub1.add("lll");
//				sub1.add("bbb");
//				List<String> sub2 = new ArrayList<String>();
//				sub2.add("aff");
//				sub2.add("ggs");
//				List<List<String>> superList = new ArrayList<List<String>>();
//				superList.add(sub2);
//				superList.add(sub1);
//				v1.col = superList;
//				v2.a = true;
//				v2.v1 = v1;
		
				V v = new V();
				v.a = null;
				List<String> l = new ArrayList<String>();
				l.add("lll");
				l.add("bbb");
				v.l = l;
				v.maper.put("keyKey", 55);
				v.maper.put("VV", 45454545);
				//ThriftSerializedTool.serializeFieldTemplate(V2.class.getField("v"), sb, v2.v);
				Field fss[] = V.class.getDeclaredFields();
				
				String vcls = ThriftSerializeTool.objectClassTemplateGen(V.class);
				System.out.println(vcls);
				String vObject = ThriftSerializeTool.objectGen(v);
				
				System.out.println(vObject);
				
				DefineIterator itr = new DefineIterator(vObject.split("\n"));
				
				Object o = ThriftDeserializeTool.deserializeObjects(itr).get(0).value;
				vObject = ThriftSerializeTool.objectGen(o);
				System.out.println(vObject);
	}

}
