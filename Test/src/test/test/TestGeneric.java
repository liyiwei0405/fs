package test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGeneric{
	public childStruct cs = new childStruct();
	public short i;
	public List<Map<Integer, String>> listmap = new ArrayList<Map<Integer, String>>();
	public List<List<Integer>> listlist = new ArrayList<List<Integer>>();
	public List<String> list = new ArrayList<String>();
	public Map<String, List<Integer>> tactic_area = new HashMap<String, List<Integer>>();
	/**
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Field f = TestGeneric.class.getField("tactic_area");
		Type genericType = f.getGenericType();

		ParameterizedType pt = (ParameterizedType)genericType;
		System.out.println(pt.getActualTypeArguments().length);
		Type keyType = pt.getActualTypeArguments()[0];
		Type valType = pt.getActualTypeArguments()[1];

	}

}
