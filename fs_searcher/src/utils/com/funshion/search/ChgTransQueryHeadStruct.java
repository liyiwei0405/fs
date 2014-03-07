package com.funshion.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.funshion.search.utils.IOUtils;
import com.funshion.search.utils.systemWatcher.SSDaemonService;

public class ChgTransQueryHeadStruct{
	public final String envName;
	public final String dirName;
	public final int index;
	public final int ver;
	public ChgTransQueryHeadStruct(InputStream ips) throws IOException{
		ver =  IOUtils.readInt(ips);
		envName = IOUtils.readString(ips);
		dirName = IOUtils.readString(ips);
		index = IOUtils.readInt(ips);
	}
	public ChgTransQueryHeadStruct(final String dirName, final int index){
		ver = 1;
		this.envName = System.getProperty(SSDaemonService.envPropName);
		this.dirName = dirName;
		this.index = index;
	}
	public void writeTo(OutputStream ops) throws IOException{
		IOUtils.writeInt(ops, ver);
		IOUtils.writeString(ops, envName == null? "null" : envName);
		IOUtils.writeString(ops, dirName);
		IOUtils.writeInt(ops, index);
	}

	public String toString(){
		return index + "@" + dirName + ", by " + envName + " under ver " + ver;
	}
}