package com.jayway.lucene;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.lucene.index.IndexStore;



public class DiscoverNumbersAndBoostingTest extends AbstractParentTestCase{

	private static final DateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	@Before
	public void setupIndex() throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException, ParseException {
		index = new IndexStore(new RAMDirectory(),standard,standard,"body",debugQueryParser);
	}

	@Test
	@Ignore
	public void testPriceRangeQuery() throws IOException, InterruptedException {

		//TODO - Index documents, use addDocument()
		
		Query q = NumericRangeQuery.newDoubleRange("price",10d,19d, false, false);
		assertEquals(1, index.search(q).length);
		assertEqualsString(q, 0, "price", "10.1");
	}
	
	@Test
	@Ignore
	public void testDateRangeQuery() throws ParseException, IOException, org.apache.lucene.queryParser.ParseException, InterruptedException {
		//TODO - Index documents, use addDocument()
		
		Date from = format.parse("20100101");
		Date to = format.parse("20100103");
		Query q = NumericRangeQuery.newLongRange("date",from.getTime(), 
				to.getTime(), false, false);
		assertEquals(1, index.search(q).length);
	}
	
	/*
	 * 
	 *  BOOOOOOOOOOOOOOSTING
	 * 
	 */
	
	
	//TODO - Explain why the result is ordered the way it is?
	@Test
	public void showSimpleImplicitBoosting() throws CorruptIndexException, IOException, InterruptedException, org.apache.lucene.queryParser.ParseException {
		addDocument(
				defaultConfiguredField("title", "This is a longer sentence with a title"));
		addDocument(
				defaultConfiguredField("title", "A title"));
		
		Document docs[] = index.search("title:title");
		assertEquals(2, docs.length);
		assertEquals("A title", docs[0].get("title"));
	}

	@Test
	@Ignore
	public void showSimpleExplicitDocumentBoosting() {
		//TODO - Copy code in test above and explicit boosting to document so that
		// the sort order is reversed
		Document docs[] = null;
		assertEquals("This is a longer sentence with a title", docs[0].get("title"));
	}
	
	@Test
	@Ignore
	public void showSimpleExplicitFieldBoosting() {
		//TODO - Copy code in test above and explicit boosting to fields so that
		// the sort order is reversed		
		Document docs[] = null;
		assertEquals("This is a longer sentence with a title", docs[0].get("title"));
	}
}
