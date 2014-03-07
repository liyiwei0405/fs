package com.funshion.search;

import java.io.IOException;


public abstract class IndexableFs extends ChgExportFS{

	public IndexableFs() {
		super(false);
	}
	public synchronized void switchChgDir(DatumFile dFile) throws IOException {
		super.switchChgDir(dFile);
		rotateIndex();
	}
	public synchronized void putDatumFile(DatumFile f) throws IOException {
		super.putDatumFile(f);
		updateIndex();
	}
	public abstract void updateIndex() throws IOException;
	public abstract void rotateIndex() throws IOException;
}
