package com.funshion.luc.defines;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Sort;

import com.funshion.retrieve.media.thrift.SortRetrieve;
import com.funshion.search.utils.ConfigReader;

public class MediaDefaultSortClass implements DefaultSortorDefine{
	ConfigReader cr;
	Sort sort;
	@Override
	public Sort getDefaultSort() throws Exception {
		if(sort != null){
			return sort;
		}
		List<SortRetrieve>list = new ArrayList<SortRetrieve>();
		String sorts = cr.getValue("dftSort");
		String []sDef = sorts.split(";");
		for(String def : sDef){
			String idef[]  = def.split(":");
			String name = idef[0].trim();
			boolean asc;
			if(idef[1].trim().equalsIgnoreCase("des")){
				asc = false;
			}else if(idef[1].trim().equalsIgnoreCase("aes")){
				asc = true;
			}else{
				throw new Exception("undefined asc depict:" + idef[1].trim());
			}
			final SortRetrieve sr;
			if(ITableDefine.RELEVENCE.equalsIgnoreCase(name)){
				sr = new SortRetrieve(ITableDefine.RELEVENCE, asc);
			}else{
				sr = new SortRetrieve(ITableDefine.instance.fieldAlias.get(name.toUpperCase()).toString(), asc);
			}

			list.add(sr);
		}
		sort = SSearcher.getSort(list);
		return sort;
	}
	
	@Override
	public void setConfig(ConfigReader cr) {
		this.cr = cr;
		
	}

}
