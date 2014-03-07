package com.funshion.search.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


/**
 * file lock to prevent reentrant
 * @author Fisher@Beiming
 *
 */
public class FileBasedLock{
	public final File LOCK_PATH = new File("./_f_locks_/");
	public final File file;
	FileLock fLock;
	FileChannel fileChannel;
	final String id;
	RandomAccessFile raFile;
	public FileBasedLock(String id) throws IOException{
		if(!LOCK_PATH.exists()) {
			LOCK_PATH.mkdirs();
		}
		this.file = new File(LOCK_PATH, id + ".lock_");
		if(!file.exists()){
			file.createNewFile();
		}
		this.id = id;
	}
	/**
	 * try to lock on this 
	 * @return
	 * @throws IOException 
	 */
	public synchronized void tryLock() throws IOException{
		if(!file.canWrite()) {
			throw new IOException("can not write file: " + file.getCanonicalFile());
		}

		raFile = new RandomAccessFile(file, "rw");

		fileChannel = raFile.getChannel();
		if(fileChannel == null){
			throw new IOException("can not lock file becouse can not get channel for " + file);
		}
		fLock = fileChannel.tryLock();
		if(fLock == null){
			throw new IOException("can not lock file becouse can not get flock for " + file);
		}
	}
	public synchronized void releaseLock() throws IOException{
		if(fLock != null){
			if(fLock.isValid()){
				fLock.release();
			}
			fLock = null;
		}
		if(fileChannel != null){
			if(fileChannel.isOpen()){
				fileChannel.close();
			}
			fileChannel = null;
		}
	}
	public void finalize(){
		try {
			if(fLock != null){
				LogHelper.log.warn("file lock is released by VM : %s", id);
			}
			this.releaseLock();
		} catch (IOException e) {
			LogHelper.log.error(e, "when finalize FileLock for %s", file);
		}
	}
	public void block() {
		while(true) {
			try {
				Thread.sleep(60 * 60  * 1000);
			} catch (InterruptedException e) {
				LogHelper.log.error(e, "when block FileLock for %s", file);
			}
		}
	}
	public File getFile() {
		return file;
	}
}
