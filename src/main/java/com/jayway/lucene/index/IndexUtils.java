package com.jayway.lucene.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.lucene.document.Field;

public class IndexUtils {
 
	
	public static Field createField(String key, File file) {
		try {
			return new Field(key,new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
