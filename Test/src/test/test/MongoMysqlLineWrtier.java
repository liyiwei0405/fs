package test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixNumFormatResult;
import com.funshion.search.utils.LineWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoMysqlLineWrtier {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		//		LineWriter lw = new LineWriter(new File("./staff大于3.txt"), false, Charset.forName("utf-8"));
		//
		//		MongoHelper mongo = new MongoHelper("192.168.8.195", 27022, "corsair_staff", "fs_staff");
		//		DBCursor cur = mongo.col.find(new BasicDBObject("quality", new BasicDBObject("$gte", 3)), new BasicDBObject("name_cn", true).append("_id", false));
		//		while(cur.hasNext()){
		//			DBObject dbobj = cur.next();
		//			String namecn = dbobj.get("name_cn").toString().trim();
		//			//			if(Utils.isChinese(namecn)){
		//			//				lw.writeLine(namecn);
		//			//			}
		//			boolean isch = true;
		//			if(Utils.isChinese(namecn)){
		//				for(int i = 0; i < namecn.length(); i ++){
		//					if(namecn.charAt(i) == '·'){
		//						isch = false;
		//						break;
		//					}
		//				}
		//				if(isch){
		//					System.out.println(namecn);
		//				}
		//			}
		//			//			boolean isBad = false;
		//			//			boolean isCn = false;
		//			//			namecn = namecn.replace(" ", " ");
		//			//			for(int x = 0; x < namecn.length(); x ++){
		//			//				char c = namecn.charAt(x);
		//			//				if(c > 127 && Character.isLetter(c) ){
		//			//					
		//			//				}else{
		//			//					isBad = true;
		//			//				}
		//			//				if(c > 127){
		//			//					isCn = true;
		//			//				}
		//			//			}
		//			//			if(isBad && isCn){
		//			//				System.out.println(namecn);
		//			//			}
		//		}
		//		cur.close();
		//		mongo.close();
		//		lw.flush();
		//		lw.close();

		MysqlHelper mysql = new MysqlHelper("jdbc:mysql://192.168.8.121:3306/corsair_0", "dbs", "R4XBfuptAH");
		ResultSet rs = mysql.getCursor("select name_cn, name_ot from fs_media");
		while(rs.next()){
			String namecn = rs.getString("name_cn");
			if(namecn != null && ! namecn.isEmpty() && namecn.length() <= 6){
				boolean isBad = false;
				boolean isCn = false;
				boolean pass = false;
				namecn = Utils.trimString(namecn.replace(" ", " "));
				for(int x = 0; x < namecn.length(); x ++){
					Character c = namecn.charAt(x);
					if(c > 127){//有中文
						isCn = true;
					}
					if(c > 127 && Character.isLetter(c)){//中文或韩文
						if(! Utils.isChinese(c.toString())){//韩文
							pass = true;
							break;
						}
					}else{//有其他字符
						isBad = true;
					}
				}
				if(!pass && isCn && isBad){
					MediaTitleSuffixNumFormatResult m = MediaTitleSuffixNumFormatResult.rewriteTitle(namecn);
					if(m == null)
						System.out.println(namecn);
				}
			}
			String nameots = rs.getString("name_ot");
			if(nameots != null && !nameots.isEmpty()){
				List<String> nameOtSet = Utils.splitToList(nameots, " / ");
				for(String name : nameOtSet){
					if(name.length() <= 6){
						boolean isBad = false;
						boolean isCn = false;
						boolean pass = false;
						name = Utils.trimString(name.replace(" ", " "));
						for(int x = 0; x < name.length(); x ++){
							Character c = name.charAt(x);
							if(c > 127){//有中文
								isCn = true;
							}
							if(c > 127 && Character.isLetter(c)){//中文或韩文
								if(! Utils.isChinese(c.toString())){//韩文
									pass = true;
									break;
								}
							}else{//有其他字符
								isBad = true;
							}
						}
						if(!pass && isCn && isBad){
							MediaTitleSuffixNumFormatResult m = MediaTitleSuffixNumFormatResult.rewriteTitle(name);
							if(m == null)
								System.out.println(name);
						}
					}
				}
			}
		}
		rs.close();
		mysql.close();
	}

}
