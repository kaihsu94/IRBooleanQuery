import java.io.*;
import java.io.IOException;
import java.util.*;

public class CSE535Assignment {


    public static void index_term(Map<String, Term_data> index, String line){
       
        String[] line_split = line.split("\\\\");

        Term_data termInfo;
        List<Tuple> postList = new LinkedList<>();

        String term = line_split[0];
        int freq = Integer.parseInt(line_split[1].substring(1));    //sets everything ready to index

        String[] posting_elements = line_split[2].substring(2, line_split[2].length() - 1).split(", ");
        for (String element : posting_elements){
            String[] element_split = element.split("/");
            Tuple posting = new Tuple(Integer.parseInt(element_split[0]), Integer.parseInt(element_split[1]));
            postList.add(posting);
        }

        termInfo = new Term_data(freq, postList);               //indexes

        index.put(term, termInfo);
    }

    

    public static String removeLastChar(String s) {   //helper method to remove last few , "
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-2);
    }


    public static void getTopK(Map<String, Term_data> index, int k){
        String ans = "";
        Set<String> terms = index.keySet();
        Collection<Term_data> values = index.values();

        List<TKTuple> sorted = new LinkedList<>();

        for (Map.Entry<String, Term_data> entry : index.entrySet()){
            int postingSize = entry.getValue().get_Posting_List_size();
            String term = entry.getKey();

            TKTuple topK = new TKTuple(postingSize, term);
            sorted.add(topK);
        }
                                                                                      //goes through and checks for k
        Collections.sort(sorted);
        if (k > index.size()){ k = index.size();}
        for (int i = 0; i < k; i++){
            
            ans += sorted.get(i).getTerm() + ", ";
        }

        System.out.println("FUNCTION: getTopK " + k);
        System.out.println("Results: " + ans);

    }

    public static void getPostings(Map<String, Term_data> index, String queryTerm){
        String orderedID = "";
        String orderedTF = "";
        System.out.println("FUNCTION: getPostings " + queryTerm);
        Term_data results = index.get(queryTerm);
        
        if (results == null){
            System.out.println("term not found");
            return;
        }

        List<Tuple> posted = results.getPosting_list();            // makes two list , one to sort by ID and one to sort by TF
        List<Tuple> TFList = posted;
        Collections.sort(posted, new DocComparator());
        for (Tuple tup : posted){
            orderedID += tup.doc_id + ", ";
        }
       
        System.out.println("Ordered by docID's: " +  removeLastChar(orderedID)); // returns Docids with the last , cut off 

        Collections.sort(TFList);
        for (Tuple tup : TFList){
            orderedTF += tup.doc_id + ", ";
        }
        
        System.out.println("Ordered by TF: " + removeLastChar(orderedTF)); // returns Tf with last , cut off

    }


    public static void termAtATimeAND(Map<String, Term_data> index, String[] queryTerms){
        long lStartTime = System.currentTimeMillis();
        String output =  "";
        String orderedIDs = "";
        int numDocs, comparisons=0;
        List<Integer> resultList = new ArrayList<>();
        int target_score = queryTerms.length;
        Map<Integer, Integer> scoredDocs = new HashMap<>();
        LinkedList ans = new LinkedList<>();
        ArrayList<Integer> medium = new ArrayList<Integer>();
        ArrayList<String> words = new ArrayList<String>();
        
        for (String term : queryTerms){
            output += term + ", ";
        }
        
        
        for (String term : queryTerms){
            words.add(term);
           comparisons ++;
           if (!index.containsKey(term)){
               System.out.println("FUNCTION: TermAtATimeAnd " + removeLastChar(output));
               System.out.println(term + " not found");
               return;
           }
        }                                                                   //adds all words into arraylist of words
            
        String previous = "";
        int innercount = 0;
        Iterator it = words.iterator();
        while (it.hasNext()){                                                   //iterates through words and compares the 
            comparisons++;
            String dis = (String) it.next();                                    //posts of the previous and current words
           
            if (previous != ""){
            comparisons++;
            List<Tuple> comp = index.get(dis).getPosting_list();                
           
            List<Tuple> doc_list = index.get(previous).getPosting_list();
            
            Collections.sort(comp);
            Collections.sort(doc_list);
                for(int i =0; i < comp.size();i++){
                    for(int k = 0 ; k < doc_list.size();k++){
                        if (comp.get(i).doc_id == doc_list.get(k).doc_id) {
                            medium.add(doc_list.get(k).doc_id);
                            comparisons++;
                        }
                    }
                }
            }
            if(innercount == 1){
                comparisons++;
                List<Tuple> first = index.get(previous).getPosting_list();        //adds in first posting list automattically
                for(int i = 0; i < first.size(); i++){
                    medium.add(first.get(i).doc_id);
                }
            }
            innercount ++;
            previous = dis;    
        }
        
        for (int po : medium){
            
        int weight = Collections.frequency(medium,po);                          //checks if they ALL are contained
            if(weight == target_score){
                resultList.add(po);
                
            }
           }
        List<Integer> dedupped = new ArrayList<Integer>(new LinkedHashSet<Integer>(resultList));
        numDocs = dedupped.size();
        
        Collections.sort(dedupped);
        for (int id : dedupped){
            orderedIDs += id + ", ";
        }
        long lEndTime = System.currentTimeMillis();
        long difference = lEndTime - lStartTime;
        System.out.println("FUNCTION: TermAtATimeAnd " + removeLastChar(output));
        System.out.println(numDocs + " documents are found");
        System.out.println("Comparisons Made: " + comparisons);
	    System.out.println(difference + " milliseconds are used");
        System.out.println("Results: " + removeLastChar(orderedIDs));
    }


        public static void termAtATimeOR(Map<String, Term_data> index, String[] queryTerms){
        long lStartTime = System.currentTimeMillis();
        String output =  "";
        String orderedIDs = "";
        int numDocs, comparisons=0;
        List<Integer> resultList = new ArrayList<>();
        int target_score = queryTerms.length;
        Map<Integer, Integer> scoredDocs = new HashMap<>();
        LinkedList ans = new LinkedList<>();
        ArrayList<Integer> medium = new ArrayList<Integer>();
        ArrayList<String> words = new ArrayList<String>();
        
        for (String term : queryTerms){
            output += term + ", ";
        }
        for (String term : queryTerms){
            words.add(term);
           comparisons ++;
            if (!index.containsKey(term)){
               System.out.println("FUNCTION: TermAtATimeAnd " + removeLastChar(output));
               System.out.println(term + " not found");
               return;
           }
        }                                                                   //adds all words into arraylist of words
            
        String previous = "";
        int innercount = 0;
        Iterator it = words.iterator();
        while (it.hasNext()){                                                   //iterates through words and compares the 
            comparisons++;
            String dis = (String) it.next();                                    //posts of the previous and current words
           
            if (previous != ""){
            comparisons++;
            List<Tuple> comp = index.get(dis).getPosting_list();                
           
            List<Tuple> doc_list = index.get(previous).getPosting_list();
            
            Collections.sort(comp);
            Collections.sort(doc_list);
                for(int i =0; i < comp.size();i++){
                    for(int k = 0 ; k < doc_list.size();k++){
                        
                            medium.add(doc_list.get(k).doc_id);
                            comparisons++;
                        
                    }
                }
            }
            if(innercount == 1){
                comparisons++;
                List<Tuple> first = index.get(previous).getPosting_list();        //adds in first posting list automattically
                for(int i = 0; i < first.size(); i++){
                    medium.add(first.get(i).doc_id);
                }
            }
            innercount ++;
            previous = dis;    
        }
        
        for (int po : medium){
            
        int weight = Collections.frequency(medium,po);                          //checks if they ALL are contained
            if(weight > 1){
                resultList.add(po);
                
            }
           }
        List<Integer> dedupped = new ArrayList<Integer>(new LinkedHashSet<Integer>(resultList));
        numDocs = dedupped.size();
        
        Collections.sort(dedupped);
        for (int id : dedupped){
            orderedIDs += id + ", ";
        }
        long lEndTime = System.currentTimeMillis();
        long difference = lEndTime - lStartTime;
        System.out.println("FUNCTION: TermAtATimeOR " + removeLastChar(output));
        System.out.println(numDocs + " documents are found");
        System.out.println("Comparisons Made: " + comparisons);
	    System.out.println(difference + " milliseconds are used");
        System.out.println("Results: " + removeLastChar(orderedIDs));
    }


    public static void docAtATimeAND(Map<String, Term_data> index, String[] queryTerms) {
         String output =  "";
         long lStartTime = System.currentTimeMillis();
         int numDocs= 0;
         String orderedIDs = "";
         List<List<Tuple>> postings = new ArrayList<List<Tuple>>();
         List<LinkedList<Tuple>> p = new ArrayList<LinkedList<Tuple>>();
         for (String term : queryTerms){
            output += term + ", ";
        }
        
         for(int i = 0; i < queryTerms.length; i++){                //sort by docic
                  if (!index.containsKey(index.get(queryTerms[i]).getPosting_list())){
                  System.out.println("term does not exist");
                      return;
                  }
                 List<Tuple> temp = index.get(queryTerms[i]).getPosting_list();
                 Collections.sort(temp, new DocComparator());
                 postings.add(temp);
         }
         
         for(int i = 0; i < postings.size(); i++){ 
               LinkedList<Tuple> temp = new LinkedList<Tuple>();        //make empty 1st entry
              
               temp.addAll(postings.get(i));
               p.add(temp);
         }
         
         //Step 2: Make a list of iterators for each list. 
         
         ArrayList<ListIterator> iters = new ArrayList<ListIterator>();
         
         for(int i = 0; i < p.size(); i++){
             ListIterator<Tuple> temp = p.get(i).listIterator();
             iters.add(temp);
            }
         

        boolean breaker = true;
        
        //Initialize docId array
        ArrayList<Integer> ans = new ArrayList<Integer>();
        
        
     
        ArrayList<Tuple> cur = new ArrayList<Tuple>();
        
       
        for(String s : queryTerms){
            System.out.print(s+ " ");
        }
        System.out.println();
      
        
        for(int i = 0; i < queryTerms.length; i++){                 //enter the indexes
            cur.add((Tuple)(iters.get(i).next()));
           
        }
         
                 
                     
                                                                             //check if each iterator is not null
                     boolean last = false;
                     int comparisons = 0;
                     
                     long t1 = new Date().getTime();
                     
                     while(true){
                       
                         Tuple first = cur.get(0);
                         int first_id = first.doc_id;
                         boolean id_check = true;
                         for(Tuple tup : cur){ 
                             id_check = true;
                             
                            comparisons++;
                             idbreak:
                             if(tup.doc_id != first_id){
                                id_check = false;
                                break idbreak;
                             }
                         }
                       
                         if(id_check){                                   //increment each iterator
                             ans.add(first_id);
                             for(int j = 0; j < cur.size(); j++){ 
                                 if(iters.get(j).hasNext()){
                                    cur.set(j, (Tuple)(iters.get(j).next()));
                                }
                                }
                            
                         }
                         
                         else{
                             
                            ArrayList<Integer> temp2 = new ArrayList<Integer>();
                            for(Tuple tups : cur){ 
                                temp2.add(tups.doc_id);
                            }
                            int max = Collections.max(temp2);
                            for(int j = 0; j < cur.size(); j++){
                                int reset = cur.get(j).doc_id;
                                if(reset != max){   //If the current doc_id isn't equal to max
                                    //System.out.print(current.get(j).doc_id);
                                    if(iters.get(j).hasNext()){
                                        cur.set(j, (Tuple)(iters.get(j).next())); 
                                }
                                //increment the iterator, reset current value
                                    //System.out.println("Reset to: " +current.get(j).doc_id);
                                }
                                
                            }
                            
                            }
                            
                            breaker = true;
                         //Check for break condition
                         for(ListIterator it : iters){
                                //Check to see if each of them is null
                                //if a single one is NOT null, don't break
  
                                
                                if(it.hasNext()){
                                    breaker = false;
                                }
                         
                            }
                          if(last){//after going thru last loop, break
                             break;
                          }
                          if(breaker){
                             last = true; //need to go through one last loop
                           }
             
                    }
                   
               
                   List<Integer> dedupped = new ArrayList<Integer>(new LinkedHashSet<Integer>(ans));
                    
       
                long lEndTime = System.currentTimeMillis();
                long difference = lEndTime - lStartTime;
               
                for(int id : dedupped){
                    orderedIDs += id + ", ";
                }
                numDocs = dedupped.size();
                System.out.println("FUNCTION: DocAtATimeAND " + removeLastChar(output));
                System.out.println(numDocs + " documents are found");
                System.out.println("Comparisons Made: " + comparisons);
                System.out.println(difference + " milliseconds are used");
                System.out.println("Results: " + removeLastChar(orderedIDs));
             
        }
    
        
        
        
    public static void main(String [] args){

        String input = args[0];                                             // sets main args to as formatted inputfile > 
        String outputLog = args[1];                                         // outputlog >> how many topk terms >> termsWant to check
        int topKnumber = Integer.parseInt(args[2]);
        String queryTerms = args[3];

        Map<String, Term_data> index = new HashMap<>();
       
        try{
            PrintStream myconsole = new PrintStream(new File(outputLog));
            System.setOut(myconsole);
            
        }
        catch(FileNotFoundException x){
            System.out.println(x);
        }
            
        try{
            FileReader reader = new FileReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                index_term(index, line);
            }
            bufferedReader.close();
        }
       
        catch (IOException e){
            System.out.println(e.getMessage());
        }
         
       

        getTopK(index, topKnumber);             //index all

       
        try{
        
            FileReader reader = new FileReader(queryTerms);
            BufferedReader bufferedReader = new BufferedReader(reader);                 //reads in inputput file

            String line;

            while ((line = bufferedReader.readLine()) != null) {   // does operation perline
                String[] queryTermsList = line.split(" ");
                for (String term : queryTermsList){
                    getPostings(index, term);
                }
                termAtATimeAND(index, queryTermsList);
                termAtATimeOR(index, queryTermsList);
                docAtATimeAND(index,queryTermsList);                  // everything needed
                
                //docAtATimeOR(index,queryTermsList);
            }
         bufferedReader.close();
        
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        
        
    }
    
}
