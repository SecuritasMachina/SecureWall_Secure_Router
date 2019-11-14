package tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ackdev.DataDictionary.URLController;
import com.ackdev.DataDictionary.repository.RepositoryHelper;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldData;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.TableModel;
import com.ackdev.common.reportingModels.DBObjectSummary;
import com.google.gson.Gson;

import TestState.myState;
import testUtils.MyReflectionTestUtils;

public class TestURLController {
	private static Gson gson = new Gson();
	URLController urlController = new URLController();


	@Test
	public void testGetDataSource() {
		DataSourceModel testRet2 = urlController.getDataSource(myState.dsID, null);
		Assert.assertTrue(testRet2.getDs_name() != null);
	}

	@Test
	public void testGetDataSources() {
		Collection<DataSourceModel> testRet = urlController.getDataSources(false, null);
		Assert.assertTrue(testRet.size() > 2);
	}

	@Test
	public void testGetField() {
		Assert.assertTrue(true);
	}

	public void testGetAllFields() {

		// TODO Future feature

		Collection<FieldModel> fieldModels = urlController.getAllFields(false, null);
		Assert.assertTrue(fieldModels.size() == 588);
		fieldModels.forEach((f) -> {

			FieldModel fieldModel = urlController.getField(f.getId(), null);
			String j21 = gson.toJson(f);
			String j22 = gson.toJson(fieldModel);
			Assert.assertTrue(j21.equals(j22));

		});

	}

	@Test
	public void testGetFields() {

		Collection<TableModel> tableModels = urlController.getTables(myState.dsID, false, null);

		// TODO compare performance of lamba vs for next, doesn't matter for UTs
		// TODO make dedicated test DB
		Assert.assertTrue(tableModels.size() == 3);
		tableModels.forEach((tm) -> {
			TableModel tableModel = urlController.getTable(tm.getId(), null);
			String j1 = gson.toJson(tm);
			String j2 = gson.toJson(tableModel);
			Assert.assertTrue(j1.equals(j2));

			Collection<FieldModel> fieldModels = urlController.getFields(tm.getId(), false, null);
//			System.out.println(tm.getName() + ":" + fieldModels.size());
			if (tm.getName().equals("filing"))
				Assert.assertTrue(fieldModels.size() == 246);
			if (tm.getName().equals("nfp_epostcard"))
				Assert.assertTrue(fieldModels.size() == 26);
			if (tm.getName().equals("pub78"))
				Assert.assertTrue(fieldModels.size() == 6);
			fieldModels.forEach((f) -> {

				FieldModel fieldModel = urlController.getField(f.getId(), null);
				String j21 = gson.toJson(f);
				String j22 = gson.toJson(fieldModel);
				Assert.assertTrue(j21.equals(j22));

			});

		});
	}

	@Test
	public void testGetFieldSearch() {
		helperStartIndexing();
		Collection<FieldModel> fieldModels = urlController.getFieldSearch("json:city", false, null);
		Assert.assertTrue(fieldModels.size() > 2);
		fieldModels.forEach((f) -> {
			FieldModel fieldModel = urlController.getField(f.getId(), null);
			String j21 = gson.toJson(f);
			String j22 = gson.toJson(fieldModel);
			Assert.assertTrue(j21.equals(j22));
		});
	}

	@Test
	public void testGetPreviewTableFields() {
		// TODO not a feature yet
	}

	@Test
	public void testGetPreviewDSFields() {
		// TODO not a feature yet
	}

	@Test
	public void testGetCounts() {
		DBObjectSummary countsObj = urlController.getCounts(null);
		Assert.assertTrue(countsObj.getAvailableCount() == null);
		Assert.assertTrue(countsObj.getConfidentialCount() == null);
		Assert.assertTrue(countsObj.getIntegrityCount() == null);
		Assert.assertTrue(countsObj.getDSCount() > 0);
		Assert.assertTrue(countsObj.getFieldCount() > 587);
		Assert.assertTrue(countsObj.getTableCount() > 2);

	}

	@Test
	public void testGetPreviewField() {
		int totalRead = 0;
		Collection<FieldModel> fieldModels = urlController.getFieldsByDS(myState.dsID, false, null);
		Assert.assertTrue(fieldModels.size() > 0);

		for (FieldModel fm : fieldModels) {
			if (fm.getName().equalsIgnoreCase("EIN") || fm.getName().equalsIgnoreCase("company_name")) {
				Collection<FieldData> fv = urlController.getPreviewField(fm.getId(), null);
				totalRead += fv.size();
				Assert.assertTrue(fv.size() > 0);
			}
		}
		Assert.assertTrue(totalRead > 15);

	}

	@Test
	public void testGetTable() {
		Assert.assertTrue(true); // covered elsewhere
	}

	@Test
	public void testGetTables() {
		Assert.assertTrue(true); // covered elsewhere
	}

	@Test
	public void testSaveField() {
		int count = 0;
		RepositoryHelper.getInstance().setTestUserName("testUserName");
		Collection<FieldModel> fields = urlController.getFieldsByDS(myState.dsID, false, null);
		for (FieldModel field : fields) {
			FieldModel fieldModelCopy = urlController.getField(field.getId(), null);
			List<String> ignoreFields = new ArrayList<>();
			ignoreFields.add("changedDate");
			ignoreFields.add("changedBy");
			ignoreFields.add("id");
			MyReflectionTestUtils.setSetters(field, ignoreFields);
			urlController.saveField(field, null);
			// field.setName("asdsadsa");
			FieldModel field2 = urlController.getField(field.getId(), null);
			List<String> myList = new ArrayList<>();
			myList.add("changedDate");
			myList.add("changedBy");
			List<String> evalList = MyReflectionTestUtils.compareGetters(field, field2, myList);
			evalList.forEach((e) -> {
				Assert.assertTrue(e, false);
			});
			RepositoryHelper.getInstance().setTestUserName(null);
			// Restore original
			urlController.saveField(fieldModelCopy, null);
			count++;
			if (count > 10)
				break;
		}
		;

	}

	@Test
	public void testSaveDS() {
		RepositoryHelper.getInstance().setTestUserName("testUserName");
		DataSourceModel dataSourceModel = urlController.getDataSource(myState.dsID, null);
		DataSourceModel dataSourceModelCopy = urlController.getDataSource(myState.dsID, null);
		List<String> ignoreFields = new ArrayList<>();
		ignoreFields.add("changedDate");
		ignoreFields.add("changedBy");
		ignoreFields.add("id");
		MyReflectionTestUtils.setSetters(dataSourceModel, ignoreFields);
		urlController.saveDS(dataSourceModel, null);
		DataSourceModel dataSourceModel2 = urlController.getDataSource(myState.dsID, null);
		List<String> myList = new ArrayList<>();
		myList.add("changedDate");
		myList.add("changedBy");
		List<String> evalList = MyReflectionTestUtils.compareGetters(dataSourceModel, dataSourceModel2, myList);
		evalList.forEach((e) -> {
			Assert.assertTrue(e, false);
		});
		RepositoryHelper.getInstance().setTestUserName(null);
		// Restore original
		urlController.saveDS(dataSourceModelCopy, null);
	}

	@Test
	public void testSaveTable() {
		RepositoryHelper.getInstance().setTestUserName("testUserName");
		Collection<TableModel> tables = urlController.getTables(myState.dsID, false, null);
		tables.forEach((table) -> {
			TableModel TableModelCopy = urlController.getTable(table.getId(), null);
			List<String> ignoreFields = new ArrayList<>();
			ignoreFields.add("changedDate");
			ignoreFields.add("changedBy");
			ignoreFields.add("id");
			MyReflectionTestUtils.setSetters(table, ignoreFields);
			urlController.saveTable(table, null);
			TableModel tableModel2 = urlController.getTable(table.getId(), null);
			List<String> myList = new ArrayList<>();
			myList.add("changedDate");
			myList.add("changedBy");
			List<String> evalList = MyReflectionTestUtils.compareGetters(table, tableModel2, myList);
			evalList.forEach((e) -> {
				Assert.assertTrue(e, false);
			});
			RepositoryHelper.getInstance().setTestUserName(null);
			// Restore original
			urlController.saveTable(TableModelCopy, null);
		});
	}

	@Test
	public void testScanDS() {
		Assert.assertTrue(true); // covered elsewhere
	}

	@Test
	public void testResetIndexes() {
//		Collection<FieldModel> fieldModels = urlController.getFieldSearch("json:city", false, null);
//		Assert.assertTrue(fieldModels.size() > 3);

		urlController.resetIndexes();
		Collection<FieldModel> fieldModels = urlController.getFieldSearch("json:city", false, null);
		Assert.assertTrue(fieldModels.size() == 0);

	}

	public void helperStartIndexing() {
		urlController.resetIndexes();
		Collection<FieldModel> fieldModels = urlController.getFieldSearch("json:city", false, null);
		Assert.assertTrue(fieldModels.size() == 0);
		urlController.startIndexing();
		// TODO eval if a hook is useful vs kuldge

		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException i) {
		}
		fieldModels = urlController.getFieldSearch("json:city", false, null);
		Assert.assertTrue("Found:" + fieldModels.size(),fieldModels.size() > 1);

	}

	@Test
	public void testDeleteDataSource() {

		DataSourceModel dataSourceModelCopy = urlController.getDataSource(myState.dsID, null);
		urlController.deleteDataSource(myState.dsID, null);
		Assert.assertTrue(urlController.getDataSource(myState.dsID, null).getExpirationDate() != null);
		urlController.saveDS(dataSourceModelCopy, null);
		Assert.assertTrue(urlController.getDataSource(myState.dsID, null) != null);
	}

	@Test
	public void testDeleteField() {
		int count = 0;
		Collection<FieldModel> fields = urlController.getFieldsByDS(myState.dsID, false, null);
		for (FieldModel field : fields) {
			urlController.deleteField(field.getId(), null);
			Assert.assertTrue(urlController.getField(field.getId(), null).getExpirationDate() != null);
			urlController.saveField(field, null);
			Assert.assertTrue(urlController.getField(field.getId(), null).getExpirationDate() == null);
			count++;
			if (count > 10)
				break;
		}
		;
	}

	@Test
	public void testDeleteTable() {
		Collection<TableModel> tables = urlController.getTables(myState.dsID, false, null);
		tables.forEach((table) -> {
			urlController.deleteTable(table.getId(), null);
			Assert.assertTrue(urlController.getTable(table.getId(), null).getExpirationDate() != null);
			urlController.saveTable(table, null);
			Assert.assertTrue(urlController.getTable(table.getId(), null).getExpirationDate() == null);

		});
	}

}
