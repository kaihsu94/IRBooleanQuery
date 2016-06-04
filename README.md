# IRBooleanQuery
Boolean Query Processing based on Postings Lists


Constructs two indexes based on two seperate ordering schemes; Document-At-A-Time And/Or ordering and Term-At-A-Time And/Or ordering. 
Was used to order the RCV1 news corpus. (http://www.daviddlewis.com/resources/testcollections/rcv1/). Has been ordered with both
"increasing document id" and "decreasing term frequencies" (from Information Retrevial Theory). Data structures used were primarly 
Linked-Lists and HashMaps. Written from stratch for Information-retrevial CSE535 class from SUNY-Buffalo.
