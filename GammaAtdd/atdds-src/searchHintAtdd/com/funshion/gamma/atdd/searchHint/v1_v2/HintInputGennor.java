package com.funshion.gamma.atdd.searchHint.v1_v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.funshion.gamma.atdd.ParableInputGennor;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.search.utils.LineReader;

public class HintInputGennor extends ParableInputGennor{

	private int crtIndex = -1;
	ArrayList<String>words = new ArrayList<String>();
	public HintInputGennor() throws IOException{
		LineReader lr = new LineReader(new File("config/searchHint.v1-v2/inputWords.txt"), "utf-8");
		while(lr.hasNext()){
			String line = lr.next();
			words.add(line);
		}
		lr.close();
	}
	@Override
	public boolean hasNext() {
		++ crtIndex;
		return crtIndex < words.size();
	}

	public String toString(){
		return "crtIndex " + crtIndex +( crtIndex > -1 ? ", query word = '" + words.get(crtIndex) + "'" :"");
	}
	@Override
	protected QueryParas genAvsQuery() {
		final String hintInput = words.get(crtIndex);
		return QueryParas.instance(hintInput);

	}

	@Override
	protected QueryParas genRcsQuery() {
		final com.funshion.searchHint.thrift.v2.requestStruct record = new com.funshion.searchHint.thrift.v2.requestStruct();
		record.word = words.get(crtIndex);
		record.offset = 0;
		record.limit = 10;
		record.includeStaff = true;
		record.filterCopyRight = false;

		return QueryParas.instance(record);
	}

}
