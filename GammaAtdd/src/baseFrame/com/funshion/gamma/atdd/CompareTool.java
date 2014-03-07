package com.funshion.gamma.atdd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.funshion.gamma.atdd.serialize.ThriftSerializeTool;
import com.funshion.search.utils.Charsets;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.LogHelper;
import com.funshion.search.utils.Misc;
import com.funshion.search.utils.TchFileTool;

public class CompareTool {
	static LogHelper log = new LogHelper("compareTool");
	static String cmdShell;
	static File workDir;
	static{
		cmdShell = TchFileTool.get(new File("./config/"), "compareTool");
		if(cmdShell == null || cmdShell.length() == 0){
			log.error("compare config Fail!!!! please create a new File at %s and set the compareTool cmd", 
					new File("./config/compareTool.tch"));
		}
		workDir = new File("./tmp/compareTool/");
		log.warn("compare  will be distoried when system reboot!", workDir);
		log.warn("compare dir %s", workDir);
		if(!workDir.exists()){
			workDir.mkdirs();
		}
		File fs[] = workDir.listFiles();

		if(fs != null){	
			log.warn("cleaning old compare files in %s", workDir);
			for(File file : fs){
				Misc.del(file);
			}
		}


	}
	public static void compare(final File real, final File expect) throws Exception{
		if(cmdShell == null){
			throw new Exception("compare cmd shell init fail! MayBe BCompare's path not set to file:config/compareTool.tch");
		}
		
		//		builder.directory(new File("e:/")); 
		new Thread(){
			@Override
			public void run(){
				try {
					final ProcessBuilder builder = new ProcessBuilder(cmdShell, 
							real.getAbsoluteFile().getCanonicalPath(), 
							expect.getAbsoluteFile().getCanonicalPath()); 
					builder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public static void compare(Object input, Object real, Object expect) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss.SSS");
		File inputDir ;
		while(true){
			String prefix = sdf.format(System.currentTimeMillis());
			inputDir = new File(workDir, prefix);
			if(inputDir.exists()){
				Thread.sleep(4);
				continue;
			}else{
				if(!inputDir.mkdirs()){
					throw new Exception("can not create dir " + inputDir);
				}
				break;
			}
		}
		File inputFile = new File(inputDir, "input.txt");
		File realFile = new File(inputDir, "real.txt");
		File expectFile = new File(inputDir, "expect.txt");
		save(inputFile, input);
		save(realFile, real);
		save(expectFile, expect);
		log.warn("compare path has created as %s", inputDir.getAbsoluteFile().getCanonicalFile());
		
		compare(realFile, expectFile);
	}
	private static void save(File toSave, Object o) throws Exception{
		String toSaveObject = ThriftSerializeTool.objectGen(o);
		LineWriter lw = new LineWriter(toSave, false, Charsets.UTF8_CS);
		lw.writeLine(toSaveObject);
		lw.close();
	}
	
	public static void main(String []args) throws Exception{
		
		
		Integer input = 1;
		Integer real = 2;
		Integer expect = 3;
		compare(input, real, expect);
	}
}
