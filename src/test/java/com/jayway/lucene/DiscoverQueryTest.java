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
import org.junit.Test;

import com.jayway.lucene.index.IndexStore;

public class DiscoverQueryTest extends AbstractParentTestCase {

	public void setupIndex(Analyzer analyzer) throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {
		index = new IndexStore(new RAMDirectory(),analyzer,analyzer,"body");
		index.debugAnalyzerDuringSearch = false;
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
	public void termQuery()  {
		//TODO - Write a TermQuery that returns a hit for id
		TermQuery query = new TermQuery(new Term("id","1"));
		assertEquals(1, countResults(query));
	}
	
	@Test
	public void termRangeQuery() throws CorruptIndexException, IOException, InterruptedException {
		addDocument(defaultConfiguredField("name", "Anna"));
		addDocument(defaultConfiguredField("name", "Britta"));
		addDocument(defaultConfiguredField("name", "Cissi"));
		addDocument(defaultConfiguredField("name", "David"));
		addDocument(defaultConfiguredField("name", "Davidsson"));
		addDocument(defaultConfiguredField("name", "…rjan"));
		
		// Construct this query so the asserts work
		TermRangeQuery query = new TermRangeQuery("name", "a","c", true, true);
		assertEquals(2, countResults(query));
		assertEqualsString(query,0, "name", "Anna");
		assertEqualsString(query,1, "name", "Britta");
		
		// Reconstruct this query so the asserts work
		query = new TermRangeQuery("name", "c",null, true, true);
		assertEquals(4, countResults(query));
		assertEqualsString(query,0, "name", "Cissi");
		assertEqualsString(query,3, "name", "…rjan");
	}
	
	@Test
	public void prefixQuery() {
		
		PrefixQuery query = new PrefixQuery(new Term("author","joh"));
		assertEquals(1, countResults(query));
		assertEqualsString(query,0, "author", "johan.rask@jayway.com");
		
		query = new PrefixQuery(new Term("title","rac"));
		assertEquals(1, countResults(query));
		assertEqualsString(query,0, "title", "The art of Adventure racing from wikipedia");
	}
	  
	@Test
	public void booleanQuery() {
		addDocument(
				defaultConfiguredField("id", "2"),
				defaultConfiguredField("title", "The art of sleeping"),
				defaultConfiguredField("author", "johan.rask@jayway.com"));
		TermQuery termQuery = new TermQuery(new Term("title","art"));
		TermQuery termQuery2 = new TermQuery(new Term("title","sleeping"));
		PrefixQuery termQuery3 = new PrefixQuery(new Term("author","johan"));
		BooleanQuery query = new BooleanQuery();
		// Combine all these queries so the asserts are correct
		
		query.add(termQuery, Occur.SHOULD);
		query.add(termQuery2, Occur.MUST_NOT);
		query.add(termQuery3, Occur.MUST);
		System.out.println(query);
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
	public void wildcardQuery() {
		addDocument(defaultConfiguredField("text", "hoppsan"));
		addDocument(defaultConfiguredField("text", "hejsan"));
		addDocument(defaultConfiguredField("text", "hoppa"));
		addDocument(defaultConfiguredField("text", "heja"));
		addDocument(defaultConfiguredField("text", "loppa"));
		
		WildcardQuery query = new WildcardQuery(new Term("text","?opp*"));
		assertCountResults(3, query);
		assertEqualsString(query, 0, "text", "hoppsan");
		assertEqualsString(query, 1, "text", "hoppa");
		assertEqualsString(query, 2, "text", "loppa");
		
		query = new WildcardQuery(new Term("text","*sa?"));
		assertCountResults(2, query);
		assertEqualsString(query, 0, "text", "hoppsan");
		assertEqualsString(query, 1, "text", "hejsan");
	}
	
	@Test
	public void fuzzyQuery() {
		FuzzyQuery query = new FuzzyQuery(new Term("title","adventurous"));
		assertCountResults(1, query);
	}
	
}
