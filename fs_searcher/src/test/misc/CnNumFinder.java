package misc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.funshion.search.media.search.mediaTitleRewriter.RegRewriteHz;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineReader;

public class CnNumFinder {
	static final Pattern p = Pattern.compile("ｏ０Ｏ一二三四五六七八九十零百千万壹贰叁肆伍陆柒捌玖拾");
	static final String strWords = "ｏ０Ｏ一二三四五六七八九十零百千万仟萬壹贰叁肆伍陆柒捌玖拾";

	Set<Character>set = new HashSet<Character>();
	CnNumFinder(){
		for(char c : strWords.toCharArray()){
			set.add(c);
		}
	}

	public String numRewrite(String input){
		int cnt = 0;
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < input.length(); x ++){
			char c = input.charAt(x);
			switch(c){
			case 'ｏ':
			case '０':
			case 'Ｏ':
				c = '零';
				break;
			case '仟':
				c = '千';
				break;
			case '萬':
				c = '万';
				break;
			case '壹':
				c = '一';
				break;
			case '贰':
				c = '二';
				break;
			case '叁':
				c = '三';
				break;
			case '肆':
				c = '四';
				break;
			case '伍':
				c = '五';
				break;
			case '陆':
				c = '六';
				break;
			case '柒':
				c = '七';
				break;
			case '捌':
				c = '八';
				break;
			case '玖':
				c = '九';
				break;
			case '拾':
				c = '十';
				break;
			}
			if(set.contains(c)){
				sb.append(c);
				cnt ++;
			}else{
				sb.append(' ');
			}
		}
		if(cnt == 0){
			return null;
		}

		StringBuilder ret = new StringBuilder();
		for(int x = 0; x < input.length(); x ++){
			char c = sb.charAt(x);
			if(c == ' '){
				ret.append(input.charAt(x));
			}else{
				int start = x;
				for(;x < input.length(); x ++){
					char nchar = sb.charAt(x);
					if(nchar == ' '){
						break;
					}
				}
				String sub = input.substring(start, x);
				int num = rewrite(sub);
				if(num > 0){
					ret.append(num);
					
				}else{//parse fail, append org value
					ret.append(sub);
				}
				x --;
			}
		}
		String retString = ret.toString();
		if(retString.equals(input)){
			return null;
		}
		return ret.toString();
	}
	private int rewrite(String str){
		if(str == null || str.length() == 0){
			return RegRewriteHz.errorRet;
		}
		if(str.length() ==1){
			char c = str.charAt(0);
			if(c == '百' || c =='千' || c == '万' || c == '零'){
				return RegRewriteHz.errorRet;
			}
		}

		int v = RegRewriteHz.parseNum(str);
		if(v > 0){
			return v;
		}
		return RegRewriteHz.errorRet;
	}
	
	public static void main(String[]args) throws IOException{
		CnNumFinder cf = new CnNumFinder();
		LineReader lr = new LineReader("/testWords");

		while(lr.hasNext()){
			String line = lr.next().trim();
//			line = "一九十二年";
			//			System.out.println(line);
			String ctt = cf.numRewrite(line);
			if(ctt == null){
				System.out.println(line + " ----> " + ctt);
				System.out.flush();
			}else{
				System.err.println(line + " ----> " + ctt);
				System.err.flush();
				Consoler.readString("press enter to continue...");
			}
		}
		lr.close();
	}
}
