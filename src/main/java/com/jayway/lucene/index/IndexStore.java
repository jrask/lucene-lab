package com.jayway.lucene.index;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.Version;

import com.jayway.lucene.analysis.DebugQueryParser;
import com.jayway.lucene.index.internal.SearcherManager;


/**
 * <p>Wrapper class for some common index operations like
 * adding and searching documents</p>
 * 
 * @author johanrask
 *
 */
public class IndexStore {

	private IndexWriter writer;
	private SearcherManager searcherManager;
	private QueryParser queryParser;
	public boolean debugAnalyzerDuringSearch = false;
	
	public IndexStore(Directory directory,Analyzer indexAnalyzer, Analyzer queryAnalyzer,String defaultQueryField) throws CorruptIndexException, LockObtainFailedException, IOException {
		writer= new IndexWriter(directory,
				indexAnalyzer,
				makeNewIndex(directory),
				MaxFieldLength.UNLIMITED);
		searcherManager = new SearcherManager(writer);
	
		queryParser = new DebugQueryParser(Version.LUCENE_30, defaultQueryField, queryAnalyzer);
	}
	
	/**
	 * A new index should be created if threre are no files in directory or the directory
	 * does not exist
	 */
	private boolean makeNewIndex(Directory dir) throws IOException {
		try {
			return (dir.listAll().length == 0);
		} catch(NoSuchDirectoryException e) {
			return true;
		}
	}
	
	/**
	 * Adds a document to the index. Document will not be visible in index until
	 * committed. 
	 */
	public void addDocument(Document document) throws CorruptIndexException, IOException {
		writer.addDocument(document);
	}

	/**
	 * Commit changes and make documents searchable by refreshing the searcher.
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void commit() throws CorruptIndexException, IOException, InterruptedException {
		writer.commit();
		searcherManager.maybeReopen();
	}

	
	/**
	 * Closes the index
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
	
	/**
	 * Returns {@link Document} found by the specified query but not more
	 * than specified by cnt.
	 * @param query
	 * @param cnt
	 * @return
	 * @throws IOException
	 */
	public Document[] search(Query query, int cnt) throws IOException {
	
		IndexSearcher searcher = searcherManager.get();
		try {
			TopDocs docs = searcher.search(query, 100);
			Document[] result = new Document[docs.scoreDocs.length];
			for(int i = 0; i < docs.scoreDocs.length; i++) {
				result[i] = searcher.doc(docs.scoreDocs[i].doc);
			}
			return result;
		} finally {
			searcherManager.release(searcher);
		}
	}
	
	/**
	 * Returns maximum 100 Documents from the query
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public Document[] search(Query query) throws IOException {
		return search(query,100);
	}
	
	public Document[] search(String query) throws IOException, ParseException {
		Query qquery =  queryParser.parse(query);
		if(debugAnalyzerDuringSearch) {
			System.out.println(query + " => " + qquery);
		}
		return search(qquery);
	}
	
	public void delete(Term... terms) throws CorruptIndexException, IOException {
		for (Term term : terms) {
			writer.deleteDocuments(term);
		}
	}
	
	public void clearIndex() throws IOException, InterruptedException {
		writer.deleteAll();
		commit();
	}

	public Analyzer getAnalyzer() {
		return writer.getAnalyzer();
	}
}
