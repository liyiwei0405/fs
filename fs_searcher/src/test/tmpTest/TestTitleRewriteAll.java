package tmpTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.funshion.search.media.chgWatcher.MediaFactory;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixNumFormatResult;
import com.funshion.search.utils.ConfigReader;
import com.funshion.search.utils.LineReader;
import com.funshion.search.utils.LineWriter;

public class TestTitleRewriteAll {
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		ConfigReader cr = new ConfigReader(MediaFactory.mysqlClientConfigFile, "fs_media");
		String driver = "com.mysql.jdbc.Driver";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(cr.getValue("url"), cr.getValue("user"), cr.getValue("password"));
		ResultSet rs = conn.createStatement().executeQuery("select name_cn from fs_media");
		File f = new File("/var/data/rewrited.txt");
		LineWriter lw = new LineWriter(f, false, Charset.forName("utf-8"));
		int x = 0;
		while(rs.next()){
			String line = rs.getString(1);
			MediaTitleSuffixNumFormatResult mr = MediaTitleSuffixNumFormatResult.rewriteTitle(line);
			
			lw.write(line);
			lw.write("\r\n");
			
			
			
			if(mr == null){
				System.out.println(line);
//				System.out.print("\t");
//				System.out.print(mr);
//				System.out.println();
			}else{
//				System.out.print(line);
//				System.out.print("\t");
//				System.out.print(mr);
//				System.out.println();
				lw.write("\t");
				lw.writeLine(mr);
				lw.write("\r\n");
			}
			
//			if(x ++ > 1000)break;
		}
		lw.close();
	}
}
