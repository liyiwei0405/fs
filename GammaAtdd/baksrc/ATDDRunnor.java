package com.funshion.gamma.atdd;

import com.funshion.search.utils.Consoler;

public class ATDDRunnor {

	public static void main(String[]args) throws Exception{
		String sec = null;
		if(args.length == 0){
			sec = Consoler.readString("section:");
		}else if(args.length == 1){
			sec = args[0];
		}
		if(sec == null || sec.length() == 0){
			System.out.println("usage: configSection");
			System.out.println("if not set args for configSection, input it by consoler");
			System.exit(0);
		}
		AtddConfig cfg = new AtddConfig(sec);

		cfg.runConfig();
	}


}
