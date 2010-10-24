package com.jayway.lucene.analysis;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.lucene.ParentTestCase;
import com.jayway.lucene.index.IndexStore;
import com.jayway.lucene.index.IndexUtils;

import static junit.framework.Assert.*;

public class DiscoverNumbersAndBoostingTest extends ParentTestCase{

	private static final DateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	@Before
	public void setupIndex() throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException, ParseException {

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		index = new IndexStore(new RAMDirectory(),analyzer,analyzer,"body");
		index.debugAnalyzerDuringSearch = true;

		//TODO - Index date and price that can be found with a range query
		
		addDocument(
				defaultConfiguredField("id", "1"),
				defaultConfiguredField("date", format.parse("20100102").getTime()),
				defaultConfiguredField("price", 10.1)
				// date and price here
		);
		addDocument(1.5f,
				defaultConfiguredField("id", "2"),
				defaultConfiguredField("date", format.parse("20100104").getTime()),
				defaultConfiguredField("price", 20.5)
				// date and price here
		);
	}

	@Test
	public void testPriceRangeQuery() throws IOException {
		Query q = NumericRangeQuery.newDoubleRange("price",10d,19d, false, false);
		assertEquals(1, index.search(q).length);
	}
	
	@Test
//	@Ignore
	public void testDateRangeQuery() throws ParseException, IOException, org.apache.lucene.queryParser.ParseException {
		Date from = format.parse("20100101");
		Date to = format.parse("20100103");
		Query q = NumericRangeQuery.newLongRange("date",from.getTime(), 
				to.getTime(), false, false);
		assertEquals(1, index.search(q).length);
	}
	
	@Test
	public void testBoosting() {
		
	}
}
