package com.funshion.search;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import com.funshion.search.utils.LineReader;

public class FSIndexableRecordIterator implements Iterator<IndexableRecord>{
	IndexableRecord crt;
	final LineReader lr;
	boolean close = false;
	FSIndexableRecordFactory factory;
	
	public FSIndexableRecordIterator(File file, FSIndexableRecordFactory factory) throws IOException{
		this(new LineReader(file, Charset.forName("utf-8")));
		this.factory = factory;
	}
	public FSIndexableRecordIterator(LineReader lr){
		this.lr = lr;
	}
	@Override
	public boolean hasNext() {
		if(close){
			return false;
		}else{
			try {
				crt = readIndexableFRecord(lr, factory);
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
	
	public static IndexableRecord readIndexableFRecord(LineReader lr, FSIndexableRecordFactory factory) throws IOException{
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
		IndexableRecord ir = factory.newFSIndexableRecord();
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
//			ir.putItem(line.substring(0, pos), line.substring(pos + 1));
		}
		if(!foundEnd){
			throw new IOException("bad record! find no end");
		}
		return ir;
	}
}