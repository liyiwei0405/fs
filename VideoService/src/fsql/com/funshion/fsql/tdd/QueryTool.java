package com.funshion.fsql.tdd;

import org.apache.lucene.search.Query;

import com.funshion.fsql.expression.FsqlInterpreter;

public class QueryTool {
	static class QueryEntry{
		String sql;
		String[] paras;
		QueryEntry(String sql){
			this(sql, new String[]{});
		}
		QueryEntry(String sql, String[] paras){
			this.sql = sql;
			this.paras = paras;
		}
	}
	public static void main(String[] args) throws Exception {
		QueryEntry[]querys = new QueryEntry[]{
				new QueryEntry(
						"title SEARCH '武汉33'"
						),
						new QueryEntry(
								"title SEARCH '武汉族33'"
								),
						new QueryEntry("(title SEARCH '武汉' && TIMELEN == ?) &&(SCORE == '88' || PLAY_NUM== ?) not (TAG_IDS==? || SOURCE == ?)",
								new String[]{"99", "37", "偶像剧", "tudou"}),

								new QueryEntry(
										"(title SEARCH '武汉')"
										),	
										new QueryEntry(
												"title SEARCH '武汉'"
												),	

												new QueryEntry(
														"title SEARCH '武汉' not title Search 33"
														),	
														new QueryEntry(
																"title SEARCH '武汉' not title Search 33"
																),	
																new QueryEntry(
																		"title SEARCH '武汉' not( title Search ?)",
																		new String[]{"上帝95p"}
																		),	
		};




		for(QueryEntry e : querys){
			long st = System.nanoTime();
			System.out.println("------------->SQL: " + e.sql + "<-----------------------");
			FsqlInterpreter pre = new FsqlInterpreter(e.sql, e.paras);

			//			pre.setValue(5, "d");
			Query q = pre.toQuery();
			System.out.println(q);
			long ed = System.nanoTime();
			System.out.println((ed - st) / 1000 / 1000.0 + "ms");

			System.out.println(pre.head);
			//				for(ItemConditionNode i : pre.var){
			//					System.out.println(idx++ + ":" +i);
			//				}

		}

	}
}

