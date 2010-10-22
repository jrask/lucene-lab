
********************
Lucene Core Lab
********************

Introduction - Perhaps say a few words about the most common stuff

Preparations

In IndexTest.java, there is a default setup that creates a single Document
with default configured fields. Make sure that the test pass. Do not touch
the @Ignore yet...


################################
 Introduction - Talk and draw.
################################

Take a look at Index.java. This class encapsulates some functionality like
writing to an index and searching the index. 

[Perhaps a walkthrough of the code]


#####################
 Phase I - Analysis
#####################

1. Go through each testcase in IndexTest.java and make sure that you understand
 what it does and why you get those results. Open CustomAnalyzer.java and have
 a quick look at it.
 Does the code and result make sense?
 How would you expect it to work?
 Discussions?!

2. Change setupIndex() method and change to use whitespace analyzer instead of 
 custom analyzer and run the test again. What happens?!
 Make some changes in the test case to see if you can make it work.
 
 Mark the testcase with @Ignore and remove all other @Ignore and run the
 tests again. 
 Checkout the code. Does it work as expected? How would you like it to work?
 
 Discussions?!
 
3. Change to Standard analyzer 
	
	Run the tests again and try to figure out how to change all searches so 
	that it works with this analyzer.

4. Change the field configuration of id so that the term search for id does not
 return any documents.

5. Field keys should be case sensitive so a search for java should not match
  JaVa. It is important that other fields work as expected.

#################################
 Phase II - Numeric Fields
#################################

3. Index a new field which should be a date. Use a range query to find the document
 with this date. Hint: NumericField

4. Index a new field price with a float value and try to find it with a range query.


###########################
 Boosting fields for relevance
###########################