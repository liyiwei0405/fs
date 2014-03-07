package com.funshion.search.analyzers;

public class CharacterFormat {
	/**
	 * 将字符串做全半角转换，并进一步将非Character和Digital的字符以空格代替(多空格不重复计数，即相当于合并为一个空格)
	 * @param input
	 * @return
	 */
	public static String rewriteString(String str){
		StringBuilder sb = new StringBuilder();
		boolean lastWS = true;
		for(int x = 0; x < str.length(); x ++){
			char c = SBCCaseFilter(str.charAt(x));
			if(Character.isLetterOrDigit(c)){
				sb.append(Character.toLowerCase(c));
				lastWS = false;
			}else{
				if(!lastWS){
					sb.append(' ');
					lastWS = true;
				}
			}
		}
		return sb.toString().trim();
	}
	/**
	 * 将字符ch转化为标准表示形式：
	 * <br>将全角转为半角
	 * <br>将大写转为小写
	 * @param ch
	 * @return
	 */
	public static char SBCCaseFilter(char ch){
		char chnew;
		if (ch >= 65281 && ch <= 65374) {   
			chnew = ((char)(ch - 65248)); 
		}else if (ch == 12288){   
			chnew = (' ');   
		}else if(ch == 65377){   
			chnew = ('。');   
		}else if(ch == 12539){   
			chnew = ('·');   
		}else if(ch ==8226){ 
			chnew = ('·');   
		}else if(ch == '　'){ 
			chnew = (' ');   
		}else if(ch == '—'){ 
			chnew = ('-');   
		}else if(ch == '【'){ 
			chnew = ('[');   
		}else if(ch == '】'){ 
			chnew = (']');   
		}else{   
			chnew = (ch);   
		}
		
		return Character.toLowerCase(chnew);
	}

}
