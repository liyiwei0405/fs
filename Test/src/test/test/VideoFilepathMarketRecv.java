package test;

import java.io.File;
import java.nio.charset.Charset;

import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LineWriter;

public class VideoFilepathMarketRecv {

	public static void main(String[] args) throws Exception {
		LineReader lr = new LineReader(new File("./htm.txt"));
		
		LineWriter lw = new LineWriter(new File("./market.txt"), false, Charset.forName("utf-8"));
		
		int i = 0;
		String line = "";
		while((line = lr.readLine()) != null){
			if(line.contains("/market/")){
				i ++;
				System.out.println(line);
				lw.write(line.split("\t")[0] + "\n");
			}
		}
		lw.close();
		lr.close();
		System.out.println("total: " + i);
	}
}
