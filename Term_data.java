import java.util.List;

public class Term_data{

    int list_size;
    List<Tuple> posting_list;

    public Term_data(int list_size, List<Tuple> posting_list){
        this.list_size = list_size;
        this.posting_list = posting_list;
    }

    public int get_Posting_List_size(){
        return list_size;
    }

    public List<Tuple> getPosting_list(){
        return posting_list;
    }

}