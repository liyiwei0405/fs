package com.funshion.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.funshion.search.utils.Md5;

public class DatumFile {
	public final boolean isMain;
	public final File file;
	private byte[]md5Bytes;
	public DatumFile(File mainFile, boolean isMain){
		this(mainFile, null, isMain);
	}
	public DatumFile(File mainFile){
		this(mainFile, null, false);
	}
	public DatumFile(File mainFile, byte[]md5Bytes, boolean isMain){
		this.file = mainFile;
		this.md5Bytes = md5Bytes;
		this.isMain = isMain;
	}
	public synchronized byte[]md5Bytes() throws IOException{
		if(this.md5Bytes == null){
			this.checkMd5();
		}
		return this.md5Bytes;
	}
	public String fileMd5String() throws IOException{
		this.checkMd5();
		return Md5.byte2Hex(this.md5Bytes);
	}
	private synchronized void checkMd5() throws IOException{
		InputStream fis = new FileInputStream(file);
		byte[]buf = new byte[4096];
		Md5 md5 = new Md5();
		while(true){
			int cnt = fis.read(buf);
			if(cnt == -1){
				break;
			}
			md5.append(buf, 0, cnt);
		}
		fis.close();
		this.md5Bytes = md5.finish();
	}
	public String toString(){
		String md5;
		try {
			md5 = this.fileMd5String();
		} catch (IOException e) {
			md5 = e.getMessage();
		}
		
		return isMain + ", " + file.getName() + "@" + file.getParentFile().getName() + ", md5:" + md5;
	}
	public String getName() {
		return file.getName();
	}
}
