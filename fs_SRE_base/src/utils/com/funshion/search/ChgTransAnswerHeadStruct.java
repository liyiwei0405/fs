package com.funshion.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.funshion.search.utils.IOUtils;

public class ChgTransAnswerHeadStruct{
	public final String dirName;
	public final int  index;
	public final String dfileName;
	public final byte[] md5;
	public final long fileLen;

	public ChgTransAnswerHeadStruct(final DatumDir dir, int index) throws IOException{
		DatumFile dfile = dir.get(index);
		this.dirName = dir.dirName();
		this.dfileName = dfile.getName();
		this.index = index;
		this.fileLen = dfile.file.length();
		this.md5 = dfile.md5Bytes();
	}

	public ChgTransAnswerHeadStruct(InputStream ips) throws IOException{
		dirName = IOUtils.readString(ips);
		index = IOUtils.readInt(ips);
		dfileName = IOUtils.readString(ips);
		this.md5 = new byte[16];
		IOUtils.mustFillBuffer(ips, md5);
		this.fileLen = IOUtils.readLong(ips);
	}
	public void writeTo(OutputStream ops) throws IOException{
		IOUtils.writeString(ops, dirName);
		IOUtils.writeInt(ops, index);
		IOUtils.writeString(ops, dfileName);
		IOUtils.writeBytes(ops, md5);
		IOUtils.writeLong(ops, fileLen);
	}
}