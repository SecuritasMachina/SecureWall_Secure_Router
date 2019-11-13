package com.ackdev.DataDictionary.tasks;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ackdev.DataDictionary.helper.LuceneHelper;
import com.ackdev.DataDictionary.repository.Repository;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.TableModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class DataSourceIndexer extends TimerTask {
	private static Gson gson = new Gson();
	private static final Logger log = Logger.getLogger(DataSourceIndexer.class);

	public void run() {
		log.info("DataSourceIndexer started");
		Repository repo = Repository.getInstance();
		Collection<DataSourceModel> dsCol = repo.getDataSources(false);
		for (DataSourceModel ds : dsCol) {
			this.indexDS(ds.getId());
			log.info("Completed Indexing " + ds.getDs_name());
		}
	}

	public void indexDS(String id) {
		Repository repo = Repository.getInstance();
		boolean showExpired = false;
		try {
			Directory directory = LuceneHelper.getDirectory();
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directory, indexWriterConfig);

			DataSourceModel ds = repo.getDataSource(id);
			List<TableModel> tables = repo.getTables(ds.getId(), showExpired);
			for (TableModel table : tables) {
				log.info("Indexing " + ds.getDs_name() + "." + table.getName());
				Collection<FieldModel> fields = repo.getFields(table.getId(), showExpired);
				for (FieldModel field : fields) {
					String jsonIndexData = "";
					JSONParser parser = new JSONParser();
					Object obj;
					try {
						obj = parser.parse(gson.toJson(field));
						JSONObject jsonObject = (JSONObject) obj;
						Iterator<?> iterator = jsonObject.entrySet().iterator();

						while (iterator.hasNext()) {
							@SuppressWarnings("unchecked")
							Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator.next();
							jsonIndexData += " " + entry.getValue();
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Document document = new Document();
					document.add(new TextField("id", field.getId(), Field.Store.YES));
					document.add(new TextField("rawguid", field.getId().replace("-", ""), Field.Store.YES));
					document.add(new TextField("tags", blankNull(field.getTags()), Field.Store.NO));
					document.add(new TextField("name", blankNull(field.getName()), Field.Store.NO));
					document.add(new TextField("owner", blankNull(field.getOwner()), Field.Store.NO));
					document.add(new TextField("confidentiality_control", blankNull(field.getConfidentiality_control()),
							Field.Store.NO));
					document.add(new TextField("integrity_control", blankNull(field.getIntegrity_control()),
							Field.Store.NO));
					document.add(new TextField("availability_control", blankNull(field.getAvailability_control()),
							Field.Store.NO));
					document.add(new TextField("sor", blankNull(field.getSor()), Field.Store.NO));
					document.add(new TextField("soa", blankNull(field.getSoa()), Field.Store.NO));
					document.add(new TextField("informationOwner", blankNull(field.getInformationOwner()), Field.Store.NO));
					
					document.add(new TextField("json", jsonIndexData, Field.Store.NO));
					writer.deleteDocuments(new Term("rawguid", field.getId().replace("-", "")));
					writer.addDocument(document);
				}
			}
			writer.commit();
			writer.close();
		} catch (Exception e) {
			log.error("Error during indexing", e);
		}
		log.info("Committed Indexing");

	}

	private String blankNull(String s) {
		if (s == null) {
			return "";
		} else {
			return s.trim();
		}
	}

}
