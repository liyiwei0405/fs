package com.funshion.search;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.funshion.search.utils.ConfigReader;


public abstract class IndexableFs extends ChgExportFS{
	private boolean canServiceNow = false;
	public final File indexPathDir;
	public IndexableFs(ConfigReader cr) throws IOException {
		super(false, cr);
		try {
			indexPathDir = new File(cr.getValue("indexPath","/indexes"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.lockAndClearPath(indexPathDir);
	}
	public synchronized void switchChgDir(DatumFile dFile) throws Exception {
		super.switchChgDir(dFile);
		rotateIndex();
	}
	public synchronized void putDatumFile(DatumFile f) throws IOException {
		super.putDatumFile(f);
		updateIndex();
	}
	public abstract void updateIndex() throws IOException;
	public abstract void rotateIndex() throws Exception;
	
	protected File getNewIndexDirectory() throws IOException{
		File f;
		while(true){
			f = new File(indexPathDir, new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS").format(System.currentTimeMillis()) + ".vlssIdx");
			if(f.exists()){
				continue;
			}else{
				break;
			}
		}
		return f;
	}
	public boolean isCanService() {
		return canServiceNow;
	}
	protected void setCanServiceNow(boolean canService) {
		log.info("set canServiceNow = %s", canService);
		this.canServiceNow = canService;
	}
}
