package com.jayway.lucene.index;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.lucene.analysis.CustomAnalyzer;

public class DiscoverAnalyzersTest {

	
	private Analyzer standard = new StandardAnalyzer(Version.LUCENE_30);
	private Analyzer whitespace = new WhitespaceAnalyzer();
	private Analyzer simple = new SimpleAnalyzer();
	private Analyzer keyword = new KeywordAnalyzer();
	private Analyzer stop = new StopAnalyzer(Version.LUCENE_30);
	private Analyzer custom = new CustomAnalyzer();
	
	private IndexStore index;
	
	@Before
	public void setupIndex() throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {
		index = new IndexStore(new RAMDirectory(),custom,custom,"body");
		
		addDocument(
				defaultConfiguredField("id", "1"),
				defaultConfiguredField("title", "The art of Adventure racing from wikipedia"),
				defaultConfiguredField("preamble", "This article describes a little about " +
					"Adventure racing and is taken from wikipedia"),
				IndexUtils.createField("body", new File("src/main/resources/body.txt")),
				defaultConfiguredField("author", "johan.rask@jayway.com"),
				defaultConfiguredField("keys", "JaVa LuCeNe FoOd"));
	}
	
	private void addDocument(Field... fields) throws CorruptIndexException, IOException, InterruptedException {
		Document doc = new Document();
		for (Field field : fields) {
			doc.add(field);
		}
		index.addDocument(doc);
		index.commit();
	}

	@Test
	public void simpleExactTermSearchWorksWithCustomAnalyzer() throws IOException, ParseException {
		assertEquals(1, index.search(new TermQuery(new Term("id","1"))).length);
		assertEquals(1, index.search(new TermQuery(new Term("author","johan.rask@jayway.com"))).length);	
		assertEquals(0, index.search("title:The art of").length);
		assertEquals(1, index.search("title:\"The art of Adventure racing from wikipedia\"").length);
		assertEquals(0, index.search("title:the art of Adventure racing from wikipedia").length);
		assertEquals(0, index.search("title:Adventure racing").length);
	}
	
	
	@Test
	@Ignore
	public void termSearch() throws IOException, ParseException {
		assertEquals(1, index.search("title:The").length);
		assertEquals(0, index.search("title:the").length);
		assertEquals(1, index.search("title:art").length);
	}
	
	@Test
	@Ignore
	public void phraseSearches() throws CorruptIndexException, IOException, InterruptedException, ParseException {		
		assertEquals(1, index.search("body:\"traverse mountainous terrain while carrying\"").length);
		assertEquals(0, index.search("body:\"to Be\"").length);	
		assertEquals(1, index.search("title:the art of Adventure racing from wikipedia").length);
		assertEquals(1, index.search("title:The art of Adventure racing from wikipedia").length);
		assertEquals(0, index.search("title:\"the art of Adventure racing from wikipedia\"").length);
	}
	
	@Test
	@Ignore
	public void defaultFieldSearch() throws IOException, ParseException {
		assertEquals(1, index.search("traverse mountainous terrain while carrying").length);
	}
	
	@Test
	@Ignore
	public void keywordSearch() throws IOException, ParseException {
		assertEquals(1, index.search("keys:JaVa").length);
		assertEquals(0,  index.search("keys:java").length);
	}
	
	private Field defaultConfiguredField(String key, String value) {
		return new Field(key, value, Store.YES, Field.Index.ANALYZED);
	}
}
