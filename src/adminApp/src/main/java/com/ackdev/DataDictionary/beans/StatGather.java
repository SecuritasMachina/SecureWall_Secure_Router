package com.ackdev.DataDictionary.beans;

import javax.servlet.http.HttpServletRequest;

import com.ackdev.DataDictionary.repository.IndexRepository;
import com.ackdev.DataDictionary.repository.Repository;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.TableModel;
import com.ackdev.common.reportingModels.DBObjectSummary;

public class StatGather {
	private static StatGather _instance = null;

	public static StatGather getInstance(boolean _isTest, HttpServletRequest request) {
		if (_instance == null)
			_instance = new StatGather(_isTest);
		return _instance;
	}

	private IndexRepository indexRepo = null;
	private Repository repo = null;
	
	private StatGather(boolean _isTest) {
		indexRepo = IndexRepository.getInstance(_isTest);
		repo=Repository.getInstance();
	}
	
	public DBObjectSummary getCounts() {
		DBObjectSummary ret = new DBObjectSummary();
		ret.setDSCount(repo.getCountByType(DataSourceModel.class.getName()));
		ret.setTableCount(repo.getCountByType(TableModel.class.getName()));
		ret.setFieldCount(repo.getCountByType(FieldModel.class.getName()));
		ret.setAvailableCount(indexRepo.getAvailableCount());
		
		return ret;
	}


}
