package test;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fragment {

	public static void main(String[] args) throws Exception {
		StringBuilder sb1 = new StringBuilder();
		MysqlHelper my = new MysqlHelper("jdbc:mysql://192.168.8.121:3306/corsair_0", "dbs", "R4XBfuptAH");
		ResultSet rss = my.getCursor("select distinct name_cn, mediaid from fs_publish order by playnum desc limit 2000");
		while(rss.next()){
			sb1.append(rss.getInt("mediaid") + ",");
		}
		rss.close();
		my.close();
		sb1.delete(sb1.length()-1, sb1.length());

		MysqlHelper mysql = new MysqlHelper("jdbc:mysql://192.168.8.121:3306/corsair_0", "dbs", "R4XBfuptAH");
		ResultSet rs = mysql.getCursor("select mediaid, name_cn, name_ot from fs_media where mediaid in (" + sb1.toString() + ")");
		while(rs.next()){
			String namecn = rs.getString("name_cn");
			if(namecn != null && ! namecn.isEmpty() && namecn.length() <= 6){
				namecn = Utils.trimString(namecn.replace(" ", " "));
				StringBuilder sb = new StringBuilder();
				boolean isch = true;
				for(int x = 0; x < namecn.length(); x ++){
					Character c = namecn.charAt(x);
					if(c > 127 && Character.isLetter(c) && Utils.isChinese(c.toString())){
//						sb.append(c);
					}else{
//						if(sb.length() > 0){
//							System.out.println(sb.toString());
//							sb.delete(0, sb.length());
//						}
						isch = false;break;
					}
				}
//				if(sb.length() > 0 && (sb.length() != namecn.length())){
//					System.out.println(sb.toString());
//					sb.delete(0, sb.length());
//				}
				if(isch){
					System.out.println(namecn);
				}
			}
			String nameots = rs.getString("name_ot");
			if(nameots != null && !nameots.isEmpty()){
				List<String> nameOtSet = Utils.splitToList(nameots, " / ");
				for(String name : nameOtSet){
					if(name.length() <= 6){
						name = Utils.trimString(name.replace(" ", " "));
						StringBuilder sb = new StringBuilder();
						boolean isch = true;
						for(int x = 0; x < name.length(); x ++){
							Character c = name.charAt(x);
							if(c > 127 && Character.isLetter(c) && Utils.isChinese(c.toString())){
//								sb.append(c);
							}else{
//								if(sb.length() > 0){
//									System.out.println(sb.toString());
//									sb.delete(0, sb.length());
//								}
								isch = false;break;
							}
						}
//						if(sb.length() > 0 && (sb.length() != name.length())){
//							System.out.println(sb.toString());
//							sb.delete(0, sb.length());
//						}
						if(isch){
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


