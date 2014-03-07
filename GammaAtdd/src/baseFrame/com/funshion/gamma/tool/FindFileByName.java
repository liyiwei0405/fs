package com.funshion.gamma.tool;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindFileByName {

	public static void main(String[]args) throws IOException{
		File f = null;
		String pattern = null;
		for(String x : args){
			if(x.startsWith("-f")){
				f = new File(trim(x.substring(2)));
				if(!f.isDirectory() || !f.exists()){
					help();
					System.exit(0);
				}
			}
			if(x.startsWith("-p")){
				pattern = trim(x.substring(2).trim());
				if(pattern.length() == 0){
					help();
					System.exit(0);
				}
			}
		}
		if(f == null || pattern == null){
			help();
			System.exit(0);
		}
		
		Pattern p = Pattern.compile(pattern.toLowerCase());
		
		recursFind(f, p);
	}
	static void recursFind(File f, Pattern p) throws IOException{
		if(f.isDirectory()){
			File fs[] = f.listFiles();
			for(File f2 : fs){
				recursFind(f2, p);
			}
		}else{
			String str = f.getName().toLowerCase();
			Matcher m = p.matcher(str);
			if(m.find()){
				System.out.println(f.getCanonicalPath());
			}
		}
	}
	static String trim(String str){
		while(str.length() > 0){
			if(str.startsWith("\"")){
				str = str.substring(1).trim();
			}else{
				break;
			}
		}
		while(str.length() > 0){
			if(str.endsWith("\"")){
				str = str.substring(10, str.length() - 1).trim();
			}else{
				break;
			}
		}
		return str;
	}
	private static void help() {
		System.out.println("usage: -fDirtoSearch -pFileNamePatternToFind");

	}
}
