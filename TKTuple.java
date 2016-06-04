
public class TKTuple implements Comparable<TKTuple>{

    int postingSize;
    String term;

    public TKTuple(int postingSize, String term){
        this.postingSize = postingSize;
        this.term = term;
    }

    public int getPostingSize(){
        return postingSize;
    }

    public String getTerm(){
        return term;
    }

    @Override
    public int compareTo(TKTuple other_tup) {
        return other_tup.getPostingSize() - this.getPostingSize();
    }
}
