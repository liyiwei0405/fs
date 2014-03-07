package tmpTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.funshion.search.media.search.mediaTitleRewriter.CnNumRewrite;
import com.funshion.search.media.search.mediaTitleRewriter.MediaTitleSuffixNumFormatResult;
import com.funshion.search.utils.LineReader;

public class TestTitleRewrite {
	public static void main(String[] args) throws IOException {
		File f = new File("/var/data/第.txt");
		LineReader lr = new LineReader(f, Charset.forName("utf-8"));
		int x = 0;
		while(lr.hasNext()){
			String line = lr.next().trim();
			String newLine = CnNumRewrite.cnNumTryRewrite(line);
			if(newLine != null){
				line = newLine;
			}
			MediaTitleSuffixNumFormatResult mr = MediaTitleSuffixNumFormatResult.rewriteTitle(line);
			
			if(mr == null){
//				System.out.println(line);
//				System.out.print("\t");
//				System.out.print(mr);
//				System.out.println();
			}else{
				System.out.print(line);
				System.out.print("\t");
				System.out.print(mr);
				System.out.println();
			}
			
			if(x ++ > 1000)break;
		}
		lr.close();
	}
}
