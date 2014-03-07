package tmpTest;

import java.io.File;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import com.funshion.search.media.search.FieldDefine;
import com.funshion.search.media.search.MediaSearcher;
import com.funshion.search.media.thrift.FieldFilter;
import com.funshion.search.media.thrift.FullTextQuery;
import com.funshion.search.media.thrift.QueryStruct;
import com.funshion.search.utils.Consoler;

public class TestMediaSearch {
	public static void main(String[]args) throws Exception{
		Directory dir = MMapDirectory.open(new File("./testIdx"));
		IndexReader lr = DirectoryReader.open(dir);

		MediaSearcher ms = new MediaSearcher(lr);
		while(true){
			String toSearch = Consoler.readString(":");

			QueryStruct qs = new QueryStruct();
			{
				FieldFilter flt = new FieldFilter(FieldDefine.FIELD_NAME_CAN_DISPLAY, 1, false);
//				qs.addToFilters(flt);
			}
			qs.word = new FullTextQuery(toSearch, FieldDefine.FIELD_NAME_NAMES, 0);
			TopDocs td = ms.searchMay(qs, 20);
			ScoreDoc[] sds = td.scoreDocs;
			for(int x = 0; x < td.totalHits && x < 20; x ++){
				ScoreDoc d = sds[x];
				System.out.println("-----------------------------------------------------");
				System.out.println(d.doc + "\t" + d.score + "\t" + lr.document(d.doc));
			}
			System.out.println("match:" + td.totalHits);
		}
	}
}
