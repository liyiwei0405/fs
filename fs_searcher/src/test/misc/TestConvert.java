package misc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.funshion.search.ConfUtils;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LogHelper;

public class TestConvert {
	LogHelper log = new LogHelper("f2j");
	Map<Character, Character>map = new HashMap<Character, Character>();
	public static final TestConvert instance = new TestConvert();
	private TestConvert(){
		try{
			LineReader lr = new LineReader(ConfUtils.getConfFile("f2j.txt"), Charset.forName("utf-8"));
			while(lr.hasNext()){
				String line = lr.next();
				line = line.trim();
				if(line.length() == 0){
					continue;
				}
				if(line.length() != 3){
					log.warn("error f2j %s", line);
					continue;
				}
				char ft = line.charAt(0);
				char jt = line.charAt(2);
				if(ft == jt){
					continue;
				}
				map.put(ft, jt);
			}
			lr.close();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		log.info("f2j total map size %s", map.size());
	}
	
	public char convert(char c){
		Character ret = this.map.get(c);
		if(ret == null){
			return c;
		}
		return ret;
	}
	
	public String conver(String str){
		StringBuilder sb = new StringBuilder();
		char[] ret = str.toCharArray();
		for(char c : ret){
			sb.append(convert(c));
		}
		
		return sb.toString();
	}
	
	public static void main(String[]args) throws IOException{
		File from = new File("/usr/local/search/media/var/chgDataDir/irec.dir.2013-05-06_15_18_20.913/irec.0.2013-05-06_15_18_20.913");
		File to = new File("/cvt.txt");
		LineReader lr = new LineReader(from);
		HashSet<String>set = new HashSet<String>();
		HashSet<String>CVT = new HashSet<String>();
		while(lr.hasNext()){
			String line = lr.next();
			if(CVT.contains(line)){
				continue;
			}
			CVT.add(line);
			String cvted = instance.conver(line);
			if(line.equals(cvted)){
				continue;
			}
			
			System.out.println(line + " ==> " + cvted);
			
//			char []cs = line.toCharArray();
//			for(char c : cs){
//				char c2 = instance.convert(c);
//				if(c2 != c){
//					String error = c + "=" +c2;
//					if(set.contains(error)){
//						continue;
//					}
//					System.out.println(error);
//					continue;
//				}
//			}
			
		}
	}
}
