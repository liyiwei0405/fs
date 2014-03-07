package com.funshion.gamma.atdd.serialize;

public class StringTokenlizer{
	int crtPosStart;
	int crtPosEnd = 0;
	final String str;
	public StringTokenlizer(String str){
		this.str = str;
	}
	public String getString(int []arr){
		return str.substring(arr[0], arr[1]);
	}
	public boolean isEmptyToken(int[]arr){
		if(arr[1] - arr[0] < 2){
			switch(str.charAt(arr[0])){
			case ' ':
			case '\t':
				return true;
			}
		}
		return false;
	}
	
	public String nextNotEmptyToken(){
		int[] arr = nextNotEmptyTokenIndex();
		if(arr == null){
			return null;
		}
		return this.getString(arr);
	}
	public int[] nextNotEmptyTokenIndex(){
		while(true){
			int []arr = this.nextTokenIndex();
			if(arr == null){
				return null;
			}
			if(isEmptyToken(arr)){
				continue;
			}
			return arr;
		}

	}
	public int[] nextTokenIndex(){
		if(crtPosEnd == -1){
			return null;
		}
		crtPosStart = crtPosEnd;
		crtPosEnd = findStopTokenPos(str, crtPosStart);
		if(crtPosEnd == -1){
			return null;
		}
		return new int[]{
				crtPosStart, crtPosEnd	
		};
	}

	static boolean isStop(char c){
		for(char cFBD : stopToken){
			if(c == cFBD){
				return true;
			}
		}
		return false;
	}
	private static char[]stopToken = new char[]{
		' ', '\t', '{', '[', ':'
	};
	public static int findStopTokenPos(String str, int from){
		if(from >= str.length()){
			return -1;
		}
		int len = str.length();
		for(int x = from; x < len; x ++){
			char c = str.charAt(x);
			if(isStop(c)){
				if(x == from){
					return 1 + x;
				}
				return x;
			}
		}
		//not find, point to end
		return str.length();
	}
	public String left() {
		return str.substring(crtPosEnd);
	}


}