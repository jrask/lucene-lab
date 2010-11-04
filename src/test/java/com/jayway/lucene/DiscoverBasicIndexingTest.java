package com.jayway.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import com.jayway.lucene.index.IndexStore;

public class DiscoverBasicIndexingTest extends AbstractParentTestCase {

	public void setupIndex(Analyzer analyzer) throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {
		index = new IndexStore(new RAMDirectory(),analyzer,analyzer,"body",debugQueryParser);
//		index.debugAnalyzerDuringSearch = false;
	}
	
	@Before
	public void indexFields() throws CorruptIndexException, IOException, InterruptedException {
		setupIndex(standard);
		Document doc = new Document();
		doc.add(new Field("id","1", Store.YES, Index.NO));
		doc.add(new Field("title","This is a title", Store.YES, Index.ANALYZED));
		doc.add(new Field("abstract","This is an abstract", Store.YES, Index.ANALYZED));
		doc.add(new Field("body","This is laaarge body text", Store.NO, Index.ANALYZED));
		doc.add(new Field("email","johan.rask@jayway.com",Store.YES,Index.NOT_ANALYZED));
		index.addDocument(doc);
		index.commit();
	}
	
	@Test
	public void search() {
		assertCountResults(1, query("email:johan.rask@jayway.com"));
		assertCountResults(1, query("body"));
		assertCountResults(1, query("title:title"));
	}
	
}
