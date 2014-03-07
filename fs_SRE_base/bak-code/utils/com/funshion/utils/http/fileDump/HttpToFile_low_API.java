package com.funshion.utils.http.fileDump;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.funshion.search.utils.LogHelper;
import com.funshion.utils.http.HttpCrawler;
import com.funshion.utils.http.HttpHandler;

public abstract class HttpToFile_low_API {
	protected final int minAllowLen;
	protected URL nextURL;
	protected File nextFile;
	private int minWait = 3000;//
	private HttpHandler hdl;
	public HttpToFile_low_API(int minAllowLen) {
		this.minAllowLen = minAllowLen;
	}
	/**
	 * make nextUrl and nextFie
	 * @return
	 * @throws IOException 
	 */
	public abstract boolean hasNext() throws IOException;

	/**
	 * 
	 * @return false means skip this one, true means ok crawlled
	 * @throws IOException
	 */
	public boolean next() throws IOException {
		LogHelper.log.info("to file %s from %s",
				this.nextFile.getName(), this.nextURL);
		if(nextFile.exists()) {
			if(this.nextFile.length() >= minAllowLen) {
				LogHelper.log.warn(
						"Skip file %s, minAllowLen = %s, fileLen = %s, from %s",
						this.nextFile.getName(), minAllowLen, this.nextFile.length(),
						this.nextURL);
				return false;
			}
		}
		HttpCrawler crl = new HttpCrawler(this.nextURL, hdl);
		crl.connect();
		crl.dumpTo(this.nextFile);
		crl.close();
		LogHelper.log.debug("Ok file %s from %s",
				this.nextFile.getName(), this.nextURL);
		return true;
	}
	public void start(boolean stopWhenError) {
		while(true) {
			try {
				if(hasNext()) {
					break;
				}

				next();
				if(minWait > 0) {
					Thread.sleep(this.minWait);
				}
			} catch (Exception e) {
				LogHelper.log.error(e, "when doing file %s from %s",
						this.nextFile.getName(), this.nextURL);
				if(stopWhenError) {
					break;
				}
			}
		}
	}
	public void setHdl(HttpHandler hdl) {
		this.hdl = hdl;
	}
	public HttpHandler getHdl() {
		return hdl;
	}
	public void setMinWait(int minWait) {
		this.minWait = minWait;
	}
	public int getMinWait() {
		return minWait;
	}
}
