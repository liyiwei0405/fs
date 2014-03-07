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
public class FileBasedLock extends Thread{
	public final File LOCK_PATH = new File("./_f_locks_/");
	private File file;
	FileLock fLock;
	FileChannel fileChannel;
	final String id;
	RandomAccessFile raFile;
	public FileBasedLock(String id) throws IOException{
		if(!LOCK_PATH.exists()) {
			LOCK_PATH.mkdirs();
		}
		setFile(new File(LOCK_PATH, id+".lock_"));
		if(!getFile().exists()){
			getFile().createNewFile();
		}
		this.id = id;
	}
	/**
	 * try to lock on this 
	 * @return
	 * @throws IOException 
	 */
	public synchronized void tryLock() throws IOException{
		if(!getFile().canWrite()) {
			throw new IOException("can not write file: " + getFile().getCanonicalFile());
		}

		raFile = new RandomAccessFile(getFile(),"rw");

		fileChannel = raFile.getChannel();
		if(fileChannel == null){
			throw new IOException("can not lock file becouse can not get channel for " + getFile());
		}
		fLock = fileChannel.tryLock();
		if(fLock == null){
			throw new IOException("can not lock file becouse can not get flock for " + getFile());
		}
	}
	public synchronized void releaseLock(){
		if(fLock != null){
			if(fLock.isValid()){
				try {
					fLock.release();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fLock = null;
		}
		if(fileChannel!=null){
			if(fileChannel.isOpen()){
				try {
					fileChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fileChannel = null;
		}
		synchronized(started) {
			started.notifyAll();
		}
	}
	public void finalize(){
		this.releaseLock();
		System.out.println("file lock is released by VM : " + id);
	}
	public void block() {
		while(true) {
			try {
				Thread.sleep(60 * 60  *1000);
			} catch (InterruptedException e) {
			}finally {
			}
		}
	}
	private Boolean started = false;
	public void nonBlock() {
		if(started) {
			return;
		}
		synchronized(started) {
			if(!started) {
				this.start();
			}
		}
	}
	public void run() {
		synchronized(started){
			try {
				started.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}
