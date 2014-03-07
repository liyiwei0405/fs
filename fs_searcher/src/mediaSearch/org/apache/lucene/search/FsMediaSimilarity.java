package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

public class FsMediaSimilarity extends Similarity{
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
			return 1;
		}

		@Override
		public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {

			if (end == 1 + start){
				int v = payload.bytes[payload.offset];
				if(v < 0){
					v = (v&0xfe)<<12;
				}
				return v;

			}
			return 1;
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
			final float raw = 1;  // compute tf(f)*weight
			return raw;
		}

	}
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

}
