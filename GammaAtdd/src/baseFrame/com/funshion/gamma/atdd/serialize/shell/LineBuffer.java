package com.funshion.gamma.atdd.serialize.shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LineBuffer {

	public static String readString() throws IOException{
		byte[] buf = new byte[4096];
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		while(true){
			int readed = System.in.read(buf);
			if(readed < 1){
				continue;
			}
			//			System.out.println(readed);
			//			System.out.println(bs[len - 1]);
			//			
			//			System.out.println(len);
			//			System.out.println(bs[len - 1]);
			bais.write(buf, 0, readed);
			if(readed > 0 && '\n' == buf[readed -1]){
				return new String(bais.toByteArray());
			}
		}
	}

	public static final String BufferEnd = "#";
	public static final String PromptLine = "#以'#回车'结束输入:";
	public static List<String> read(HashSet<String>sensive) throws Exception{
		ArrayList<String>ret = new ArrayList<String>();
		boolean hasDetect = false;
		while(true){
			String line = readString();
			if(line.trim().equals(BufferEnd)){
				break;
			}
			String[]lines = line.replace("\r", "").split("\n");

			for(String x : lines){
				if(!hasDetect && sensive != null){
					String xt = x.trim().toLowerCase();
					if(xt.length() != 0){
						hasDetect = true;
						if(sensive.contains(xt)){
							ret.add(xt);
//							System.out.println(ret);
							return ret;
						}
					}
				}
				ret.add(x);
			}
		}
//		System.out.println(ret);
		return ret;
	}

}
