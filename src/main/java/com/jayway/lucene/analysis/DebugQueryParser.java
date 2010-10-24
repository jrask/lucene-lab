package com.jayway.lucene.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

public class DebugQueryParser extends QueryParser {

	public DebugQueryParser(Version matchVersion, String f, Analyzer a) {
		super(matchVersion, f, a);
	}
	
	
	@Override
	protected org.apache.lucene.search.Query getFieldQuery(String field,
			String queryText) throws ParseException {

		org.apache.lucene.search.Query q = super.getFieldQuery(field, queryText);
		System.out.println(String.format("DebugQueryParser.getFieldQuery(%s,%s) => %s",field,queryText,q));
		return q;
	}
	
	

}
