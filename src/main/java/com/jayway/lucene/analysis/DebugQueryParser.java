package com.jayway.lucene.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

public class DebugQueryParser extends QueryParser {

	public boolean debug = false;
	
	public DebugQueryParser(Version matchVersion, String f, Analyzer a) {
		super(matchVersion, f, a);
	}
	
	
	@Override
	protected org.apache.lucene.search.Query getFieldQuery(String field,
			String queryText) throws ParseException {

		org.apache.lucene.search.Query q = super.getFieldQuery(field, queryText);
		if(debug) {
			System.out.println(String.format("DebugQueryParser.getFieldQuery(%s,%s) => %s",field,queryText,q));
		}
		return q;
	}
	
	@Override
	protected org.apache.lucene.search.Query getRangeQuery(String field,
			String part1, String part2, boolean inclusive)
			throws ParseException {
		
		org.apache.lucene.search.Query query =  super.getRangeQuery(field, part1, part2, inclusive);
		if(debug) {
			System.out.println(String.format("DebugQueryParser.getRangeQuery(%s,%s,%s) => %s",field,part1,part2,query));
		}
		return query;
	}

}
