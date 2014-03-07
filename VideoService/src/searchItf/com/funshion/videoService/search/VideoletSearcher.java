package com.funshion.videoService.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;

import com.funshion.fsql.expression.FsqlInterpreter;
import com.funshion.fsql.expression.OrderInfo;
import com.funshion.luc.defines.AbstractSearcher;
import com.funshion.luc.defines.IFieldDefine;
import com.funshion.luc.defines.ITableDefine;
import com.funshion.videoService.thrift.RetrieveStruct;

public class VideoletSearcher extends AbstractSearcher{

	public VideoletSearcher(IndexReader ir) throws IOException{
		super(ir);
	}
	public static Sort toSort(List<OrderInfo> orders) throws Exception{
		SortField[]ss = new SortField[orders.size()];
		for(int x = 0; x < ss.length; x ++){
			OrderInfo sr = orders.get(x);
			if(sr.filed.equalsIgnoreCase(ITableDefine.RELEVENCE)){
				ss[x] = SortField.FIELD_SCORE;
			}else{
				IFieldDefine fieldD = ITableDefine.instance.getFieldDefine(sr.filed);
				SortField.Type sfType = fieldD.aType.toSortFieldType();

				if(sfType == null){
					throw new Exception("can not find field's SortFiled.Type:" + sr);
				}else{
					ss[x] = new SortField(
							fieldD.fieldName,
							sfType,
							!sr.asc);
				}
			}
		}
		return new Sort(ss);
	}
	class SearchInfo{
		TopDocs docs;
		int offset;
		int limit;
		SearchInfo(TopDocs docs, int offset, int limit){
			this.docs = docs;
			this.offset = offset;
			this.limit = limit;
		}
	}
	public SearchInfo search(RetrieveStruct rs) throws Exception{
		long time0 = System.nanoTime();
		FsqlInterpreter pre = new FsqlInterpreter(rs.fsql, rs.paras);
		Query bqx = pre.toQuery();
		long time1 = System.nanoTime();
		Sort sort = toSort(pre.orders);
		int topN = pre.limit[0] + pre.limit[1];
		if(log.logger.isDebugEnabled()){
			log.debug("query %s with sort %s, topN %s", bqx, sort, topN);
		}
		if(log.logger.isDebugEnabled()){
			log.debug("search make-query use %s", (time1 - time0) / 10000/100.0);
		}
		TopDocs td = super.search(bqx, sort, topN);

		return new SearchInfo(td, pre.limit[0], pre.limit[1]);
	}

}
