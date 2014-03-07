package com.funshion.gamma.atdd.healthWatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.funshion.gamma.atdd.GammaTestCase;
import com.funshion.gamma.atdd.QueryParas;
import com.funshion.gamma.atdd.QueryReturnValue;
import com.funshion.search.utils.Md5;

public class NamedGammaTestCase extends GammaTestCase{
	public final String md5Flag;
	public final String name;
	public NamedGammaTestCase(String name, String md5Flag, QueryParas paras, QueryReturnValue retValue){
		super(paras, retValue);
		this.name = name;
		this.md5Flag = md5Flag;
	}

	@Override
	public int hashCode(){
		return name.hashCode();
	}
	@Override
	public boolean equals(Object o){
		if(!(o instanceof NamedGammaTestCase)){
			return false;
		}
		NamedGammaTestCase other = (NamedGammaTestCase) o;
		if(name.equals(other.name)){
			if(md5Flag.equals(other.md5Flag)){
				return true;
			}
		}
		return false;
	}
	
	public static String md5Flag(String txt4input, String txt4return) throws IOException{
		txt4input = txt4input.trim();
		txt4return = txt4return.trim();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(txt4input.getBytes());
		bos.write(txt4return.getBytes());

		return Md5.md5(bos.toByteArray());
	}
	
	@Override
	public String toString(){
		StringBuilder bs = new StringBuilder();
		bs.append("#testCase name: '" + name);
		bs.append("'\n");
		
		bs.append("#testCase md5: '" + this.md5Flag);
		bs.append("'\n");
		bs.append(super.toString());
		
		return bs.toString();
	}
}
