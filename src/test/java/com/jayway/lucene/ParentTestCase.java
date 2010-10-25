package com.jayway.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.junit.After;

import com.jayway.lucene.analysis.AnalyzerUtils;
import com.jayway.lucene.index.IndexStore;

public abstract class ParentTestCase {

	protected boolean debugAnalyzerOnIndexing = false;
	
	protected IndexStore index;
	
	@After
	public void clearIndex() throws IOException, InterruptedException {
		index.clearIndex();
	}
	
	protected NumericField defaultConfiguredField(String key, double value) throws IOException {
		if(debugAnalyzerOnIndexing) {
			AnalyzerUtils.displayTokens(String.valueOf(value), index.getAnalyzer());
		}
		return new NumericField(key, Store.YES,true).setDoubleValue(value);
	}
	
	protected NumericField defaultConfiguredField(String key, long value) throws IOException {
		if(debugAnalyzerOnIndexing) {
			AnalyzerUtils.displayTokens(String.valueOf(value), index.getAnalyzer());
		}
		return new NumericField(key, Store.YES,true).setLongValue(value);
	}
	
	protected Field defaultConfiguredField(String key, String value) throws IOException {
		return defaultConfiguredField(-1,key, value);
	}
	
	protected Field defaultConfiguredField(float boost,String key, String value) throws IOException {
		if(debugAnalyzerOnIndexing) {
			AnalyzerUtils.displayTokens(value, index.getAnalyzer());
		}
		Field f = new Field(key, value, Store.YES, Field.Index.ANALYZED);
		if(boost != -1) {
			f.setBoost(boost);
		}
		return f;
	}
	
	protected void addDocument(Fieldable... fields) throws CorruptIndexException, IOException, InterruptedException {
		addDocument(-1,fields);
	}
	
	protected void addDocument(float boost,Fieldable... fields) throws CorruptIndexException, IOException, InterruptedException {
		Document doc = new Document();
		for (Fieldable field : fields) {
			doc.add(field);
		}
		if(boost != -1) {
			doc.setBoost(boost);
		}
		index.addDocument(doc);
		index.commit();
	}
}
