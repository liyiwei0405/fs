package test;

import java.io.File;
import java.nio.charset.Charset;

import com.funshion.search.utils.LineWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class VideoFilepathExport {

	public static void main(String[] args) throws Exception {
		MongoHelper helper = new MongoHelper("192.167.3.95", 27077, "corsair_video", "fs_video");

		LineWriter mp4Lw = new LineWriter(new File("./mp4s.txt"), false, Charset.forName("utf-8"));
		int mp4Count = 0;
		LineWriter htmLw = new LineWriter(new File("./htms.txt"), false, Charset.forName("utf-8"));
		int htmCount = 0;

		DBCursor cur = helper.col.find(new BasicDBObject("filepath", new BasicDBObject("$exists", true).append("$ne", "")).append("publishflag", "published"), new BasicDBObject("videoid", true).append("title", true).append("filepath", true));

		while(cur.hasNext()){
			DBObject dbObject = cur.next();

			Object oFilepath = dbObject.get("filepath");
			int videoid = (int)Double.parseDouble(dbObject.get("videoid").toString());
			String title = String.valueOf(dbObject.get("title"));
			if(oFilepath != null){
				String sFilepath = oFilepath.toString();
				if(sFilepath.endsWith(".mp4")){
					mp4Lw.write(videoid + "\t" + title + "\t" + sFilepath + "\n");
					mp4Count ++;
				}else{
					htmLw.write(videoid + "\t" + title + "\t" + sFilepath + "\n");
					htmCount ++;
				}
			}
		}
		mp4Lw.close();
		htmLw.close();
		helper.close();
		
		System.out.println("mp4Ids count: " + mp4Count);
		System.out.println("htmIds count: " + htmCount);
	}
}
