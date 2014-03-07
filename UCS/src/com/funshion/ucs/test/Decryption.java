package com.funshion.ucs.test;

public class Decryption {

	public static String decrypt(String result){
		result=result.trim();
		String decryptedresult="";
		String tem = "";
		try{			
			if(result.length()==35){
				for(int i = 0; i<result.length()-1;i++){
					if(i==5||i==11||i==17||i==23||i==29){    	
						tem=tem+result.charAt(i);    	
					}
				}
			}        	   
			if(result.length()==36){
				for(int i = 0; i<result.length()-1;i++){
					if(i==5||i==11||i==17||i==23||i==24||i==30){
						tem=tem+result.charAt(i);
					}
				}       	   
			}

			for (int j=0;j<3;j++){
				decryptedresult= decryptedresult+tem.charAt(2-j);
			}

			for (int j=3;j<tem.length();j++){
				decryptedresult= decryptedresult+tem.charAt(j);
			}               

		} catch (Exception e) {  
			e.printStackTrace();  
		}
		return decryptedresult;
	}
}
