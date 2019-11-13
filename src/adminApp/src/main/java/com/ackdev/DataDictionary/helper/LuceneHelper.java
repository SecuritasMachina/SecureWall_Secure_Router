package com.ackdev.DataDictionary.helper;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.ackdev.DataDictionary.reader.ConfigReader;

public class LuceneHelper implements AutoCloseable {
	private static final Logger log = Logger.getLogger(LuceneHelper.class);


	public static Directory getDirectory() throws IOException {
		Directory directory = FSDirectory.open(Paths.get(ConfigReader.getDictionaryPath()));
		return directory;

	}

	@Override
	public void close() throws Exception {
		getDirectory().close();
	}

}
