package com.funshion.retrieve.media;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.junit.Test;

import com.funshion.retrieve.media.portableCondition.ConjunctType;
import com.funshion.retrieve.media.portableCondition.OperType;
import com.funshion.retrieve.media.portableCondition.PortalCompoundCondition;
import com.funshion.retrieve.media.portableCondition.PortalItemCondition;
import com.funshion.retrieve.media.portableCondition.PortalRangeCondition;
import com.funshion.retrieve.media.thrift.Token;
import com.funshion.search.media.search.FieldDefine;

public class TestCompoundConditionSerial {
	
	public String list2String(List<Token>list){
		StringBuilder sb = new StringBuilder();
		for(Token t : list){
			sb.append(t);
			sb.append('\n');
		}
		return sb.toString();
	}
	public PortalCompoundCondition List2String() throws Exception{
		PortalCompoundCondition conds = new PortalCompoundCondition();
		conds.addCondition(ConjunctType.AND, new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES,
						OperType.SEARCH_TITLE_FULL,
						"x"));
		conds.addCondition(ConjunctType.NOT, new PortalItemCondition(FieldDefine.FIELD_NAME_COVER_PIC_ID, OperType.EQUAL,  "145"));

		PortalCompoundCondition subList = new PortalCompoundCondition();
		subList.addCondition(ConjunctType.AND, new PortalItemCondition(FieldDefine.FIELD_NAME_NAME_EN, OperType.LIKE, "1"));
		subList.addCondition(ConjunctType.NOT, new PortalRangeCondition(FieldDefine.FIELD_NAME_RELEASE_DATE, 0, 958, true, true));

		conds.addCondition(ConjunctType.AND, subList);
		conds.addCondition(ConjunctType.OR, new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES, OperType.SEARCH_TITLE, "取胜第二一季未删节版"));
		conds.addCondition(ConjunctType.AND, new PortalItemCondition(FieldDefine.FIELD_NAME_NAMES, OperType.SEARCH_TITLE_FULL, "取胜第二一季未删节版"));
		ArrayList<Token>list = new ArrayList<Token>();
		conds.appendToList(list, ConjunctType.NOT);
		
		return conds;
	}
	
	@Test
	public void test() throws Exception {
		PortalCompoundCondition cmp1 = List2String();
		List<Token>l1 = QueryParseTool.toList(cmp1);
//		cmp.appendToList(l1);
		PortalCompoundCondition cmp2 = QueryParseTool.parseList(l1);
		List<Token>l2 = QueryParseTool.toList(cmp2);
		
		String s1 = list2String(l1);
		String s2 = list2String(l2);
		System.out.println(s1);
		System.out.println("--------------------------------------------");
		System.out.println(s2);
		System.out.println("--------------------------------------------");
		assertEquals("CompoundCondition test fail!", s1, s2);
		
		
		Query build1 = QueryParseTool.buildQuery(cmp1);
		System.out.println("query:" + build1);
		
		Query build2 = QueryParseTool.buildQuery(cmp2);
		System.out.println("query:" + build2);
	}

}
