package pressTest;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.funshion.search.Counter;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineReader;

public class ParsePressTestResult {
	Pattern p = Pattern.compile("^\\d{2}\\-\\d{2}\\-\\d{2}");

	Counter<Integer>cntInner = new Counter<Integer>();
	Counter<Integer>cntOutter = new Counter<Integer>();
	ParsePressTestResult(File file) throws IOException{
		this.parse(file);
	}
	public void parseFile(File f) throws IOException{
		LineReader lr = new LineReader(f);
		while(lr.hasNext()){
			String line = lr.next();
			Matcher m = p.matcher(line);
			if(!m.find()){
				System.out.println("misMatch " + line);
				continue;
			}
			String[] token = line.split("\t");
			int inner = (int) (0.5 + Double.parseDouble(token[3]));
			int outter = (int) (0.5 + Double.parseDouble(token[4]));
			if(inner == 0 && outter == 0){
				System.out.println("\t" + line);
			}
			cntInner.count(inner);
			cntOutter.count(outter);
		}
		System.out.println("now size:" + cntOutter.getMap().size());
		lr.close();
	}
	public void parse(File file) throws IOException{
		
		if(file.isDirectory()){
			System.out.println("parsing dir " + file);
			File fs[] = file.listFiles();
			for(File f : fs){
				parse(f);
			}
		}else{
			System.out.println("parsing file " + file);
			parseFile(file);
		}
	}
	public static void main(String[]args) throws IOException{
		String dir = Consoler.readString("dir or file:");
		File dirFile = new File(dir);
		
		ParsePressTestResult pptr = new ParsePressTestResult(dirFile);
		pptr.print();
	}
	private void print() {
		System.out.println("cntInner.getMap():\n" + cntInner.getMap());
		System.out.println();
		System.out.println();
		System.out.println("cntOutter.getMap():\n" + cntOutter.getMap());
		
		
	}
}
