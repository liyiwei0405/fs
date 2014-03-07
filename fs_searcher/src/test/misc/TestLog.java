package misc;

import com.funshion.search.utils.LogHelper;

public class TestLog {

	public static void main(String[]args){
		for(int x = 0;;x ++){
			LogHelper.log.log("%s idonot now %s", x, "sdfas" + x);
		}
	}
}
