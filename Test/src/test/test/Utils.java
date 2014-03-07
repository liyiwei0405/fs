package test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isChinese(String str) {    
        String regex = "[\\u4e00-\\u9fa5]";    
        Pattern pattern = Pattern.compile(regex);    
        Matcher matcher = pattern.matcher(str);    
        return matcher.find();    
    } 
    
	//trim name after replace html space,\t,\n with space
	public static String trimString(String input) {
		if(input == null){
			return "";
		}
		return input.replace((char)160, ' ').replace('\t', ' ').replace('\n', ' ').trim();
	}

	public static List<String> splitToList(String input, String seperator){
		String arr[] = input.split(seperator);
		return Arrays.asList(arr);
	}
	
	
	public static String rewriteText(String content) {
		if(content == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if(c == '&') {
				if((i + 1 < content.length())&&content.charAt(i + 1) == '#') {
					int end = getEnd(content, i + 2);
					if(end < 3) {
						sb.append(c);
					}else {
						String s = content.substring(i + 2, 
								i + 2 + end);
						try {
							c = (char) Integer.parseInt(s);
							sb.append(c);
							i = i + 2 + end;
						}catch(Exception e) {
							sb.append(c);
						}

					}
				}else {
					sb.append(c);
				}
			}else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	static int getEnd(String content, int start) {
		int end = 0;
		for(int i = 0; i < 7; i++) {
			if(i + start >= content.length()) {
				if(i < 2) {
					return -1;
				}
				return i;
			}
			char c = content.charAt(i + start);
			if(c == ';') {
				end = i;
				break;
			}else if(c >= '0' && c <= '9') {
				continue;
			}else {
				return  -1;
			}
		}
		if(end < 2) {
			return -1;
		}
		return end;
	}
}
