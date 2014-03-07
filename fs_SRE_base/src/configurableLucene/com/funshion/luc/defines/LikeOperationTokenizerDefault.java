package com.funshion.luc.defines;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.index.Term;

import com.funshion.search.analyzers.CharacterFormat;
import com.funshion.search.media.search.mediaTitleRewriter.F2JConvert;

/**
 * split words one by one
 * @author liying
 *
 */
public class LikeOperationTokenizerDefault extends Tokenizer{

	protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private int termPos = 0;
	public LikeOperationTokenizerDefault(Reader reader) {
		super(reader);
	}

	@Override
	public void reset() throws IOException {
		termPos = 0;
	}

	@Override
	public final boolean incrementToken() throws IOException {
		clearAttributes();
		while(true){
			int i = super.input.read();
			if(i == -1){
				input.close();
//				System.out.println();
				return false;
			}
			char c = rewrite((char) i);
			if(Character.isLetterOrDigit(c)){
//				System.out.print((char)i);
				termAtt.setEmpty().append(Character.toLowerCase((char)i));
				offsetAtt.setOffset(termPos ,  ++termPos);
				return true;
			}

		}
	}
	
	public static char rewrite(char c){
		c = F2JConvert.instance.convert(c);
		c = CharacterFormat.SBCCaseFilter(c);
		return c;
	}
	public static List<Term>toTerms(String field, String toQuery){
		List<Term> lt = new ArrayList<Term>();
		for(int x = 0; x < toQuery.length(); x ++){
			char c = rewrite(toQuery.charAt(x));
			if(Character.isLetterOrDigit(c)){
				Term t = new Term(field, toQuery.substring(x, x + 1).toLowerCase());
				lt.add(t);
			}
		}
		return lt;
	}
}
