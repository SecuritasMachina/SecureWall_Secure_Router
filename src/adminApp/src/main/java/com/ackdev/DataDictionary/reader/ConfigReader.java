package com.ackdev.DataDictionary.reader;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

public class ConfigReader {
	private static final Logger log = Logger.getLogger(ConfigReader.class);
	static boolean dontshowLog = false;

	public static String getDictionaryPath() {
		String ret = "";
		try {
			Context initCxt = new InitialContext();
			ret = (String) initCxt.lookup("java:comp/env/LuceneDictionaryPath");
			if (!dontshowLog)
				log.info("using java:comp/env/LuceneDictionaryPath yielded: " + ret);
			if (!ret.startsWith("/")) {
				if (ret.equalsIgnoreCase("temp") || ret.equalsIgnoreCase("tmp")) {
					ret = System.getProperty("java.io.tmpdir") + "/EnterpriseDataDictionary";
				} else {
					ret = System.getenv(ret);
				}
				if (ret == null) {
					throw new NullPointerException();
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage() + " error using path or environment variable");
			ret = System.getProperty("java.io.tmpdir") + "/EnterpriseDataDictionary";
			log.warn("LuceneDictionaryPath defaulting to: " + ret);
		}
		return ret;

	}

	public static int getDataSourcePollHours() {
		int ret = 12;
		try {
			Context initCxt = new InitialContext();
			ret = ((Long) initCxt.lookup("java:comp/env/DataSourcePollHours")).intValue();
			if (!dontshowLog)
				log.info("using java:comp/env/DataSourcePollHours yielded: " + ret);
		} catch (Exception e) {
			log.error(e.getMessage() + " error using path or environment variable");
			ret = 12;
			log.warn("DataSourcePollHours defaulting to: " + ret);
		}
		return ret;

	}

	public static int getIndexerPollMinutes() {
		int ret = 15;
		try {
			Context initCxt = new InitialContext();
			ret = ((Long) initCxt.lookup("java:comp/env/IndexerPollMinutes")).intValue();
			if (!dontshowLog)
				log.info("using java:comp/env/IndexerPollMinutes yielded: " + ret);
		} catch (Exception e) {
			log.error(e.getMessage() + " error using path or environment variable");
			ret = 15;
			log.warn("IndexerPollMinutes defaulting to: " + ret);
		}
		return ret;

	}

}
