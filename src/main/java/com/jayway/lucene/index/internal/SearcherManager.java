package com.jayway.lucene.index.internal;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

/**
 * Class used to make sure that {@link IndexSearcher} instances are
 * closed and opened correctly although accessed from multiple threads.
 * 
 * This example is taken from Lucene in action book
 * 
 *
 */
public class SearcherManager {

	private IndexSearcher currentSearcher;
	private IndexWriter writer;

	public SearcherManager(Directory dir) throws IOException { // 1
		currentSearcher = new IndexSearcher(IndexReader.open(dir)); // B
		warm(currentSearcher);
	}

	public SearcherManager(IndexWriter writer) throws IOException { // 2
		this.writer = writer;
		currentSearcher = new IndexSearcher(writer.getReader()); // C
		warm(currentSearcher);

		writer.setMergedSegmentWarmer( // D
				new IndexWriter.IndexReaderWarmer() { // D
					public void warm(IndexReader reader) throws IOException { // D
						SearcherManager.this.warm(new IndexSearcher(reader)); // D

					} // D
				}); // D
	}

	public void warm(IndexSearcher searcher) // E
			throws IOException // E
	{
	} // E

	private boolean reopening;

	private synchronized void startReopen() // F
			throws InterruptedException {
		while (reopening) {
			wait();
		}
		reopening = true;
	}

	private synchronized void doneReopen() { // G
		reopening = false;
		notifyAll();
	}

	public void maybeReopen() throws InterruptedException, IOException { // H

		startReopen();

		try {
			final IndexSearcher searcher = get();
			try {
				IndexReader newReader = currentSearcher.getIndexReader()
						.reopen(); // I
				if (newReader != currentSearcher.getIndexReader()) { // I
					IndexSearcher newSearcher = new IndexSearcher(newReader); // I
					if (writer == null) { // I
						warm(newSearcher); // I
					} // I
					swapSearcher(newSearcher); // I
				}
			} finally {
				release(searcher);
			}
		} finally {
			doneReopen();
		}
	}

	public synchronized IndexSearcher get() { // J
		currentSearcher.getIndexReader().incRef();
		return currentSearcher;
	}

	public synchronized void release(IndexSearcher searcher) // K
			throws IOException {
		searcher.getIndexReader().decRef();
	}

	private synchronized void swapSearcher(IndexSearcher newSearcher) // L
			throws IOException {
		release(currentSearcher);
		currentSearcher = newSearcher;
	}
}
