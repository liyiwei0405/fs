package com.funshion.gamma.atdd.serialize.shell;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import com.funshion.gamma.atdd.AbstractThriftService;
import com.funshion.search.utils.Charsets;
import com.funshion.search.utils.LineWriter;
public abstract class AbstractTemplateGenCmdShell {
	public static final String VER = "@atddGenor.ver.0.1";
	private File templatesGenDir = new File("./templatesGenDir");
	public final String svcName;
//	final Class<?>svcClass;
	final Class<?> clsIface;
	public AbstractTemplateGenCmdShell(String svcName) throws Exception{
		this(Class.forName(svcName + AbstractThriftService.IfaceDepector));
	}
	public AbstractTemplateGenCmdShell(Class<?>clsIface) throws Exception{
		this.svcName = clsIface.getName().replace(AbstractThriftService.IfaceDepector, "");
//		this.svcClass = Class.forName(svcName);
		this.clsIface = clsIface;
	}
	protected abstract File mkdirs() ;

	protected String strName(){
		return lastToken(svcName) + "(" + 
				svcName + ")";
	}
	private String lastToken(String cName){
		int pos = cName.lastIndexOf('.');
		String mName = "";
		if(pos == -1){
			return cName;
		}else{
			mName = cName.substring(pos + 1);
		}
		return mName;
	}
	public String methodName(Method m){
		String mName = m.getName();
		Class<?> []clses = m.getParameterTypes();
		for(Class<?> cls : clses){
			String cName = cls.getName();
			mName += "-" + lastToken(cName);
		}
		return mName + ".method";
	}
	public abstract void helpInfo() ;
	public abstract void exe() throws Exception;


	public File getTemplatesGenDir() {
		return templatesGenDir;
	}
	public void setTemplatesGenDir(File templatesGenDir) {
		this.templatesGenDir = templatesGenDir;
	}


	class Writer{
		final LineWriter lw;

		Writer(File f) throws IOException{
			f.getParentFile().mkdirs();
			lw = new LineWriter(f, false, Charsets.UTF8_CS);
		}
		public void writeLine(Object ...args) throws IOException{
			for(Object o : args){
				lw.write(o);
				System.out.print(o);
			}
			lw.writeLine("");
			System.out.println();
		}

		public void close() {
			lw.close();
		}
	}
}
