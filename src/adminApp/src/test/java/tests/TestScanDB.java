package tests;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ackdev.DataDictionary.beans.ScanDS;
import com.ackdev.DataDictionary.repository.Repository;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.TableModel;

import TestState.myState;
import testUtils.Helpers;

public class TestScanDB {
	Repository repo = null;

	@Before
	public void setUp() {
		repo = Repository.getInstance();
	}

	@After
	public void tearDown() {
		Helpers.clearDS();
		repo.close();
	}


	@Test
	public void test1ScanDB() {

		DataSourceModel dataSourceModel = new DataSourceModel();
		dataSourceModel.setDescription("Junit test description");
		dataSourceModel.setDriver("com.mysql.cj.jdbc.Driver");
		dataSourceModel
				.setDSN("jdbc:mysql://localhost:3306/nfp?useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false");
		dataSourceModel.setUsername("demouser");
		dataSourceModel.setPassword("demopassword");
		dataSourceModel.setDs_name("JUnit Test");
		dataSourceModel.setOwner("JUNIT");
		dataSourceModel.setTags("tag1,tag2");
		ScanDS scanDB = ScanDS.getInstance(false);
		DataSourceModel eval = scanDB.doScan(dataSourceModel);
		DataSourceModel eval2 = repo.getDataSource(eval.getId());
		Assert.assertTrue(eval2 != null);
		Collection<TableModel> tableModels = repo.getTables(eval2.getId(), false);
		Assert.assertTrue(tableModels.size() == 3);
		Collection<FieldModel> fieldModels = repo.getFieldsByDS(eval2.getId(), false);
		Assert.assertTrue(fieldModels.size() == 278);
		// Lets see how the code coverage tools report this:
		String mapCompare = Helpers.mapCompare(eval2, dataSourceModel);
		Assert.assertTrue(mapCompare.startsWith("not equal: only on left={changedDate="));
		dataSourceModel.setId(eval2.getId());
		dataSourceModel.setChangedDate(eval2.getChangedDate());
		dataSourceModel.setChangedBy(eval2.getChangedBy());
		Assert.assertTrue(Helpers.objDBCompare(eval2, dataSourceModel));
		myState.dsID = eval2.getId();
	}

}
