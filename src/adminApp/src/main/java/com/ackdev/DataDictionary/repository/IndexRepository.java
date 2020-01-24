package com.ackdev.DataDictionary.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.ackdev.DataDictionary.helper.LuceneHelper;
import com.ackdev.DataDictionary.tasks.DataSourceIndexer;
import com.ackdev.common.model.FieldModel;

public class IndexRepository {
	private static IndexRepository _instance = null;

	public static IndexRepository getInstance(boolean _isTest) {
		if (_instance == null)
			_instance = new IndexRepository(_isTest);
		return _instance;
	}

	private Repository repo = null;
	private static final Logger log = Logger.getLogger(IndexRepository.class);
	private Directory directory;
	private Analyzer analyzer = new StandardAnalyzer();

	private IndexRepository(boolean isTest) {
		repo = Repository.getInstance(null);
		try {
			directory = LuceneHelper.getDirectory();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(),e);
		}
	}

	public List<Document> searchIndex(Query query) {
		try {
			IndexReader indexReader = DirectoryReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, 1000);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}

			return documents;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection<FieldModel> searchFields(String terms, Boolean showExpired) {
		// create search, lookup matching docs, pull from DB or deserialzie json?
		Collection<FieldModel> ret = new ArrayList<>();
		Query query;
		try {
			// terms = "*" + terms + "*";
			query = new QueryParser("json", analyzer).parse(terms);
			List<Document> resultDocs = searchIndex(query);
			log.info("found " + resultDocs.size() + " using " + query.toString());
			for (Document d : resultDocs) {
				String id = d.getField("id").stringValue();
				FieldModel f = repo.getField(id);
				ret.add(f);
			}
		} catch (ParseException e) {
			log.error("parse error", e);
		}

		return ret;

	}

	public void resetIndexes() {
		try {
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
			long deleted = writer.deleteAll();
			log.warn("Index documents deleted: " + deleted);
			writer.commit();
			writer.close();
		} catch (IOException e) {
			log.error("delete all error", e);
		}

	}

	public void startIndexing() {
		new DataSourceIndexer().run();
	}

	public void startIndexing(String id) {
		new DataSourceIndexer().indexDS(id);

	}

	public Long getAvailableCount() {
		// TODO Auto-generated method stub
		return null;
	}


}
