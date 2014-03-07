package com.funshion.search;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.funshion.search.utils.LineReader;

public class IndexableRecord {
	Map<String, String>fieldValues = new HashMap<String, String>();
	protected void putRecord(String key, String values){
		this.fieldValues.put(key, values);
	}
	public String valueOf(String key){
		String ret = fieldValues.get(key);
		return ret;
	}
//	public String valueOf(char key){
//		String ret = fieldValues.get(key);
//		return ret.substring(2, ret.length()).trim();
//	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, String>> itr = fieldValues.entrySet().iterator();
		while(itr.hasNext()){
			sb.append(itr.next());
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
			int pos = line.indexOf('\t');
			if(pos == -1){
				throw new IOException("invalid field define:'" + line + "'");
			}
			ir.putRecord(line.substring(0, pos), line.substring(pos + 1));
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
			this(new LineReader(file, Charset.forName("utf-8")));
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
