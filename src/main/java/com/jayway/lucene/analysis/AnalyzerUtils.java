package com.jayway.lucene.analysis;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
* Stolen from Lucene in Action book but slightly changed
 */
public class AnalyzerUtils {

	public static void displayTokens(String text,Analyzer... analyzers)
			throws IOException {
		
		for (Analyzer analyzer : analyzers) {
			System.out.print(analyzer.getClass().getSimpleName() + ":");
			displayTokens(analyzer.tokenStream("contents", new StringReader(text))); 
		}

	}

	public static void displayTokens(TokenStream stream) throws IOException {

		/*
		*/
		TermAttribute term = (TermAttribute) stream
				.addAttribute(TermAttribute.class);
		while (stream.incrementToken()) {
			System.out.print("[" + term.term() + "] "); // B
		}
		System.out.println();
	}
}
