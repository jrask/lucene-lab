package com.jayway.lucene;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.lucene.index.IndexStore;
import com.jayway.lucene.index.IndexUtils;

public class DiscoverMoreAnalyzersTest extends AbstractParentTestCase {

	
	public void setupIndex(Analyzer analyzer) throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {
		index = new IndexStore(new RAMDirectory(),analyzer,analyzer,"body",debugQueryParser);
		index.debugAnalyzerDuringSearch = false;
	}
	
	
	/*
	 * TODO - Change indexing process so that all these tests works.
	 *        DO not dare changing the asserts!
	 */
	@Test
	@Ignore
	public void solveAllAnalyzerIssues() throws CorruptIndexException, IOException, InterruptedException, ParseException {
		setupIndex(standard);
		addDocument(
			defaultConfiguredField("id", "1"),
			defaultConfiguredField("title", "The art of Adventure racing from wikipedia"),
			defaultConfiguredField("preamble", "This article describes a little about " +
				"Adventure racing and is taken from wikipedia"),
			IndexUtils.createField("body", new File("src/main/resources/body.txt")),
			defaultConfiguredField("author", "johan.rask@jayway.com"),
			defaultConfiguredField("keys", "JaVa LuCeNe FoOd"));
	
		assertNull(index.search("id:1")[0].get("id"));
		assertEquals(0, index.search("keys:java").length);
		assertEquals(1, index.search("title:Art Of adventure Racing").length);
	}
}
