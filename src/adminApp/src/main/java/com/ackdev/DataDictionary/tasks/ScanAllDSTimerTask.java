package com.ackdev.DataDictionary.tasks;

import java.util.Collection;
import java.util.TimerTask;

import com.ackdev.DataDictionary.beans.ScanDS;
import com.ackdev.DataDictionary.repository.Repository;
import com.ackdev.common.model.DataSourceModel;

public class ScanAllDSTimerTask extends TimerTask {

	@Override
	public void run() {
		Repository repo = Repository.getInstance();
		Collection<DataSourceModel> dsCol = repo.getDataSources(false);
		for (DataSourceModel dbModel : dsCol) {
			ScanDS.getInstance(false).doScan(dbModel);
		}
	}

}
