
********************
Lucene Core Lab
********************

################################
 Introduction - Talk and draw.
################################

 - Explain what lucene does
 - Short about Documents and Fields
 - Short about analyzers, tokenizers, indexing, queryParser
 - Where does todays lab fit in indexing and search process
   [Some drawing here]

################################## 
Preparations
##################################

Take a look at IndexStore.java. This class encapsulates some functionality like
writing to an index and searching the index. 


#####################
 Phase I - Analysis
#####################

1.1. Go through each testcase in DiscoverAnalyzersTest.java and make sure that you understand
 what it does and why you get those results. Open CustomAnalyzer.java and have
 a quick look at it.
 [We can go through the code to explain what it does?]
 Does the code and result make sense?
 How would you expect it to work?
 Discussions?!

1.2. Change setupIndex() method and change to use whitespace analyzer instead of 
 custom analyzer and run the test again. What happens?!
 Make some changes in the test case to see if you can make it work.
 
 Mark the testcase with @Ignore and remove all other @Ignore and run the
 tests again. 
 Checkout the code. Does it work as expected? How would you like it to work?
 
 Discussions?!
 
 
1.3. Change back to CustomAnalyzer again and try to figure out how to change
 the implementation of CustomeAnalyzer so that it will work with the current
 test suite.
  
1.4. Change to Standard analyzer 
	
	Run the tests again and try to figure out how to change all searches so 
	that it works with this analyzer.

1.5. Change the field configuration of id so that the term search for id does not
 return any documents.

1.6. Field "keys" should be case sensitive so a search for "java" should not match
  "JaVa". It is important that other fields work as expected. There are a number
  of ways to do this, but one way gives you extra points.

#################################
 Phase II - Numeric Fields
#################################

Open DiscoverNumbersAndBoostingTest.java.
Notice that there is not so much code in this one, it is your job to index documents
so it works. Go through the TODOs in in class, more info below.

 Hint: NumericField, NumericFieldQueryParser...

2.1. Index a new field price with a float value and try to find it with a range query.

2.2. Index a new field which should be a date. Use a range query to find the document
 with this date. 


###########################
 Boosting fields for relevance
###########################