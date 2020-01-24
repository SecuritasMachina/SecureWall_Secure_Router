package com.ackdev.DataDictionary.repository;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ackdev.DataDictionary.RequestStore;
import com.ackdev.common.DaoTools;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.NodeModel;
import com.ackdev.common.model.TableModel;

public class RepositoryHelper {
	private static boolean suppressError = false;


	private String testUserName;

	private static RepositoryHelper _instance = null;

	public static RepositoryHelper getInstance() {
		if (_instance == null)
			_instance = new RepositoryHelper();
		return _instance;
	}

	private RepositoryHelper() {

	}

	private static Logger log = Logger.getLogger(RepositoryHelper.class);

	public static NodeModel copyRStoDM(ResultSet rs) {

		NodeModel ret = new NodeModel();
		try {
			ret.setId(rs.getString("id"));
			ret.setNodeType(rs.getString("nodeType"));
			ret.setParent_id(rs.getString("parent_id"));
			ret.setRelated_id(rs.getString("related_id"));
			ret.setDescription(rs.getString("description"));
			ret.setRelationshipType(rs.getString("relationshiptype"));
			ret.setName(rs.getString("name"));
			ret.setDescription(rs.getString("description"));
			ret.setJsonData(rs.getString("jsondata"));
			ret.setRelationshipType(rs.getString("relationshiptype"));
			ret.setEnteredBy(rs.getString("enteredby"));
			ret.setChangedBy(rs.getString("changedby"));
			ret.setEnteredDate(rs.getLong("entereddate"));
			ret.setEffectiveDate(rs.getLong("effectivedate"));
			ret.setChangedDate(DaoTools.getLong(rs, "changeddate"));
			ret.setExpirationDate(DaoTools.getLong(rs, "expirationdate"));
		} catch (SQLException e) {
			log.error("SQLException", e);
		}
		return ret;
	}

	public static DataSourceModel copyRStoDSM(ResultSet rs) {
		DataSourceModel ret = new DataSourceModel();
		try {
			ret.setId(rs.getString("id"));
			ret.setDs_name(rs.getString("ds_name"));
			ret.setDSN(rs.getString("DSN"));
			ret.setUsername(rs.getString("username"));
			ret.setPassword(rs.getString("password"));
			ret.setDriver(rs.getString("driver"));
			ret.setVersion(rs.getString("version"));
			ret.setDescription(rs.getString("description"));
			ret.setTags(rs.getString("tags"));
			ret.setOwner(rs.getString("owner"));
			ret.setEffectiveDate(rs.getLong("effectivedate"));
			ret.setChangedDate(DaoTools.getLong(rs, "changeddate"));
			ret.setExpirationDate(DaoTools.getLong(rs, "expirationdate"));
		} catch (SQLException e) {
			log.error("SQLException", e);
		}
		return ret;
	}

	public static FieldModel copyRStoFieldModel(ResultSet rs) {
		FieldModel ret = new FieldModel();
		try {
			ret.setId(rs.getString("id"));
			ret.setTable_def_id_fk(rs.getString("table_def_id_fk"));
			ret.setName(rs.getString("name"));
			try {
				ret.setTableName(rs.getString("tablename"));
			} catch (Exception ignore) {
			}
			ret.setDescription(rs.getString("description"));
			ret.setDbdescription(rs.getString("dbdescription"));
			ret.setDefaultvalue(rs.getString("defaultvalue"));
			ret.setSize(rs.getInt("size"));
			ret.setType(rs.getString("type"));
			ret.setIsautoincrement(rs.getBoolean("isautoincrement"));
			ret.setIsprimarykey(rs.getBoolean("isprimarykey"));
			ret.setIsrequired(rs.getBoolean("isrequired"));
			ret.setEffectiveDate(rs.getLong("effectivedate"));
			ret.setChangedDate(DaoTools.getLong(rs, "changeddate"));
			ret.setExpirationDate(DaoTools.getLong(rs, "expirationdate"));
			ret.setIntegrity_control(rs.getString("integrity_control"));
			ret.setConfidentiality_control(rs.getString("confidentiality_control"));
			ret.setAvailability_control(rs.getString("availability_control"));

		} catch (SQLException e) {
			log.error("SQLException", e);
		}
		return ret;
	}

	public static TableModel copyRStoTableModel(ResultSet rs) {
		TableModel ret = new TableModel();
		try {
			ret.setId(rs.getString("id"));
			ret.setData_source_id_fk(rs.getString("data_source_id_fk"));
			ret.setName(rs.getString("name"));
			ret.setRowCount(rs.getLong("rowCount"));
			ret.setDescription(rs.getString("description"));
			ret.setTags(rs.getString("tags"));
			ret.setEffectiveDate(rs.getLong("effectivedate"));
			ret.setChangedDate(DaoTools.getLong(rs, "changeddate"));
			ret.setExpirationDate(DaoTools.getLong(rs, "expirationdate"));
		} catch (SQLException e) {
			log.error("SQLException", e);
		}
		return ret;
	}

	public String getUser(HttpServletRequest requestStore) {
		String ret = "scanner";
		if (this.testUserName != null)
			ret = this.testUserName;
		if (requestStore == null) {
			if (!suppressError) {
				log.info("requestStore is null!, no authorization available");
				suppressError = true;
			}
			return ret;
		}
		Principal p = requestStore.getUserPrincipal();
		if (p != null) {
			// log.debug("PrincipalUser : " + p.getName());
			ret = p.getName();
		} else {
			log.debug("PrincipalUser is null");
			log.debug("request.getRemoteUser : " + requestStore.getRemoteUser());
		}
		return ret;
	}

	public void setTestUserName(String testUserName) {
		this.testUserName = testUserName;
	}
}
