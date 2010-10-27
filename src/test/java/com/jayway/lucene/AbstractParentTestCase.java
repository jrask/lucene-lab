package com.jayway.lucene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.After;

import com.jayway.lucene.analysis.AnalyzerUtils;
import com.jayway.lucene.analysis.CustomAnalyzer;
import com.jayway.lucene.index.IndexStore;

public abstract class AbstractParentTestCase {

	protected Analyzer standard = new StandardAnalyzer(Version.LUCENE_30);
	protected Analyzer whitespace = new WhitespaceAnalyzer();
	protected Analyzer simple = new SimpleAnalyzer();
	protected Analyzer keyword = new KeywordAnalyzer();
	protected Analyzer stop = new StopAnalyzer(Version.LUCENE_30);
	protected Analyzer custom = new CustomAnalyzer();

	
	protected boolean debugAnalyzerOnIndexing = false;
	protected boolean debugQueryParser = false;

	
	protected IndexStore index;
	
	@After
	public void clearIndex() throws IOException, InterruptedException {
		if(index != null) {	
			index.clearIndex();
		}
		System.out.println(" ----------- Index cleared ------------");
	}
	
	protected NumericField defaultConfiguredField(String key, double value) {
		if(debugAnalyzerOnIndexing) {
			try {
				AnalyzerUtils.displayTokens(String.valueOf(value), index.getAnalyzer());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return new NumericField(key, Store.YES,true).setDoubleValue(value);
	}
	
	protected NumericField defaultConfiguredField(String key, long value)   {
		if(debugAnalyzerOnIndexing) {
			try { 
				AnalyzerUtils.displayTokens(String.valueOf(value), index.getAnalyzer());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return new NumericField(key, Store.YES,true).setLongValue(value);
	}
	
	protected Field defaultConfiguredField(String key, String value)  {
		return defaultConfiguredField(-1,key, value);
	}
	
	protected Field defaultConfiguredField(float boost,String key, String value)  {
		if(debugAnalyzerOnIndexing) {
			try {
				AnalyzerUtils.displayTokens(value, index.getAnalyzer());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		Field f = new Field(key, value, Store.YES, Field.Index.ANALYZED);
		if(boost != -1) {
			f.setBoost(boost);
		}
		return f;
	}
	
	protected void addDocument(Fieldable... fields)  {
		addDocument(-1,fields);
	}
	
	protected void addDocument(float boost,Fieldable... fields)  {
		Document doc = new Document();
		for (Fieldable field : fields) {
			doc.add(field);
		}
		if(boost != -1) {
			doc.setBoost(boost);
		}
		try {
			index.addDocument(doc);
			index.commit();
		} catch (CorruptIndexException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}
	
	protected void assertCountResults(int expected, Query query) {
		assertEquals("Count must be of expected length",expected,search(query).length);
	}
	
	protected int countResults(Query query) {
		try {
			return index.search(query).length;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Document[] search(Query query) {
		try {
			return index.search(query);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void printResults(Query query) {
		for(Document doc : search(query)) {
			System.out.println(doc);
		}
	}
	
	protected Query query(String query) {
		try {
			return index.getQueryParser().parse(query);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void assertEqualsString(Query query, int pos, String key, String value) {
		assertEquals("Document should match value",value, search(query)[pos].get(key));
	}
}
