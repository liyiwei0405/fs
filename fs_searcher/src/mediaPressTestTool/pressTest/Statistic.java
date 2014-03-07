package pressTest;

public class Statistic {
	Statistic(String word){
		this.word = word;
	}
	String word;
	int resultCnt;
	double searchUsed;
	double totUsed;
	boolean hasError;

	long v12, v23, v34, v45, v56, v67, v78;
	boolean serverError;

	public String toString(){
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", 
				word.replace('\t', ' '), this.resultCnt, this.searchUsed, this.totUsed, this.hasError,
				this.v12, this.v23, this.v34, this.v45, this.v56, this.v67, this.v78);
	}
}
