package com.funshion.gamma.atdd.serialize.test;

import java.util.ArrayList;
import java.util.List;

import com.funshion.gamma.atdd.serialize.ThriftSerializedTool;
public class TestSerialObject {
	public static class Tt{
		public int i1;
		public Tt(){}
		public Tt(int va){
			i1 = va;
		}
	}
	public static class TV{
		public int value = 7;
		public Tt tt;
		public List<Tt> lst = new ArrayList<Tt>();
		Tt xx = new Tt();
		public TV(){
			lst.add(new Tt(0));
			lst.add(new Tt(10));
			lst.add(new Tt(100));
			lst.add(new Tt(10000));
			lst.add(new Tt(1000));
		}
	}

	public static void main(String[] args) throws Exception {
		List<Integer> lst = new ArrayList<Integer>();
		lst.add(1);

		String str = ThriftSerializedTool.objectClassTemplateGen(lst.getClass());
		System.out.println(str);

		str = ThriftSerializedTool.objectGen(lst);
		System.out.println(str);
		
		str = ThriftSerializedTool.objectGen((Integer)5);
		System.out.println(str);
		str = ThriftSerializedTool.objectClassTemplateGen(int.class);
		System.out.println(str);
		str = ThriftSerializedTool.objectClassTemplateGen(((Integer)5).getClass());
		System.out.println(str);
		str = ThriftSerializedTool.objectClassTemplateGen(lst.getClass());
		System.out.println(str);

		str = ThriftSerializedTool.objectClassTemplateGen(com.funshion.gamma.atdd.tacticService.thrift.MediaIdList.class);
		System.out.println(str);
		
		TV tv = null;
		str = ThriftSerializedTool.objectClassTemplateGen(TV.class);
		System.out.println(str);
		
//		str = serializeObject(tv);
//		System.out.println(str);
		
		tv = new TV();
		str = ThriftSerializedTool.objectGen(tv);
		System.out.println(str);
		
		tv.tt = new Tt();
		str = ThriftSerializedTool.objectGen(tv);
		System.out.println(str);
		
	}
}
