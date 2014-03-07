package test;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.Similarity.ExactSimScorer;
import org.apache.lucene.search.similarities.Similarity.SimWeight;
import org.apache.lucene.search.similarities.Similarity.SloppySimScorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;

import com.funshion.search.analyzers.StrictTokenizer;
import com.funshion.search.segment.RMMSegment;
import com.funshion.search.segment.TokenHandler;
import com.funshion.search.utils.Consoler;
import com.funshion.search.videolet.search.VideoletIndexer;

public class TestSearcher {
	static class SloppySimScorerImpl extends SloppySimScorer{
		SimWeight weight;
		AtomicReaderContext context;
		public SloppySimScorerImpl(SimWeight weight, AtomicReaderContext context) {
			this.weight = weight;
			this.context = context;
		}

		@Override
		public float score(int doc, float freq) {
			return 1;
		}

		@Override
		public float computeSlopFactor(int distance) {
			return distance;
		}

		@Override
		public float computePayloadFactor(int doc, int start, int end,
				BytesRef payload) {
			return 0;
		}

	}
	static class SimWeightImpl extends SimWeight{
		float topLevelBoost = 1;
		SimWeightImpl(float boost){
			this.topLevelBoost = boost;
		}
		@Override
		public float getValueForNormalization() {
			return topLevelBoost;
		}

		@Override
		public void normalize(float queryNorm, float topLevelBoost) {
			this.topLevelBoost *= topLevelBoost;
		}
	}
	class ExactSimScorerImpl extends ExactSimScorer{
		SimWeight weight;
		AtomicReaderContext context;
		public ExactSimScorerImpl(SimWeight weight, AtomicReaderContext context) {
			this.weight = weight;
			this.context = context;
		}

		@Override
		public float score(int doc, int freq) {
			final float raw = freq * weight.getValueForNormalization();  // compute tf(f)*weight
			return raw;
			//		      return context == null ? raw : raw * decodeNormValue((byte)norms.get(doc)); // normalize for field
		}

	}
	final IndexSearcher sIns, sIns2;
	public TestSearcher() throws IOException{
		Similarity sim = new Similarity(){
			public float queryNorm(float valueForNormalization) {
				return valueForNormalization;
			}
			@Override
			public long computeNorm(FieldInvertState state) {
				return 1;
			}

			@Override
			public SimWeight computeWeight(float queryBoost,
					CollectionStatistics collectionStats,
					TermStatistics... termStats) {
				return new SimWeightImpl(queryBoost);
			}

			@Override
			public ExactSimScorer exactSimScorer(SimWeight weight,
					AtomicReaderContext context) throws IOException {
				return new ExactSimScorerImpl(weight, context);
			}

			@Override
			public SloppySimScorer sloppySimScorer(SimWeight weight,
					AtomicReaderContext context) throws IOException {
				return new SloppySimScorerImpl(weight, context);
			}

		};
		//TODO
		Directory dir = new MMapDirectory(new File("testIdx1"));
		IndexReader ir = DirectoryReader.open(dir);
//		ExecutorService exe = Executors.newFixedThreadPool(10);
		sIns = new IndexSearcher(ir);

		sIns.setSimilarity(sim);
		
		sIns2 = new IndexSearcher(ir);
	}
	protected void searchStrict(String toSearch) throws IOException{
		long st = System.currentTimeMillis();
		toSearch = StrictTokenizer.rewriteString(toSearch);
		TermQuery tqTitle = new TermQuery(new Term(VideoletIndexer.INDEX_TITLE_STRICT, toSearch));
		tqTitle.setBoost(20);

		TermQuery tqTags = new TermQuery(new Term(VideoletIndexer.INDEX_TAGS_STRICT, toSearch));
		BooleanQuery bq = new BooleanQuery();
		bq.add(tqTitle, Occur.SHOULD);
		bq.add(tqTags, Occur.SHOULD);
		long st2 = System.currentTimeMillis();
		TopDocs tdoc = this.sIns.search(bq, 20);

		long ed = System.currentTimeMillis();
		System.out.println("query:" + bq);
		System.out.println("Ms:" + (ed - st) + "(" + (ed - st2) + "), \t" + tdoc.totalHits);
		int end = Math.min(20, tdoc.totalHits);
		for(int x = 0; x < end; x ++){
			System.out.println("\t" + tdoc.scoreDocs[x]);
		}
	}

	protected void searchMay(String toSearch) throws IOException{
		long st = System.currentTimeMillis();
		TokenHandler handler = RMMSegment.instance.segment(toSearch);

		BooleanQuery bq = new BooleanQuery();
		while(handler.hasNext()){
			String str = handler.next().trim().toLowerCase();
			if(str.length() == 0){
				continue;
			}
			TermQuery tqTitle = new TermQuery(new Term(VideoletIndexer.INDEX_TITLE, str));
			tqTitle.setBoost(20);

			TermQuery tqTags = new TermQuery(new Term(VideoletIndexer.INDEX_TAGS, str));
			bq.add(tqTitle, Occur.SHOULD);
			bq.add(tqTags, Occur.SHOULD);
		}
		long st2 = System.currentTimeMillis();
		TopDocs tdoc = this.sIns2.search(bq, 20);

		long ed = System.currentTimeMillis();
		System.out.println("query:" + bq);
		System.out.println("Ms:" + (ed - st) + "(" + (ed - st2) + "), \t" + tdoc.totalHits);
		int end = Math.min(20, tdoc.totalHits);
		for(int x = 0; x < end; x ++){
			System.out.println("\t" + tdoc.scoreDocs[x]);
		}
	}

	protected void searchAnd(String toSearch) throws IOException{
		long st = System.currentTimeMillis();
		TokenHandler handler = RMMSegment.instance.segment(toSearch);

		BooleanQuery bq = new BooleanQuery();
		while(handler.hasNext()){
			String str = handler.next().trim().toLowerCase();
			if(str.length() == 0){
				continue;
			}
			TermQuery tqTitle = new TermQuery(new Term(VideoletIndexer.INDEX_TITLE, str));
			tqTitle.setBoost(20);

			TermQuery tqTags = new TermQuery(new Term(VideoletIndexer.INDEX_TAGS, str));
			BooleanQuery bq2 = new BooleanQuery();
			bq2.add(tqTitle, Occur.SHOULD);
			bq2.add(tqTags, Occur.SHOULD);
			
			bq.add(bq2, Occur.MUST);
		}
		long st2 = System.currentTimeMillis();
		TopDocs tdoc = this.sIns2.search(bq, 20);

		long ed = System.currentTimeMillis();
		System.out.println("query:" + bq);
		System.out.println("Ms:" + (ed - st) + "(" + (ed - st2) + "), \t" + tdoc.totalHits);
		int end = Math.min(20, tdoc.totalHits);
		for(int x = 0; x < end; x ++){
			System.out.println("\t" + tdoc.scoreDocs[x]);
		}
	}
	public static void main(String[]args) throws IOException{
		PropertyConfigurator.configureAndWatch("./config/log4j.properties");
		TestSearcher s = new TestSearcher();
		while(true){
			String line = Consoler.readString(":");
			s.searchStrict(line);
			
			s.searchAnd(line);
			s.searchMay(line);
		}

	}
}
