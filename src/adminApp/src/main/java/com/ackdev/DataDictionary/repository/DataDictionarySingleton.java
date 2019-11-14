package com.ackdev.DataDictionary.repository;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

public class DataDictionarySingleton implements AutoCloseable {
	private static DataDictionarySingleton _instance = null;
	private Context ctx = null;
	private DataSource ds = null;
	private Logger log = Logger.getLogger(DataDictionarySingleton.class);
	BasicDataSource myPool = null;

	// private constructor restricted to this class itself
	private DataDictionarySingleton() {
		ds = (DataSource) getJNDIPool();
	}

	private Object getJNDIPool() {
		try {
			ctx = new InitialContext();
			return ctx.lookup("java:/comp/env/jdbc/AckDevDataDictionary");
		} catch (Exception e) {
			log.error("Context Data Source 'java:/comp/env/jdbc/AckDevDataDictionary' not found", e);
		}
		return null;

	}

	// static method to create instance of Singleton class
	public static DataDictionarySingleton getInstance() {
		if (_instance == null)
			_instance = new DataDictionarySingleton();
		return _instance;
	}


	public DataSource getDS() {
		return ds;
	}

	@Override
	public void close() {
		try {
			myPool.close();
		} catch (Exception e) {
		}
	}
}
