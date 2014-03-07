package org.apache.lucene.search;

import java.io.IOException;

public class FSMediaScore extends Scorer{
	public static final int minCheck = 1 << 12;
	Scorer org;
	protected FSMediaScore(Scorer score) {
		super(score.weight);
		this.org = score;
	}

	@Override
	public float score() throws IOException {
		float ret = org.score();
		if(ret > minCheck){
			return (((int)ret) & 0x8ffff000);
		}
		return ret;
	}

	@Override
	public int freq() throws IOException {
		return org.freq();
	}

	@Override
	public int docID() {
		return org.docID();
	}

	@Override
	public int nextDoc() throws IOException {
		return org.nextDoc();
	}

	@Override
	public int advance(int target) throws IOException {
		return org.advance(target);
	}
}
