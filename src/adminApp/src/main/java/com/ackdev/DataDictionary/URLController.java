package com.ackdev.DataDictionary;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ackdev.DataDictionary.beans.StatGather;
import com.ackdev.DataDictionary.repository.IndexRepository;
import com.ackdev.DataDictionary.repository.Repository;
import com.ackdev.DataDictionary.tasks.ScanDSRunner;
import com.ackdev.common.model.DataSourceModel;
import com.ackdev.common.model.FieldData;
import com.ackdev.common.model.FieldModel;
import com.ackdev.common.model.TableFields;
import com.ackdev.common.model.TableModel;
import com.ackdev.common.reportingModels.DBObjectSummary;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/rest")
public class URLController {
	//TODO add search by DS
	//TODO add search by table
	private static final Logger log = Logger.getLogger(URLController.class);

	private IndexRepository indexRepository = null;

	public URLController() {
		indexRepository = IndexRepository.getInstance(false);
	}

	@DeleteMapping("/dataSource/{id}")
	public void deleteDataSource(@PathVariable String id, HttpServletRequest request) {

		Repository.getInstance(request).deleteDataSource(id);
	}

	@DeleteMapping("/field/{id}")
	public void deleteField(@PathVariable String id, HttpServletRequest request) {

		Repository.getInstance(request).deleteField(id);
	}

	@DeleteMapping("/table/{id}")
	public void deleteTable(@PathVariable String id, HttpServletRequest request) {

		Repository.getInstance(request).deleteTable(id);
	}

	@GetMapping("/dataSource/{fk}")
	public DataSourceModel getDataSource(@PathVariable String fk, HttpServletRequest request) {

		return Repository.getInstance(request).getDataSource(fk);
	}

	@GetMapping("/dataSources/{showExpired}")
	public Collection<DataSourceModel> getDataSources(@PathVariable Boolean showExpired, HttpServletRequest request) {

		return Repository.getInstance(request).getDataSources(showExpired);
	}

	@GetMapping("/field/{id}")
	public FieldModel getField(@PathVariable String id, HttpServletRequest request) {

		return Repository.getInstance(request).getField(id);
	}

	@GetMapping("/fields/{fk}/{showExpired}")
	public Collection<FieldModel> getFields(@PathVariable String fk, @PathVariable Boolean showExpired,
			HttpServletRequest request) {

		return Repository.getInstance(request).getFields(fk, showExpired);
	}

	@GetMapping("/fieldSearch/{parms}/{showExpired}")
	public Collection<FieldModel> getFieldSearch(@PathVariable String parms, @PathVariable Boolean showExpired,
			HttpServletRequest request) {

		return IndexRepository.getInstance(false).searchFields(parms, showExpired);
	}

	@GetMapping("/fieldsByDS/{fk}/{showExpired}")
	public Collection<FieldModel> getFieldsByDS(@PathVariable String fk, @PathVariable Boolean showExpired,
			HttpServletRequest request) {
		return Repository.getInstance(request).getFieldsByDS(fk, showExpired);
	}

	@GetMapping("/allFields/{showExpired}")
	public Collection<FieldModel> getAllFields(@PathVariable Boolean showExpired, HttpServletRequest request) {
		return Repository.getInstance(request).getAllFields(showExpired);
	}

	@GetMapping("/previewTableFields/{id}")
	public TableFields getPreviewTableFields(@PathVariable String id, HttpServletRequest request) {
		return Repository.getInstance(request).getPreviewTableFields(id);
	}

//	@GetMapping("/previewDSFields/{id}")
//	public Collection<FieldData> getPreviewDSFields(@PathVariable String id, HttpServletRequest request) {
//		return Repository.getInstance(request).getPreviewDSFields(id);
//	}

	@GetMapping("/getCounts")
	public DBObjectSummary getCounts(HttpServletRequest request) {
		return StatGather.getInstance(false, request).getCounts();
	}

	@GetMapping("/previewField/{id}")
	public Collection<FieldData> getPreviewField(@PathVariable String id, HttpServletRequest request) {

		return Repository.getInstance(request).getPreviewField(id);
	}

	@GetMapping("/table/{tableId}")
	public TableModel getTable(@PathVariable String tableId, HttpServletRequest request) {
		return Repository.getInstance(request).getTable(tableId);
	}

	@GetMapping("/tables/{fk}/{showExpired}")
	public Collection<TableModel> getTables(@PathVariable String fk, @PathVariable Boolean showExpired,
			HttpServletRequest request) {

		return Repository.getInstance(request).getTables(fk, showExpired);
	}

	@PostMapping(path = "/dataSource", consumes = "application/json", produces = "application/json")
	public DataSourceModel saveDS(@RequestBody DataSourceModel dataSourceModel, HttpServletRequest request) {

		return Repository.getInstance(request).updateDataSource(dataSourceModel);
	}

	@PostMapping(path = "/field", consumes = "application/json", produces = "application/json")
	public void saveField(@RequestBody FieldModel fieldModel, HttpServletRequest request) {

		Repository.getInstance(request).updateField(fieldModel);
	}

	@PostMapping(path = "/table", consumes = "application/json", produces = "application/json")
	public void saveTable(@RequestBody TableModel tableModel, HttpServletRequest request) {

		Repository.getInstance(request).updateTable(tableModel);
	}

	@PostMapping(path = "/scanDS", consumes = "application/json", produces = "application/json")
	public void scanDS(@RequestBody DataSourceModel dataSourceModel, HttpServletRequest request) {
		log.info("Initializing thread for " + dataSourceModel.getDs_name());
		Thread thread = new ScanDSRunner(dataSourceModel);
		thread.start();
		log.info("Starting thread for " + dataSourceModel.getDs_name());

	}

	@GetMapping("/util/index/reset")
	public void resetIndexes() {
		indexRepository.getInstance(false).resetIndexes();
	}

	@GetMapping("/util/index/start")
	public void startIndexing() {
		indexRepository.getInstance(false).startIndexing();
	}

}
