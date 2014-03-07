package tmpTest;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import com.funshion.search.ConfUtils;
import com.funshion.search.DatumDir;
import com.funshion.search.DatumFile;
import com.funshion.search.media.search.MediaIndexer;

public class TestMediaIndex {

	public static void main(String[]args) throws IOException{
		PropertyConfigurator.configureAndWatch(ConfUtils.getConfFile("log4j.properties").getAbsolutePath());
		File f = new File("testIdx");
		Directory dir = new MMapDirectory(f, null,  1 << 30);

		DatumDir datums = new DatumDir(new File("./chg/totalXmlFile.chg"));
		long st = System.currentTimeMillis();
		MediaIndexer idx = new MediaIndexer(dir);
		DatumFile dfile = new DatumFile(new File("./chg/irec.dir.2013-04-26_13_36_38.458/irec.0.2013-04-26_13_36_38.458"), true);
		idx.index(dfile, false);

		long ed = System.currentTimeMillis();
		System.out.println("index use " + (ed - st));
	}
}
