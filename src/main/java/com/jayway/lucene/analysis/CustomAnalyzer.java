package com.jayway.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;


/**
 * This analyzer returns the complete text a a single token.
 * Using this analyzer presents what happens to lucene without
 * analysis.
 * 
 * @author johanrask
 *
 */
public class CustomAnalyzer extends Analyzer {
	
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new CharTokenizer(reader) {

			@Override
			protected boolean isTokenChar(char c) {
				return true;
			}
			

	};
	}
}
