package com.ackdev.DataDictionary.tasks;

import com.ackdev.DataDictionary.beans.ScanDS;
import com.ackdev.common.model.DataSourceModel;

public class ScanDSRunner extends Thread  {
	private DataSourceModel dbModel;

	public ScanDSRunner(DataSourceModel dbModel) {
		this.dbModel = dbModel;
	}

	@Override
	public void run() {
		ScanDS.getInstance(false).doScan(dbModel);
	}

}
