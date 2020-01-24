package com.ackdev.DataDictionary.tasks;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import com.ackdev.DataDictionary.helper.LuceneHelper;
import com.ackdev.DataDictionary.reader.ConfigReader;
import com.ackdev.DataDictionary.repository.DataDictionarySingleton;
import com.ackdev.DataDictionary.repository.Repository;

@WebListener
public class BackgroundJobManager implements ServletContextListener {

	private ScheduledExecutorService scheduler;
	private static final Logger log = Logger.getLogger(BackgroundJobManager.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.info("Waiting 15 seconds for system to quiet down before starting indexing");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
		}
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new ScanAllDSTimerTask(), 0, ConfigReader.getDataSourcePollHours(),
				TimeUnit.HOURS);
		scheduler.scheduleAtFixedRate(new DataSourceIndexer(), 15 * 1000 * 60, ConfigReader.getIndexerPollMinutes(),
				TimeUnit.MINUTES);

	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.info("contextDestroyed scheduler.shutdownNow");
		scheduler.shutdownNow();

		log.info("contextDestroyed driver unload");
		// ... First close any background tasks which may be using the DB ...
		Repository.getInstance().close();
		DataDictionarySingleton.getInstance().close();
		try {
			LuceneHelper.getDirectory().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ... Then close any DB connection pools ...

		// Now deregister JDBC drivers in this context's ClassLoader:
		// Get the webapp's ClassLoader
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		// Loop through all drivers
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				// This driver was registered by the webapp's ClassLoader, so deregister it:
				try {
					log.info("Deregistering JDBC driver: " + driver);
					DriverManager.deregisterDriver(driver);
				} catch (SQLException ex) {
					log.error("Error deregistering JDBC driver: " + driver, ex);
				}
			} else {
				// driver was not registered by the webapp's ClassLoader and may be in use
				// elsewhere
				log.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader: "
						+ driver);
			}
		}
	}

}
