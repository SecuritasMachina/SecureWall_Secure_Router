package com.ackdev.DataDictionary.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.log4j.Logger;

import com.ackdev.DataDictionary.repository.IndexRepository;
import com.ackdev.DataDictionary.repository.Repository;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.TableModel;

public class ScanDS {
	
	private static ScanDS _instance = null;

	public static ScanDS getInstance(boolean _isTest) {
		if (_instance == null) //covered elsewhere
			_instance = new ScanDS(_isTest);
		return _instance;
	}

	private Logger log = Logger.getLogger(ScanDS.class);
	private Repository repo = null;
	private boolean _isTest;

	private ScanDS(boolean _isTest) {
		this._isTest = _isTest;
		repo = Repository.getInstance( null);
	}

	public DataSourceModel doScan(DataSourceModel dbModel) {
		BasicDataSource ds = new BasicDataSource();
		dbModel.setLastScanned(System.currentTimeMillis());
		dbModel.setError(null);
		DataSourceModel dbModel1 = repo.updateDataSource(dbModel);

		ds.setDriverClassName(dbModel.getDriver());
		ds.setUsername(dbModel.getUsername());
		ds.setPassword(dbModel.getPassword());
		ds.setUrl(dbModel.getDSN());

		ds.setMaxActive(10);
		ds.setMaxIdle(5);
		ds.setInitialSize(5);
		ds.setValidationQuery("SELECT 1");
		Database db = null;
		try {
			db = readDatabase(ds);
		} catch (Exception e) {
			log.error("Encountered error while scanning '" + dbModel.getDs_name() + "': " + e.getLocalizedMessage());
			dbModel1.setError(e.getLocalizedMessage());
			repo.updateDataSource(dbModel1);
			return null;

		}
		dbModel1.setVersion(db.getVersion());
		dbModel1 = repo.updateDataSource(dbModel1);
		repo.updateScanHistory(dbModel1);

		for (Table t : db.getTables()) {
			log.debug("Scanning " + t.getName());
			TableModel tModel = repo.getTableByName(dbModel1.getId(), t.getName(), false);

			if (tModel == null) {
				tModel = new TableModel();
				tModel.setData_source_id_fk(dbModel1.getId());
				tModel.setName(t.getName());
				tModel.setDsName(dbModel1.getDs_name());
			}
			Connection con = null;
			long count = 0;
			String sql = "SELECT count(1) FROM " + t.getName();
			try {
				con = ds.getConnection();
				try (PreparedStatement stmt = con.prepareStatement(sql)) {
					try (ResultSet rs = stmt.executeQuery()) {
						while (rs.next()) {
							count = rs.getLong(1);
						}
					}
				}
			} catch (SQLException e) {
				log.error("SQLException", e);
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
			tModel.setRowCount(count);
			TableModel tModel2 = repo.updateTable(tModel);

			for (Column f : t.getColumns()) {
				FieldModel fModel = repo.getFieldByName(tModel2.getId(), f.getName(), false);
				if (fModel == null) {
					fModel = new FieldModel();
					fModel.setTable_def_id_fk(tModel2.getId());
				}
				fModel.setName(f.getName());
				fModel.setDsName(dbModel1.getDs_name());
				fModel.setTableName(t.getName());
				fModel.setDbdescription(f.getDescription());
				fModel.setDefaultvalue(f.getDefaultValue());
				fModel.setIsautoincrement(f.isAutoIncrement());
				fModel.setIsprimarykey(fModel.getIsprimarykey());
				fModel.setIsrequired(fModel.getIsrequired());
				fModel.setSize(fModel.getSize());
				fModel.setType(fModel.getType());
				repo.updateField(fModel);
			}
		}
		log.info("Completed scanning: " + dbModel.getDs_name());
		log.info("Starting Indexer for " + dbModel.getDs_name());
		IndexRepository ir = IndexRepository.getInstance(_isTest);
		ir.startIndexing(dbModel.getId());

		return dbModel1;
	}

	private Database readDatabase(BasicDataSource dataSource) {
		Platform platform = PlatformFactory.createNewPlatformInstance(dataSource);

		return platform.readModelFromDatabase(dataSource.getDefaultCatalog());

	}
}
