package com.funshion.updatefs_specailAndfs_auto_recommend;

import java.io.File;

import com.funshion.search.utils.Consoler;
import com.funshion.search.utils.KeyValuePair;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.rds.RDS;

public class FsAutoRecommendRevert {
	public static void main(String[]args) throws Exception{
		String path = Consoler.readString("revert file:");
		LineReader lr = new LineReader(new File(path));
		RDS up = RDS.getRDSByDefine("corsair_0", "update fs_auto_recommend set relate_id = ? where mediaid = ?");
		int count = 0;
		while(lr.hasNext()){
			String next = lr.next();
			KeyValuePair<String, String> pair = KeyValuePair.parseLine(next);
			up.setInt(1, Integer.parseInt(pair.value));
			up.setInt(2, Integer.parseInt(pair.key));
			
			up.execute();
			++count;
			if(count % 1000 == 0){
				System.out.println("has update " + count);
			}
		}
		System.out.println("total update " + count);
		up.close();
		lr.close();
	}
}
