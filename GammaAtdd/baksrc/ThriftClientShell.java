package com.funshion.gamma.atdd.serialize.shell;

import com.funshion.gamma.atdd.AtddConfig;
import com.funshion.gamma.atdd.MethodAtddCommonThriftClient;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.gamma.atdd.serialize.ThriftSerializedTool;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.Consoler;
public class ThriftClientShell {
	final MethodAtddCommonThriftClient client;
	ThriftClientShell(ConfigReader cr) throws Exception{
		this.client = new MethodAtddCommonThriftClient(cr, "rcs");
	}

	private void exe() {
		while(true){
			try {
				String cmd = Consoler.readString("~>");
				if(cmd.length() == 0){
					exe(0);
				}else{
					int index = -1;
					try{
						index = Integer.parseInt(cmd);
					}catch(Exception e){}
					if(index < 0 || index >= exes.length){
						System.out.println(" #ErrorInput:" + cmd);
					}else{
						System.out.println(" #" + exes[index].cmd);
						exe(index);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	abstract class Exe{
		final String cmd;
		Exe(String cmd){
			this.cmd = cmd;
		}
		public abstract void exe()throws Exception;
	}
	Exe[]exes = new Exe[]{
			new Exe("help"){

				@Override
				public void exe() throws Exception {
					int index = 0;
					System.out.println("请输入序号：");
					for(Exe e: exes){
						System.out.println(index ++ + " " + e.cmd);
					}

				}

			},
			//test cmd
			new Exe("互动输入测试"){

				@Override
				public void exe() throws Exception {
					QueryParas paras = LineBuffer.readQueryParas();
					System.out.println("#executing.......................................................................");
					Object ret = client.query(paras);
					String serialStr = ThriftSerializedTool.methodReturnValueGen(
							client.method, ret);

					System.out.println("#serialized return value ");
					System.out.println(serialStr);
				}

			},
	};
	void exe(int index) throws Exception{
		exes[index].exe();
	}
	public static void main(String[] args) throws Exception {
		ConfigReader cr = AtddConfig.getConfig(args);
		ThriftClientShell shell = new ThriftClientShell(cr);
		shell.exe();
	}

}
