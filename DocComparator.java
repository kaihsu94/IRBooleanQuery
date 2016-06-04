import java.util.Comparator;

public class DocComparator implements Comparator<Tuple>{

   @Override
   public int compare(Tuple t1, Tuple t2) {
       return t1.doc_id - t2.doc_id;
    }

}
//List<Tuple> t1 = index.get(query_terms[0]).getPosting_list();
// Collections.sort(t1, new DocComparator());