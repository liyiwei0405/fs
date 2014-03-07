package com.funshion.search;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.funshion.search.utils.LogHelper;
/**
 * @author liying
 */
public class ChgExportFS{
	protected final LogHelper log = new LogHelper("ExportFS");
	protected DatumDir crtDatumDir;
	public final File sysExportDir;
	public final File sysExportTmpDir;
	private final void checkDir(File checkDir) throws IOException{
		if(!checkDir.exists()){
			if(!checkDir.mkdirs()){
				throw new IOException("can not mkdir:" + checkDir.getAbsolutePath());
			}
		}else{
			if(!checkDir.isDirectory()){
				throw new IOException("must be dir:" + checkDir.getAbsolutePath());
			}
		}
	}
	public ChgExportFS(boolean serverModel){
		if(serverModel){
			sysExportDir = new File("./chg/");
			sysExportTmpDir = new File("./chg-tmp-sys/");
		}else{
			sysExportDir = new File("./syn-chg/");
			sysExportTmpDir = new File("./syn-chg-tmp-sys/");
		}
		try{
			checkDir(sysExportDir);
			checkDir(sysExportTmpDir);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	public File getNewSeqFile(boolean createNew) throws IOException{
		File dir;
		int seq;
		if(createNew){
			dir = getNewDir(null);
			seq = 0;
		}else{
			 dir = this.crtDatumDir.get(0).file.getParentFile();
			 seq = this.crtDatumDir.size();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS");
		for(;;){
			File f = new File(dir, "irec."+seq+"." + sdf.format(System.currentTimeMillis()));
			if(f.exists()){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			return f;
		}
	}
	public File getNewDir(String name)throws IOException{
		if(name == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS");
			for(;;){
				File f = new File(this.sysExportDir, "irec.dir." + sdf.format(System.currentTimeMillis()));
				if(f.exists()){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}else{
					if(!f.mkdirs()){
						log.warn("can not create newDir %s", f);
						continue;
					}
				}
				return f;
			}
		}else{
			File f = new File(this.sysExportDir, name);
			if(!f.exists()){
				if(!f.mkdirs()){
					throw new IOException("can not create newDir " + f.getName());
				}
			}else{
				if(!f.isDirectory()){
					throw new IOException("has regular file exist！ " + f.getName());
				}
			}
			return f;
		}
	}

	public void switchChgDir(DatumFile dFile) throws IOException {
		log.info("switch crtChgDir! from %s to new dir with main file %s", crtDatumDir, dFile);
		DatumDir old = this.crtDatumDir;
		this.crtDatumDir = new DatumDir(dFile);
		log.info("switch datum dir! from '%s' to '%s'", old, this.crtDatumDir);
		if(old != null){
			log.warn("deleting old datum dir : '%s'", old);
			delOldDatum(old);
		}
	}
	private void delOldDatum(DatumDir old) {
		// FIXME not implements yet!
	}

	public File prepareTmpFile() throws IOException{
		while(true){
			int rnd = (int) (Math.random() * Integer.MAX_VALUE);
			File tmp = new File(this.sysExportTmpDir, rnd + ".tmp");
			if(!tmp.exists()){
				return tmp;
			}else{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
		}
	}
	public boolean isCurrentDirName(String newDirName) {
		if(this.crtDatumDir == null){
			return false;
		} 
		return this.crtDatumDir.equalName(newDirName);
	}
	public int checkChg(String newDirName, int fileNum) {
		if(isCurrentDirName(newDirName)){
			if(crtDatumDir.size() < fileNum){
				return crtDatumDir.size();
			}else{
				return -1;
			}
		}else{
			return 0;
		}
	}
	public void putDatumFile(DatumFile f) throws IOException {
		log.info("add datumFile %s to %s", f, this.crtDatumDir);
		this.crtDatumDir.putDatumFile(f);
	}
	public boolean currentDatumDirValid() {
		return this.crtDatumDir != null;
	}
	public DatumDir newCopyOfCurrentDatumDir() {
		if(crtDatumDir == null){
			return null;
		}else{
			return this.crtDatumDir.copyOf();
		}
	}
}
