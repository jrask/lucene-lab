package com.jayway.lucene.analysis;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * Stolen from Lucene in Action book but slightly changed
 */
public class AnalyzerUtils {

	public static void displayTokens(String text, Analyzer... analyzers)
			throws IOException {

		for (Analyzer analyzer : analyzers) {
			System.out.print(analyzer.getClass().getSimpleName() + ":");
			displayTokens(analyzer.tokenStream("contents", new StringReader(
					text)));
		}
		
	}

	public static void displayTokens(TokenStream stream) {
		TermAttribute term = (TermAttribute) stream
				.addAttribute(TermAttribute.class);
		try {
			while (stream.incrementToken()) {
				System.out.print("[" + term.term() + "] "); // B
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

	public static void displayTokensWithPositions(Analyzer analyzer, String text)
			throws IOException {
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
		TermAttribute term = (TermAttribute) stream
				.addAttribute(TermAttribute.class);
		PositionIncrementAttribute posIncr = (PositionIncrementAttribute) stream
				.addAttribute(PositionIncrementAttribute.class);

		int position = 0;
		while (stream.incrementToken()) {
			int increment = posIncr.getPositionIncrement();
			if (increment > 0) {
				position = position + increment;
				System.out.println();
				System.out.print(position + ": ");
			}
			System.out.print("[" + term.term() + "] ");
			System.out.println();
		}
	}
}
