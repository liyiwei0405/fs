package com.funshion.gamma.atdd.serialize.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.funshion.gamma.atdd.AtddConfig;
import com.funshion.gamma.atdd.CompareTool;
import com.funshion.gamma.atdd.GammaTestCase;
import com.funshion.gamma.atdd.MethodAtddCommonThriftClient;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.gamma.atdd.CommonThriftClientInfo.ClientInfo;
import com.funshion.gamma.atdd.QueryReturnValue;
import com.funshion.gamma.atdd.ResultCompareDefault;
import com.funshion.gamma.atdd.serialize.ThriftSerializeTool;
import com.funshion.search.utils.Charsets;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LineWriter;
import com.funshion.search.utils.Misc;

/*
 * 交互式shell对,某个method输入测试用例，得到输出结果，然后可以保存此用例(input expect对)
 */
public class ThriftClientShell {
	class LastOne{
		final GammaTestCase testCase;
		LastOne(GammaTestCase testCase){
			this.testCase = testCase;
		}
		List<File>hasSaveToDir = new ArrayList<File>();
	}
	final MethodAtddCommonThriftClient client;

	LastOne lastOne;
	Exe[]exes;	//存储各Exe
	public ThriftClientShell(ConfigReader cr) throws Exception{
		this(new MethodAtddCommonThriftClient(cr, "rcs"));

	}
	public ThriftClientShell(MethodAtddCommonThriftClient client) throws Exception{
		this.client = client;

		this.loadExes();
	}

	//循环输入cmd执行，若执行testClient直接输入input即可
	public void exe() {
		HashSet<String>set = new HashSet<String>();
		for(Exe e : exes){
			set.add(e.cmd.trim().toLowerCase());
		}
		while(true){
			System.out.println("# 请输入命令 :");
			List<String> readed;
			try {
				readed = LineBuffer.read(set);
			} catch (Exception e1) {
				e1.printStackTrace();
				continue;
			}
			Exe cmd = getExe(readed);
			if(cmd != null){
				try {
					cmd.exe(readed);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	abstract class Exe{
		final String cmd;
		Exe(String cmd){
			this.cmd = cmd;
		}
		public abstract void exe(List<String>lst)throws Exception;
	}
	void loadExes(){
		exes = new Exe[]{
				new Exe("testClient"){
					@Override
					public void exe(List<String>lst) throws Exception {
						System.out.println("#      以'#回车'结束输入:     ");
						QueryParas paras;
						try{
							//反序列化输入
							paras = QueryParas.fromStringList(lst);
						}catch(Exception e){
							System.out.println("错误： 无法解析输入的thrift结构体！ " + e);
							return;
						}
						System.out.println("#executing.......................................................................");
						ClientInfo ci = null;
						long st = 0, st1 = 0, ed = 0, ed1 = 0;
						try{
							st = System.currentTimeMillis();
							client.getNewClient(null);
							st1 = System.currentTimeMillis();
							Object ret = client.query(paras, ci);
							ed = System.currentTimeMillis();
							//序列化返回结果
							String serialStr = ThriftSerializeTool.methodReturnValueGen(
									client.method, ret);
							ed1 = System.currentTimeMillis();
							System.out.println(serialStr);
							//FIXME maybe we should load from disk
							lastOne = new LastOne(new GammaTestCase(paras, ret, null));
						}catch(java.lang.IllegalArgumentException e){
							System.out.println("参数错误！ 可能输入的不是指定的参数类型：" + e);
						}finally{
							if(ci != null){
								ci.close();
							}
							System.out.println("connect use " + (st1 - st) + " ms, total use " + (ed - st) + " ms, serial use time " + (ed1 - ed));
						}


					}
				},
				new Exe("help"){

					@Override
					public void exe(List<String>lst) throws Exception {
						for(Exe e: exes){
							System.out.println(" " + e.cmd);
						}

					}

				},
				new Exe("save"){

					File saveDir;
					boolean inited = false;
					void init() throws Exception{
						if(inited)
							return;

						String innerOrOuter = "";
						Integer input = Consoler.readInt("内网(0) 公网(1):", 0);
						if(input == 1){
							innerOrOuter = "OuterTest";
						}else{
							innerOrOuter = "InnerTest";
						}
						File fpar = new File("./savedTestCases/" + innerOrOuter, client.serviceName);
						//						fpar = new File(fpar, ThriftSerializeTool.methodStringName(client.method));
						saveDir = new File(fpar, ThriftSerializeTool.methodStringName(client.method));
						if(!saveDir.exists()){
							saveDir.mkdirs();
						}
						if(!saveDir.exists()){
							throw new Exception("can not create save dir " + saveDir);
						}
						if(!saveDir.canWrite() || !saveDir.canExecute() || !saveDir.canRead()){
							throw new Exception("no sufficient privileges for dir " + saveDir);
						}
						inited = true;
					}

					@Override
					public void exe(List<String>lst) throws Exception {
						init();

						if(lastOne == null){
							System.out.println("#Error: no sucessfully executed testcase, has alreay save?");
							return;
						}
						String name ;
						File f;
						List<String>buf;
						while(true){
							name = Consoler.readString("名字:");
							name = Misc.formatFileName(name).trim();
							if(name.length() == 0){
								continue;
							}

							f = new File(saveDir, name + ".testcase");
							if(f.exists()){
								System.out.println("#file already exists! " + f);
							}else{
								f.mkdirs();
								break;
							}
						}
						System.out.println(" 请输入注释，以 换行+#+换行 结束");
						buf = LineBuffer.read(null);
						LineWriter lw;

						lw = new LineWriter(new File(f, "note.txt"), false, Charsets.UTF8_CS);
						for(String x : buf){
							lw.writeLine(x);
						}
						lw.close();

						lw = new LineWriter(new File(f, "input.txt"), false, Charsets.UTF8_CS);
						String serialStr = ThriftSerializeTool.methodParameterValueGen(
								client.method, lastOne.testCase.queryParas());
						lw.writeLine(serialStr);
						lw.write("##");
						lw.close();

						String igrFields = Consoler.readString("请输入返回结果中要忽略的字段，以空格分隔(all为所有)：");
						List<String> igrList = Arrays.asList(igrFields.split(" ")); 

						File notused = new File(f, "notused.txt");
						lw = new LineWriter(notused, false, Charsets.UTF8_CS);
						serialStr = ThriftSerializeTool.methodReturnValueGen(
								client.method, lastOne.testCase.expectResult());
						lw.writeLine(serialStr);
						lw.close();
						//处理需要@Igr的字段
						StringBuilder sb = new StringBuilder();
						LineReader lr = new LineReader(notused, "utf-8");
						String line;
						String end = Misc.isWin() ? "\r\n" : "\n";
						boolean allIgr = false;
						if(igrList.size() == 1 && igrList.get(0).trim().equals("all")){
							allIgr = true;
						}
						while((line = lr.readLine()) != null){
							if(allIgr){
								if(line.indexOf(":") != -1){
									line = line.substring(0, line.indexOf(":") + 1) + " @Igr";
								}
							}else{
								for(String igr : igrList){
									igr = igr.trim();
									if(! igr.isEmpty()){
										if(line.contains(igr)){
											line = line.substring(0, line.indexOf(igr) + igr.length() + 1) + " @Igr";
											break;
										}
									}
								}
							}
							sb.append(line + end);
						}
						lr.close();
						notused.delete();
						lw = new LineWriter(new File(f, "expect.txt"), false, Charsets.UTF8_CS);
						lw.writeLine(sb.toString());
						lw.write("##");
						lw.close();

						System.out.println("after ignore:\n" + sb.toString());
						System.out.println("test case save to " + f);
						lastOne.hasSaveToDir.add(f);
					}
				},
				new Exe("cmp"){

					@Override
					public void exe(List<String> lst) throws Exception {
						if(lastOne == null){
							System.out.println("#Error: no sucessfully executed testcase!");
							return;
						}
						System.out.println("------->" + "请输入要比较的对象" + "<-------");
						List<String>readed = LineBuffer.read(null);
						QueryReturnValue retValue = QueryReturnValue.fromStringList(readed);
						try{
							ResultCompareDefault dft = new ResultCompareDefault();
							dft.compare(lastOne.testCase.queryParas(), 
									lastOne.testCase.expectResult(), retValue.expect, retValue.cmpMask);
							System.out.println("compare equals!");
						}catch(Exception e){
							System.out.println("compare fail: " + e);
						}

						CompareTool.compare(lastOne.testCase.queryParas(), retValue.expect, lastOne.testCase.expectResult());

					}

				}
		};
	}
	//根据Exe名称获取Exe，默认为testClient
	private Exe getExe(List<String> readed) {
		//findFirstLine(readed);
		String line = null;
		for(String x : readed){
			x = x.trim();

			if(x.length() == 0){
				continue;
			}else{
				line = x;
				break;
			}
		}
		if(line == null){
			return null;
		}
		for(int x = 1; x < exes.length; x ++){
			if(line.equalsIgnoreCase(exes[x].cmd)){
				return exes[x];
			}
		}
		return exes[0];
	}

	public static void main(String[] args) throws Exception {
		ConfigReader cr = AtddConfig.getConfig(args);
		ThriftClientShell shell = new ThriftClientShell(cr);
		shell.exe();
	}

}
