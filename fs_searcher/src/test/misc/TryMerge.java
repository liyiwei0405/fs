package misc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.funshion.search.Counter;
import com.funshion.search.CounterNum;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LineWriter;

public class TryMerge {
	
	static class  F2JMap{
		Map<Character, Character> map = new HashMap<Character, Character>();
		public F2JMap(File f) throws IOException{
			LineReader lr = new LineReader(f, Charset.forName("utf-8"));
			while(lr.hasNext()){
				String line = lr.next();
				line = line.trim();
				if(line.length() == 0){
					continue;
				}
				if(line.length() != 3){
					System.out.println("error " + line);
					System.exit(0);
				}
				char ft = line.charAt(0);
				char jt = line.charAt(2);
				if(ft == jt){
					continue;
				}
				map.put(ft, jt);
			}
			System.out.println("total " + map.size());
			lr.close();
		}
	}
	
	public static void main(String[]args) throws IOException{
		Set<Character>set = new HashSet<Character>();
		LineReader lr = new LineReader("/tmp/fbd.txt");
		while(lr.hasNext()){
			String line = lr.next();
			if(line.length() == 0){
				continue;
			}
			if(line.length() != 1){
				System.out.println("error : " + line);
				continue;
			}
			set.add(line.charAt(0));
		}
		File fs[] = new File[]{
			new File("/f2j/1.txt"),
			new File("/f2j/2.txt"),
			new File("/f2j/3.txt"),
			new File("/f2j/4.txt"),
			new File("/f2j/0.txt"),
			
		};
		Map<Character, Character>[] maps = new HashMap[fs.length];
		for(int x = 0; x < fs.length; x ++){
			File f = fs[x];
			System.out.println("scan " + f);
			F2JMap f2j = new F2JMap(f);
			maps[x] = f2j.map;
		}
		Counter<String>cnt = new Counter<String>();
		for(Map<Character, Character> v : maps){
			Iterator<Entry<Character, Character>> itr = v.entrySet().iterator();
			while(itr.hasNext()){
				Entry<Character, Character> e = itr.next();
				String x = e.getKey() + "=" + e.getValue();
				cnt.count(x);
			}
		}
		HashMap<Character, Character>verify = new HashMap<Character, Character>();
		List<Entry<String, CounterNum>> lst = cnt.topEntry(10000);
		LineWriter lw = new LineWriter(new File("/tmp/signle.txt"), false);
		LineWriter lwAll = new LineWriter(new File("/tmp/lwAll.txt"), false);
		for(Entry<String, CounterNum> e : lst){
			char c = e.getKey().charAt(0);
			if(set.contains(c)){
				System.err.println("fbd:" + e);
				continue;
			}
			if(!set.contains(e.getKey().charAt(2))){
				System.err.println("fbd: jianti " + e);
				continue;
			}
			lwAll.writeLine(e.getKey());
			lwAll.flush();
			if(e.getValue().intValue() < 2){
				lw.writeLine(e);
				lw.flush();
				continue;
			}
			verify.put(e.getKey().charAt(0), e.getKey().charAt(2));
		}
		System.out.println("verified " + verify.size());
	}
}
