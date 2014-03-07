package test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.Charsets;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.Misc;
import com.funshion.utils.http.HttpCrawler;

public class CrawlPage {
	private LineWriter excepLw = null;
	private File exceptionFile = null;
	ConfigReader cr = null;
	
	List<URL> urlList = new ArrayList<URL>();
	
	public CrawlPage() throws IOException{
		this.exceptionFile = new File("./exceptions.txt");
		this.excepLw = new LineWriter(exceptionFile, false, Charsets.UTF8_CS);
		this.cr = new ConfigReader(new File("./config/crawler.conf"), "service");
	}
	
	@SuppressWarnings("resource")
	public void readUrls(File urlFile) throws Exception{
		File usetimeFile = new File("./useTimes-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS").format(System.currentTimeMillis()) + ".txt");
		
		String line = "";
		LineReader lr = new LineReader(urlFile, "utf-8");
		LineWriter usetimeLw = new LineWriter(usetimeFile, true, Charsets.UTF8_CS);
		
		String urlPrefix = this.cr.getValue("urlPrefix");
		System.out.println(urlPrefix);
		while( (line = lr.readLine()) != null){
			this.urlList.add(new URL( urlPrefix + line));
		}
		lr.close();
		
		int i = 0;
		while(true){
			i ++;
			
			int rand = Misc.randInt(0, urlList.size());
			URL url = urlList.get(rand);
			long time = crawl(url);
			if(time != 0){
				usetimeLw.writeLine(time + "\t" + url);
			}
			if(i > 10){
				i = 0;
				usetimeLw.flush();
			}
		}
	}
/**
 * urls
 * 函数调用crawl，crawl中记录时间
 * lw
 * @param url
 * @throws IOException
 */
	public long crawl(URL url) throws IOException{
		HttpCrawler crl = new HttpCrawler(url);
		long t1 = System.currentTimeMillis();
		try{
			crl.connect();
			crl.getBytes();
		}catch(Exception e){
			this.excepLw.writeLine(url + "\t" + e.getMessage());
			return 0;
		}finally{
			crl.close();
		}

		long t2 = System.currentTimeMillis();
		
		return t2 - t1;
		
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configureAndWatch(ConfUtils.getConfFile("log4j.properties").getAbsoluteFile().toString());
		File urlFile = new File("./q_client.log");
		
		CrawlPage cp = new CrawlPage();
		cp.readUrls(urlFile);
		
		cp.excepLw.close();
//		File usetimeFile = new File("./useTimes.txt");
//		LineWriter usetimeLw = new LineWriter(usetimeFile, false, Charsets.UTF8_CS);
//		usetimeLw.write("ssss");
//		usetimeLw.close();
	}
}
