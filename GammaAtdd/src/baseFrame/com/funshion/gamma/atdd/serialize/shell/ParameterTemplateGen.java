package com.funshion.gamma.atdd.serialize.shell;

import java.io.File;
import java.lang.reflect.Method;

import com.funshion.gamma.atdd.serialize.ThriftSerializeTool;

public class ParameterTemplateGen extends AbstractTemplateGenCmdShell{
	public ParameterTemplateGen(String svcName) throws Exception{
		super(svcName);
	}
	public ParameterTemplateGen(Class<?> svcClass) throws Exception{
		super(svcClass);
	}
	@Override
	public void exe() throws Exception {
		File baseDir = mkdirs();
		Method [] ms = clsIface.getMethods();
		for(int idx = 0; idx < ms.length; idx ++){
			Method m = ms[idx];
			String methodName = ThriftSerializeTool.methodStringName(m);
			String fileName = methodName;
			String str = ThriftSerializeTool.methodParameterTemplateGen(m);
			File cfgFile = new File(baseDir, fileName + ".para");
			Writer writer = new Writer(cfgFile);
			writer.writeLine(str);
			writer.lw.write("##");
			writer.close();
			
			str = ThriftSerializeTool.methodReturnTemplateGen(m);
			cfgFile = new File(baseDir, fileName + ".ret");
			writer = new Writer(cfgFile);
			writer.writeLine(str);
			
			writer.lw.write("##");
			writer.close();
		}

	}
	@Override
	public void helpInfo(){
		//TODO may help?
	}
	@Override
	protected File mkdirs() {
		File baseDir = new File(getTemplatesGenDir(), strName() + "/method/" );
		baseDir.mkdirs();
		System.out.println("workDirctory:" + baseDir.getPath());
		return baseDir;
	}
	
}
