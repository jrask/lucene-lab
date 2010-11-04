package com.jayway.lucene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.lucene.index.IndexStore;

public class DiscoverQueryTest extends AbstractParentTestCase {

	public void setupIndex(Analyzer analyzer) throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {
		index = new IndexStore(new RAMDirectory(),analyzer,analyzer,"body",debugQueryParser);
	}
	
	@Before
	public void setup() throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {
		setupIndex(standard);
		addDocument(
				defaultConfiguredField("id", "1"),
				defaultConfiguredField("title", "The art of Adventure racing from wikipedia"),
				defaultConfiguredField("author", "johan.rask@jayway.com"));
	}

	@Test
	@Ignore
	public void termQuery()  {
		//TODO - Write a TermQuery that returns a hit for id
		TermQuery query = null;
		assertEquals(1, countResults(query));
	}
	
	@Test
	@Ignore
	public void termRangeQuery() throws CorruptIndexException, IOException, InterruptedException {
		addDocument(defaultConfiguredField("name", "Anna"));
		addDocument(defaultConfiguredField("name", "Britta"));
		addDocument(defaultConfiguredField("name", "Cissi"));
		addDocument(defaultConfiguredField("name", "David"));
		addDocument(defaultConfiguredField("name", "Davidsson"));
		addDocument(defaultConfiguredField("name", "…rjan"));
		
		// TODO - Construct this query so the asserts work
		TermRangeQuery query = null; 
		assertEquals(2, countResults(query));
		assertEqualsString(query,0, "name", "Anna");
		assertEqualsString(query,1, "name", "Britta");
		
		//TODO-  Reconstruct this query so the asserts work
		query = null;
		assertEquals(4, countResults(query));
		assertEqualsString(query,0, "name", "Cissi");
		assertEqualsString(query,3, "name", "…rjan");
	}
	
	@Test
	@Ignore
	public void prefixQuery() {
		// TODO - Construct this query so the asserts work
		PrefixQuery query = null;
		assertEquals(1, countResults(query));
		assertEqualsString(query,0, "author", "johan.rask@jayway.com");
		// TODO - Construct this query so the asserts work
		query = null;
		assertEquals(1, countResults(query));
		assertEqualsString(query,0, "title", "The art of Adventure racing from wikipedia");
	}
	  
	@Test
	@Ignore
	public void booleanQuery() {
		addDocument(
				defaultConfiguredField("id", "2"),
				defaultConfiguredField("title", "The art of sleeping"),
				defaultConfiguredField("author", "johan.rask@jayway.com"));
		TermQuery termQuery = new TermQuery(new Term("title","art"));
		TermQuery termQuery2 = new TermQuery(new Term("title","sleeping"));
		PrefixQuery termQuery3 = new PrefixQuery(new Term("author","johan"));
		BooleanQuery query = new BooleanQuery();
		
		// TODO - Combine all these queries with query so the asserts are correct
		
		assertCountResults(1, query);
		assertEqualsString(query, 0, "id", "1");
	}
	
	/**
	 * Phrase queries are a mess when not using a QueryParser.
	 * Try to understand what happens here...
	 */
	@Test
	public void phraseQuery() {
		PhraseQuery query = new PhraseQuery();
		query.setSlop(1);
		for(String str : "racing wikipedia".split(" ")) {
			query.add(new Term("title",str));
		}
		System.out.println(query);
		assertCountResults(1, query);
	}
	
	@Test
	@Ignore
	public void wildcardQuery() {
		addDocument(defaultConfiguredField("text", "hoppsan"));
		addDocument(defaultConfiguredField("text", "hejsan"));
		addDocument(defaultConfiguredField("text", "hoppa"));
		addDocument(defaultConfiguredField("text", "heja"));
		addDocument(defaultConfiguredField("text", "loppa"));
		
		// TODO - Construct this query so the asserts work
		WildcardQuery query = null;
		assertCountResults(3, query);
		assertEqualsString(query, 0, "text", "hoppsan");
		assertEqualsString(query, 1, "text", "hoppa");
		assertEqualsString(query, 2, "text", "loppa");
		
		query = null;
		assertCountResults(2, query);
		assertEqualsString(query, 0, "text", "hoppsan");
		assertEqualsString(query, 1, "text", "hejsan");
	}
	
	@Test
	@Ignore
	public void fuzzyQuery() {
		// TODO - Construct this query so the asserts work
		// Ok, this is simple but at least try it with a few
		// different values
		FuzzyQuery query = null;
		assertCountResults(1, query);
	}
	
}
