package com.ackdev.DataDictionary.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.ackdev.common.model.AuditLogModel;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldData;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.NodeModel;
import com.ackdev.common.model.TableFields;
import com.ackdev.common.model.TableModel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;

public class Repository implements AutoCloseable {
	private static Repository _instance = null;
	private HttpServletRequest request = null;

	private Repository(HttpServletRequest servletRequest) {
		cache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(60, TimeUnit.SECONDS).build();
		this.request = servletRequest;
		this.dataSource = DataDictionarySingleton.getInstance();

		this.repoHelper = RepositoryHelper.getInstance();
	}

	private Logger log = Logger.getLogger(Repository.class);
	private Gson gson = new Gson();
	private Cache<String, DataSource> cache;
	private DataDictionarySingleton dataSource;
	private RepositoryHelper repoHelper;

	public static Repository getInstance(HttpServletRequest servletRequest) {
		if (_instance == null)
			_instance = new Repository(servletRequest);
		_instance.request = servletRequest;
		return _instance;
	}

	public static Repository getInstance() {
		if (_instance == null)
			_instance = new Repository(null);
		return _instance;
	}

	public void deleteDataSource(String id) {
		DataSourceModel ds = this.getDataSource(id);
		ds.setExpirationDate(System.currentTimeMillis());
		this.updateDataSource(ds);
		deleteNode(id);

	}

	public void deleteField(String id) {
		FieldModel field = this.getField(id);
		field.setExpirationDate(System.currentTimeMillis());
		this.updateField(field);
		deleteNode(id);

	}

	public void deleteNode(String id) {
		String sql = "update objects set expirationdate = ? where id = ?";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setLong(1, System.currentTimeMillis());
				stmt.setString(2, id);
				int rowCount=stmt.executeUpdate();
				if(rowCount!=1) {
					throw new SQLException("error deleting "+id);
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

	}

	public void deleteTable(String id) {
		TableModel table = this.getTable(id);
		table.setExpirationDate(System.currentTimeMillis());
		this.updateTable(table);
		deleteNode(id);

	}

	public Collection<NodeModel> findNodesByType(String nodeType, boolean showExpired) {
		List<NodeModel> ret = new ArrayList<>();
		String sql = "SELECT * FROM objects where nodeType=? and (expirationDate is null or expirationdate = 0) order by name";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, nodeType);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						ret.add(RepositoryHelper.copyRStoDM(rs));
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

		return ret;
	}

	private Collection<NodeModel> findNodesModelByParentId_Name(String fk, String nodeType, Boolean showExpired) {
		List<NodeModel> ret = new ArrayList<>();
		String sql = "SELECT * FROM objects where parent_id=? and nodeType=? and (expirationDate is null or expirationdate = 0) order by name";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, fk);
				stmt.setString(2, nodeType);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						ret.add(RepositoryHelper.copyRStoDM(rs));
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

		return ret;
	}

	public List<NodeModel> findNodesModelByParentId_Name(String id, String nodeType, String name, Boolean showExpired) {
		List<NodeModel> ret = new ArrayList<>();
		String sql = "SELECT * FROM objects where parent_id=? and nodeType=? and name=? and (expirationDate is null or expirationdate = 0) order by name";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, id);
				stmt.setString(2, nodeType);
				stmt.setString(3, name);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						ret.add(RepositoryHelper.copyRStoDM(rs));
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

		return ret;
	}

	public Collection<FieldModel> getAllFields(Boolean showExpired) {
		List<FieldModel> ret = new ArrayList<>();
		Collection<NodeModel> allDs = this.findNodesByType(DataSourceModel.class.getName(), showExpired);
		for (NodeModel ds : allDs) {
			Collection<NodeModel> tables = this.findNodesModelByParentId_Name(ds.getId(), TableModel.class.getName(),
					showExpired);
			for (NodeModel t : tables) {
				Collection<NodeModel> fields = this.findNodesModelByParentId_Name(t.getId(), FieldModel.class.getName(),
						showExpired);
				for (NodeModel f : fields) {
					FieldModel fm = gson.fromJson(f.getJsonData(), FieldModel.class);
					fm.setDsName(ds.getName());
					fm.setTableName(t.getName());
					ret.add(fm);
				}
			}
		}
		return ret;
	}

	public DataSourceModel getDataSource(String fk) {
		DataSourceModel ret = gson.fromJson(getNodeModel(fk).getJsonData(), DataSourceModel.class);

		return ret;
	}

	public Collection<DataSourceModel> getDataSources(boolean showExpired) {
		List<DataSourceModel> ret = new ArrayList<>();
		Collection<NodeModel> nodes = findNodesByType(DataSourceModel.class.getName(), showExpired);
		for (NodeModel n : nodes) {
			ret.add(gson.fromJson(n.getJsonData(), DataSourceModel.class));
		}
		return ret;
	}

	public FieldModel getField(String id) {
		FieldModel ret = gson.fromJson(getNodeModel(id).getJsonData(), FieldModel.class);
		return ret;
	}

	public FieldModel getFieldByName(String id, String name, boolean showExpired) {
		FieldModel ret = null;
		List<NodeModel> t = this.findNodesModelByParentId_Name(id, FieldModel.class.getName(), name, showExpired);
		if (t.size() > 0)
			ret = gson.fromJson(t.get(0).getJsonData(), FieldModel.class);

		return ret;

	}

	public Collection<FieldModel> getFields(String fk, Boolean showExpired) {
		List<FieldModel> ret = new ArrayList<>();
		Collection<NodeModel> nodes = findNodesModelByParentId_Name(fk, FieldModel.class.getName(), showExpired);
		for (NodeModel n : nodes) {
			ret.add(gson.fromJson(n.getJsonData(), FieldModel.class));
		}
		return ret;
	}

	public Collection<FieldModel> getFieldsByDS(String fk, Boolean showExpired) {
		List<FieldModel> ret = new ArrayList<>();
		NodeModel ds = this.getNodeModel(fk);
		Collection<NodeModel> tables = this.findNodesModelByParentId_Name(ds.getId(), TableModel.class.getName(),
				showExpired);
		// TODO Optimize
		for (NodeModel t : tables) {
			Collection<NodeModel> fields = this.findNodesModelByParentId_Name(t.getId(), FieldModel.class.getName(),
					showExpired);
			for (NodeModel f : fields) {
				FieldModel fm = gson.fromJson(f.getJsonData(), FieldModel.class);
				fm.setTableName(t.getName());
				ret.add(fm);
			}
		}
		return ret;
	}

	public NodeModel getNodeModel(String fk) {
		NodeModel ret = new NodeModel();
		String sql = "SELECT * FROM objects where id=?";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, fk);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						ret = RepositoryHelper.copyRStoDM(rs);
					}
				}
			}
		} catch (Exception e) {
			log.error("SQLException", e);
		} finally {
			try {
				con.close();

			} catch (Exception e) {
				log.error("Exception while closing connection", e);
			}
		}

		return ret;

	}

	public long getCountByType(String fk) {
		long ret = 0;
		String sql = "SELECT count(1) FROM objects where nodeType=? and (expirationDate is null or expirationdate = 0)";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, fk);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						ret = rs.getLong(1);
					}
				}
			}
		} catch (Exception e) {
			log.error("SQLException", e);
		} finally {
			try {
				con.close();

			} catch (Exception e) {
				log.error("Exception while closing connection", e);
			}
		}

		return ret;

	}

	public Collection<FieldData> getPreviewDSFields(String id) {
		// TODO Auto-generated method stub
		// lookup table, lookup DS, make SQL
		ArrayList<FieldData> ret = new ArrayList<>();

		DataSourceModel dbModel = this.getDataSource(id);
		BasicDataSource ds = new BasicDataSource();

		ds.setDriverClassName(dbModel.getDriver());
		ds.setUsername(dbModel.getUsername());
		ds.setPassword(dbModel.getPassword());
		ds.setUrl(dbModel.getDSN());
		ds.setMaxActive(10);
		ds.setMaxIdle(5);
		ds.setInitialSize(5);
		ds.setValidationQuery("SELECT 1");
		List<TableModel> tableModels = this.getTables(id, false);
		for (TableModel t : tableModels) {
			// TODO add limit and paging
			String sql = "SELECT * FROM " + t.getName();
			Connection con = null;
			try {
				con = dataSource.getDS().getConnection();
				try (PreparedStatement stmt = con.prepareStatement(sql)) {

					try (ResultSet rs = stmt.executeQuery()) {
						while (rs.next()) {
							Collection<FieldModel> fields = this.getFields(id, false);
							ArrayList<FieldData> embed = new ArrayList<>();
							for (FieldModel field : fields) {
								FieldData fd = new FieldData();

								fd.setName(field.getName());
								fd.setValue(rs.getString(field.getName()));
								embed.add(fd);
							}
							FieldData fdArray = new FieldData();
							fdArray.setValue(gson.toJson(embed));
							ret.add(fdArray);
							if (ret.size() > 100)
								break;
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
		}

		return ret;
	}

	public Collection<FieldData> getPreviewField(String id) {
		// lookup table, lookup DS, make SQL
		int maxRows = 20;
		ArrayList<FieldData> ret = new ArrayList<>();

		FieldModel f = this.getField(id);
		TableModel t = this.getTable(f.getTable_def_id_fk());
		DataSourceModel dbModel = this.getDataSource(t.getData_source_id_fk());
		BasicDataSource ds = new BasicDataSource();

		ds.setDriverClassName(dbModel.getDriver());
		ds.setUsername(dbModel.getUsername());
		ds.setPassword(dbModel.getPassword());
		ds.setUrl(dbModel.getDSN());
		// log.debug("dbModel.getDSN():" + dbModel.getDSN());
		ds.setMaxActive(2);
		// ds.setDefaultCatalog("nfp");
		ds.setMaxIdle(2);
		ds.setInitialSize(2);
		ds.setValidationQuery("SELECT 1");
		if (dbModel.getDriver().contains("oracle"))
			ds.setValidationQuery("SELECT 1 from dual");
		// TODO add and paging

		String sql = "SELECT distinct ";
		if (dbModel.getDriver().contains("microsoft") || dbModel.getDriver().contains("sajdsas")) {
			sql += " TOP " + maxRows;
		}

		sql += f.getName() + " FROM " + t.getName();
		sql += " WHERE " + f.getName() + " is not null";
		if (dbModel.getDriver().contains("mysql") || dbModel.getDriver().contains("postgresql")) {
			sql += " LIMIT " + maxRows;
		} else if (dbModel.getDriver().contains("oracle")) {
			sql += " and ROWNUM <= " + maxRows;
		}
		log.trace("Preview sql: " + sql);
		Connection con = null;
		try {
			con = ds.getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						FieldData fd = new FieldData();
						String val = rs.getString(1);
						if (!rs.wasNull() && val != null && val.trim().length() > 0) {
							fd.setValue(val);
							ret.add(fd);
						}
						if (ret.size() > maxRows)
							break;
					}
				}
			}
		} catch (SQLException e) {
			log.error("SQLException on id:" + id + " " + e.getLocalizedMessage());
		} finally {
			try {
				con.close();
				ds.close();
			} catch (Exception e) {
			}
		}
		return ret;
	}

	public TableFields getPreviewTableFields(String id) {
		TableFields ret = new TableFields();

		TableModel t = this.getTable(id);
		DataSourceModel dbModel = this.getDataSource(t.getData_source_id_fk());
		BasicDataSource ds = new BasicDataSource();

		ds.setDriverClassName(dbModel.getDriver());
		ds.setUsername(dbModel.getUsername());
		ds.setPassword(dbModel.getPassword());
		ds.setUrl(dbModel.getDSN());
		ds.setMaxActive(10);
		ds.setMaxIdle(5);
		ds.setInitialSize(5);
		ds.setValidationQuery("SELECT 1");
		Collection<FieldModel> fields = this.getFields(id, false);
		ArrayList<String> columns = new ArrayList<>();
		for (FieldModel field : fields) {
			columns.add(field.getName());
		}

		ret.setColumns(columns);
		String sql = "SELECT * FROM " + t.getName();
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			ArrayList<ArrayList<String>> columnData = new ArrayList<ArrayList<String>>();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {

				try (ResultSet rs = stmt.executeQuery()) {

					while (rs.next()) {
						ArrayList<String> row = new ArrayList<>();
						for (FieldModel field : fields) {
							row.add(rs.getString(field.getName()));
						}
						columnData.add(row);
						if (columnData.size() > 100)
							break;
					}
				}
			}
			ret.setColumnData(columnData);
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}

		return ret;
	}

	public TableModel getTable(String id) {
		TableModel ret = gson.fromJson(getNodeModel(id).getJsonData(), TableModel.class);
		return ret;
	}

	public TableModel getTableByName(String db_fk, String name, boolean showExpired) {
		TableModel ret = null;
		List<NodeModel> t = this.findNodesModelByParentId_Name(db_fk, TableModel.class.getName(), name, showExpired);
		if (t.size() > 0)
			ret = gson.fromJson(t.get(0).getJsonData(), TableModel.class);
		return ret;
	}

	public List<TableModel> getTables(String fk, Boolean showExpired) {
		List<TableModel> ret = new ArrayList<>();
		Collection<NodeModel> nodes = findNodesModelByParentId_Name(fk, TableModel.class.getName(), showExpired);
		for (NodeModel n : nodes) {
			ret.add(gson.fromJson(n.getJsonData(), TableModel.class));
		}
		return ret;
	}

	private String makeGUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public void updateAuditLog(AuditLogModel auditLogModel) {
		if (true)
			return;
		String sql = "INSERT INTO audit_log" + "(id," + "user," + "object," + "action," + "entereddate)" + "VALUES"
				+ "(?," + "?," + "?," + "?," + "?)";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement updateStmt = con.prepareStatement(sql)) {
				int i = 1;
				updateStmt.setString(i++, this.makeGUID());
				updateStmt.setString(i++, auditLogModel.getUser());
				updateStmt.setString(i++, auditLogModel.getObject());
				updateStmt.setString(i++, auditLogModel.getAction());
				updateStmt.setLong(i++, System.currentTimeMillis());
				updateStmt.execute();
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}

	}

	public DataSourceModel updateDataSource(DataSourceModel dbModel) {
		// flat to nodes
		NodeModel nodeModel = new NodeModel();
		if (dbModel.getId() == null) {
			dbModel.setEffectiveDate(System.currentTimeMillis());
			dbModel.setEnteredDate(System.currentTimeMillis());
			dbModel.setId(makeGUID());
			nodeModel.setEnteredDate(System.currentTimeMillis());
			nodeModel.setEffectiveDate(System.currentTimeMillis());
		} else {
			if (!repoHelper.getUser(request).equalsIgnoreCase("scanner")) {
				dbModel.setChangedBy(repoHelper.getUser(request));
			}
			dbModel.setChangedDate(System.currentTimeMillis());
		}

		nodeModel.setId(dbModel.getId());
		nodeModel.setName(dbModel.getDs_name());
		nodeModel.setNodeType(DataSourceModel.class.getName());
		nodeModel.setJsonData(gson.toJson(dbModel));

		dbModel = gson.fromJson(this.updateNode(nodeModel).getJsonData(), DataSourceModel.class);
		return dbModel;
	}

	public FieldModel updateField(FieldModel fModel) {
		NodeModel nodeModel = new NodeModel();
		if (fModel.getId() == null) {
			fModel.setEffectiveDate(System.currentTimeMillis());
			fModel.setEnteredDate(System.currentTimeMillis());
			fModel.setId(makeGUID());
		} else {
			if (!repoHelper.getUser(request).equalsIgnoreCase("scanner"))
				fModel.setChangedBy(repoHelper.getUser(request));
			fModel.setChangedDate(System.currentTimeMillis());
		}
		nodeModel.setId(fModel.getId());
		nodeModel.setName(fModel.getName());
		nodeModel.setNodeType(FieldModel.class.getName());
		nodeModel.setParent_id(fModel.getTable_def_id_fk());
		nodeModel.setJsonData(gson.toJson(fModel));
		fModel = gson.fromJson(this.updateNode(nodeModel).getJsonData(), FieldModel.class);
		return fModel;
	}

	public NodeModel updateNode(NodeModel nodeModel) {
		NodeModel ret = nodeModel;
		if (nodeModel.getEnteredBy() == null)
			nodeModel.setEnteredBy(repoHelper.getUser(request));
		if (!repoHelper.getUser(request).equalsIgnoreCase("scanner"))
			nodeModel.setChangedBy(repoHelper.getUser(request));

		String sql = "";

		int updateRowCount = 0;
		sql = "UPDATE objects" + " SET" + " nodeType = ?," + "uicontrol = ?," + "parent_id = ?," + "related_id = ?,"
				+ "relationshiptype = ?," + "name = ?," + "description = ?," + "jsondata = ?," + "effectivedate = ?,"
				+ "expirationdate = ?," + "enteredby = ?," + "changedby = ?," + "entereddate = ?," + "changeddate = ?"
				+ " WHERE id = ?";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();

			try (PreparedStatement updateStmt = con.prepareStatement(sql)) {
				int i = 1;
				updateStmt.setString(i++, nodeModel.getNodeType());
				updateStmt.setString(i++, nodeModel.getUiControl());
				updateStmt.setString(i++, nodeModel.getParent_id());
				updateStmt.setString(i++, nodeModel.getRelated_id());
				updateStmt.setString(i++, nodeModel.getRelationshipType());
				updateStmt.setString(i++, nodeModel.getName());
				updateStmt.setString(i++, nodeModel.getDescription());
				updateStmt.setString(i++, nodeModel.getJsonData());
				if (nodeModel.getEffectiveDate() == null)
					updateStmt.setNull(i++, Types.BIGINT);
				else
					updateStmt.setLong(i++, nodeModel.getEffectiveDate());

				if (nodeModel.getExpirationDate() == null)
					updateStmt.setNull(i++, Types.BIGINT);
				else
					updateStmt.setLong(i++, nodeModel.getExpirationDate());
				updateStmt.setString(i++, nodeModel.getEnteredBy());
				updateStmt.setString(i++, nodeModel.getChangedBy());
				if (nodeModel.getEnteredDate() == null)
					updateStmt.setNull(i++, Types.BIGINT);
				else
					updateStmt.setLong(i++, nodeModel.getEnteredDate());
				updateStmt.setLong(i++, System.currentTimeMillis());
				updateStmt.setString(i++, nodeModel.getId());
				updateRowCount = updateStmt.executeUpdate();
				updateAuditLog(new AuditLogModel("table_def", "TODO", "Update " + nodeModel.getId()));
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}

		// insert if needed
		if (updateRowCount == 0) {
			sql = "INSERT INTO objects" + "(id," + "nodeType," + "uicontrol," + "parent_id," + "related_id,"
					+ "relationshiptype," + "name," + "description," + "jsondata," + "effectivedate,"
					+ "expirationdate," + "enteredby," + "changedby," + "entereddate)" + "VALUES"
					+ "(?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?," + "?"
					+ ");";
			try {
				con = dataSource.getDS().getConnection();

				try (PreparedStatement updateStmt = con.prepareStatement(sql)) {
					int i = 1;
					updateStmt.setString(i++, nodeModel.getId());
					updateStmt.setString(i++, nodeModel.getNodeType());
					updateStmt.setString(i++, nodeModel.getUiControl());
					updateStmt.setString(i++, nodeModel.getParent_id());
					updateStmt.setString(i++, nodeModel.getRelated_id());
					updateStmt.setString(i++, nodeModel.getRelationshipType());
					updateStmt.setString(i++, nodeModel.getName());
					updateStmt.setString(i++, nodeModel.getDescription());
					updateStmt.setString(i++, nodeModel.getJsonData());
					updateStmt.setLong(i++, System.currentTimeMillis());
					if (nodeModel.getExpirationDate() == null)
						updateStmt.setNull(i++, Types.BIGINT);
					else
						updateStmt.setLong(i++, nodeModel.getExpirationDate());
					updateStmt.setString(i++, nodeModel.getEnteredBy());
					updateStmt.setString(i++, nodeModel.getChangedBy());
					updateStmt.setLong(i++, System.currentTimeMillis());

					updateStmt.execute();
					updateAuditLog(new AuditLogModel("table_def", "TODO", "Insert " + nodeModel.getId()));
				}
			} catch (SQLException e) {
				log.error("SQLException", e);
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}

		return ret;
	}

	public void updateScanHistory(DataSourceModel dbModel) {
		String sql = "";
		sql = "INSERT INTO scan_history" + "(id," + "data_source_fk," + "scannedby," + "entereddate)" + "VALUES" + "(?,"
				+ "?," + "?," + "?)";
		Connection con = null;
		try {
			con = dataSource.getDS().getConnection();
			try (PreparedStatement updateStmt = con.prepareStatement(sql)) {
				int i = 1;
				updateStmt.setString(i++, this.makeGUID());
				updateStmt.setString(i++, dbModel.getId());
				updateStmt.setString(i++, dbModel.getOwner());
				updateStmt.setLong(i++, System.currentTimeMillis());
				updateStmt.execute();
			}
		} catch (SQLException e) {
			log.error("SQLException", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}

	}

	public TableModel updateTable(TableModel tModel) {
		NodeModel nodeModel = new NodeModel();
		if (tModel.getId() == null) {
			tModel.setEffectiveDate(System.currentTimeMillis());
			tModel.setEnteredDate(System.currentTimeMillis());
			tModel.setId(makeGUID());
		} else {
			if (!repoHelper.getUser(request).equalsIgnoreCase("scanner"))
				tModel.setChangedBy(repoHelper.getUser(request));
			tModel.setChangedDate(System.currentTimeMillis());
		}
		nodeModel.setId(tModel.getId());
		nodeModel.setName(tModel.getName());
		nodeModel.setNodeType(TableModel.class.getName());
		nodeModel.setParent_id(tModel.getData_source_id_fk());
		nodeModel.setJsonData(gson.toJson(tModel));
		tModel = gson.fromJson(this.updateNode(nodeModel).getJsonData(), TableModel.class);
		return tModel;
	}

	@Override
	public void close() {
		cache.cleanUp();
		cache = null;
		try {
			dataSource.getDS().getConnection().close();
		} catch (SQLException e) {
		}
		
	}

}
