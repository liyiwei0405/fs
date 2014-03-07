package com.funshion.utils.http;

import java.io.File;
import java.net.URL;

import com.funshion.search.utils.LogHelper;

public class CrawlContext implements Runnable{
		private final URL url;
		private File file;
		final HttpHandler hdl;
		private int totRead = -1;
		private int httpCode = -1;
		private boolean okRun = false;
		private boolean isDownload = false;
		private LogHelper log;
		private boolean allowDownload = false;
		public CrawlContext(URL url, File file, HttpHandler hdl, LogHelper log){
			this.url = url;
			this.setFile(file);
			this.hdl = hdl;
			this.log = log;
		}
		public void run(){
			HttpCrawler crl = null;
			try {
				crl = HttpCrawler.makeCrawler(getUrl(), hdl);
				if(200 == crl.getHttpCode()){
					DownloadTypeParser dpp = DownloadTypeParser.getParser(crl.getURL(), getUrl(), crl.getHeadInfo());

					boolean isDownload = dpp.isDownloadType();
					String suffix = null;
					if(isDownload){
						suffix = dpp.suffix();
						if(suffix != null){
							this.setDownload(true);
						}
					}
					if(this.isDownload()){
						if(!allowDownload){
							crl.close();
							log.error("reject read url %s beacouse not allow download", getUrl());
							return;
						}
					}
					crl.dumpTo(getFile());
					crl.close();

					totRead = crl.getTotalReaded();
					this.setHttpCode(crl.getHttpCode());


					if(this.isDownload()){
						File renameTo = new File(getFile().getParent(), getFile().getName() + "." + suffix);
						if(!getFile().renameTo(renameTo)){
							log.error("rename error! from %s, to %s", getFile(), renameTo);
						}else{
							this.setFile(renameTo);
						}
					}
					setOkRun(true);
				}else{
					log.error("error httpCode %s for url %s", crl.getHttpCode(), getUrl());
					crl.close();
				} 
			}catch (Exception e) {
				log.error(e, "when crawl %s", getUrl());
			}finally{
				if(crl != null){
					crl.close();
				}
			}
		}
		public int getTotRead() {
			return totRead;
		}
		public void setHttpCode(int httpCode) {
			this.httpCode = httpCode;
		}
		public int getHttpCode() {
			return httpCode;
		}
		public void setOkRun(boolean okRun) {
			this.okRun = okRun;
		}
		public boolean isOkRun() {
			return okRun;
		}
		public void setFile(File file) {
			this.file = file;
		}
		public File getFile() {
			return file;
		}
		public URL getUrl() {
			return url;
		}
		public void setDownload(boolean isDownload) {
			this.isDownload = isDownload;
		}
		public boolean isDownload() {
			return isDownload;
		}
	}