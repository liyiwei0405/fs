package com.funshion.search;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.funshion.search.media.search.FieldDefine;
import com.funshion.search.utils.LineReader;

public class IndexableRecord {
	Map<Character, String>fieldValues = new HashMap<Character, String>();
	protected void putRecord(char key, String values){
		this.fieldValues.put(key, values);
	}
	public String valueOf(String key){
		String ret = fieldValues.get(key.charAt(0));
		return ret.substring(FieldDefine.NameLen + 1, ret.length());
	}
	public String valueOf(char key){
		String ret = fieldValues.get(key);
		return ret.substring(2, ret.length());
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(String x : fieldValues.values()){
			sb.append(x);
			sb.append('\n');
		}
		return sb.toString();
	}
	public static IndexableRecord readIndexableFRecord(LineReader lr) throws IOException{
		//find record start position
		boolean foundStart = false;
		while(lr.hasNext()){
			String line = lr.next();
			if(FlushableRecord.isRecordStart(line)){
				foundStart = true;
				break;
			}
		}
		if(!foundStart){
			return null;
		}
		IndexableRecord ir = new IndexableRecord();
		//we has found the record start, now we need read the record 
		boolean foundEnd = false;
		while(lr.hasNext()){
			String line = lr.next();
			if(FlushableRecord.isRecordEnd(line)){
				foundEnd = true;
				break;
			}

			//parse item
			if(line.length() < 2){
				throw new IOException("invalid field define:'" + line + "'");
			}
			ir.putRecord(line.charAt(0), line);
		}
		if(!foundEnd){
			throw new IOException("bad record! find no end");
		}
		return ir;
	}

	public static class IRIterator implements Iterator<IndexableRecord>{
		IndexableRecord crt;
		final LineReader lr;
		boolean close = false;
		public IRIterator(File file) throws IOException{
			this(new LineReader(file));
		}
		public IRIterator(LineReader lr){
			this.lr = lr;
		}
		@Override
		public boolean hasNext() {
			if(close){
				return false;
			}else{
				try {

					crt = readIndexableFRecord(lr);
					if(crt == null){
						lr.close();
						close = true;
						return false;
					}else{
						return true;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public IndexableRecord next() {
			return crt;
		}

		@Override
		public void remove() {
			throw new RuntimeException("method #remove not supported!");
		}

		public void close(){
			lr.close();
		}
		public void finalize(){
			close();
		}
	}
}
