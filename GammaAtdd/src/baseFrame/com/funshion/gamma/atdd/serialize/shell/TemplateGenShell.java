package com.funshion.gamma.atdd.serialize.shell;

import com.funshion.search.utils.Consoler;
public class TemplateGenShell {
	abstract class Cmd{
		final String cmdName;
		Cmd(String cmdName){
			this.cmdName = cmdName;
		}
		
		abstract void exe() throws Exception;
	}
	Cmd[] cmds = new Cmd[]{
			new Cmd("gen config template"){

				@Override
				public void exe() throws Exception {
					String serviceName = Consoler.readString(
							"service name(full class name): ");
					ConfigTemplateGen tg = new ConfigTemplateGen(serviceName);
					tg.exe();
				}

			},
			new Cmd("gen method templates"){

				@Override
				public void exe() throws Exception {
					String serviceName = Consoler.readString(
							"service name(full class name): ");
					ParameterTemplateGen gen = new ParameterTemplateGen(serviceName);
					gen.exe();
				}

			}
	};
	
	private void execute(int x) throws Exception {
		Cmd cmd = cmds[x];
		System.out.println("#" + cmd.cmdName);
		cmd.exe();
	}
	public void run(){
		while(true){
			try {
				String cmd = Consoler.readString("~>");
				execute(cmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void execute(String cmd) throws Exception{
		cmd = cmd.trim().toLowerCase();
		if(cmd.length() == 0){
			System.out.println("#NEED HELP? use cmd 'help'");
			return;
		}
		if(cmd.equalsIgnoreCase("help")){
			int pos = 0;
			for(Cmd c : cmds){
				System.out.println((++ pos) + " " + c.cmdName);
			}
		}else{
			int idx = 0;
			try{
				idx = Integer.parseInt(cmd);
			}catch(Exception e){}
			if(idx < 1 || idx > cmds.length){
				System.out.println("#ERROR CMD:" + cmd);
			}else{
				execute(idx - 1);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		TemplateGenShell shell = new TemplateGenShell();
		shell.execute("help");
		shell.run();
	}
}
