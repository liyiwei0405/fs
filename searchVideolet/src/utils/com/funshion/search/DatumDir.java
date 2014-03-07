package com.funshion.search;

import java.io.File;
import java.util.Vector;

public class DatumDir {
	private final Vector<DatumFile>updates = new Vector<DatumFile>();
	private DatumDir(Vector<DatumFile>updates){
		this.updates.addAll(updates);
	}
	public DatumDir(DatumFile main){
		this.updates.add(main);
	}
	public DatumDir(File inf){
		this(new DatumFile(inf));
	}
	public String dirName(){
		return this.updates.get(0).file.getParentFile().getName();
	}
	public final int size(){
		return updates.size();
	}
	public final DatumFile get(int index){
		return this.updates.get(index);
	}
	
	public void putDatumFile(DatumFile file){
		this.updates.add(file);
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(DatumFile dfile : updates){
			sb.append(dfile);
		}
		return sb.toString();
	}
	public boolean equalName(String name){
		return name.equals(this.dirName());
	}
	public DatumDir copyOf() {
		return new DatumDir(this.updates);
	}
}
