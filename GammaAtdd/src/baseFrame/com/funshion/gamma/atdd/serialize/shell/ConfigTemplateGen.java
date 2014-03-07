package com.funshion.gamma.atdd.serialize.shell;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import com.funshion.gamma.atdd.serialize.SerialType;
public class ConfigTemplateGen extends AbstractTemplateGenCmdShell{
	public ConfigTemplateGen(String svcName) throws Exception{
		super(svcName);
	}
	public ConfigTemplateGen(Class<?> svcClass) throws Exception{
		super(svcClass);
	}
	private String[] helpInfo = new String[]{
			"#test-case defines:",
			" #testCaseNum, if use input gennor to gen testcases, here defines how many test cases should be used in press-test",
			" #onlyGenAvsOrRcs, only gen rsc or avs, default not use(rcs and avs are all used and gen)",
			"",

			"#checkRetCode, check retCode or not, default 1",
			"#checkRetMsg, check RetMsg or not, default 1",
			"",
			"#comparator defines:",
			" #useCmp, use comparator for attd test? default 1",
			" #resultComparator, define, default " + com.funshion.gamma.atdd.ResultCompareDefault.class.getName(),
			"",
			"#round model defines:",
			" #roundModel, default 1: use thread num [startNum] -->[1] -->[2] -->[4] -->[8] ...[maxThreadNumber]",
			"  #if roundModel set to 0, will only use [startNum] when test",
			"",
			"#thread number defines:",
			"#maxThreadNumber = 256",
			"#startNum, use how many threads to start, default 1",
			"",
			"#socket connection defines:",
			" #timeoutMs, default 1000 milli-seconds",
			" #shortConnection, if use shortConnection? default 1"
	};
	private void genConfig(Method m, Writer sb) throws IOException{
		String cfgName = m.getName();//this.methodName(m);

		sb.writeLine("#WARN!!! 如果存在同名函数，请修改[cfgName]");
		sb.writeLine("[", cfgName, "]");
		sb.writeLine("inputGennor =  ");
		sb.writeLine("avs-host = ");
		sb.writeLine("avs-port = ");
		sb.writeLine("rcs-host = ");
		sb.writeLine("rcs-port = ");
		sb.writeLine("isForTestPress = 0");
		sb.writeLine("swapThreadsInMinutes = 10");


		sb.writeLine("avs-service-name = ", svcName);
		sb.writeLine("avs-method-name = ", m.getName());
		sb.writeLine("avs-method-paras = ", getparas(m));

		sb.writeLine("rcs-service-name = ", svcName);
		sb.writeLine("rcs-method-name = ", m.getName());
		sb.writeLine("rcs-method-paras = ", getparas(m));

		sb.writeLine("");
	}
	private String getparas(Method m) {
		Class<?> cls[] = m.getParameterTypes();
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < cls.length; x ++){
			SerialType type = SerialType.getType(cls[x]);
			if(sb.length() > 0){
				sb.append(", ");
			}
			sb.append(type.toString(cls[x]));
		}
		return sb.toString();
	}
	@Override
	public void exe() throws Exception{
		File baseDir = mkdirs();
		String cfgFileName = "conf-" + strName() + ".cfg.template";
		File cfgFile = new File(baseDir, cfgFileName);
		Writer writer = new Writer(cfgFile);
		Method [] ms = clsIface.getMethods();

		writer.writeLine("#config template gennor ", VER);
		writer.writeLine("#gen time:", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis()));
		writer.writeLine("");
		for(int idx = 0; idx < ms.length; idx ++){
			genConfig(ms[idx], writer);
		}

		writer.writeLine("\n\n#-------->other parameters<------------");
		for(String x : helpInfo){
			writer.writeLine(x);
		}

		writer.close();
		System.out.println("\n    save to file : " + cfgFile);
	}
	@Override
	public void helpInfo(){
		for(String x : helpInfo){
			System.out.println(x);
		}
	}
	@Override
	protected File mkdirs() {
		File baseDir = new File(getTemplatesGenDir(), strName() + "/" );
		baseDir.mkdirs();
		System.out.println("workDirctory:" + baseDir.getPath());
		return baseDir;
	}
}
