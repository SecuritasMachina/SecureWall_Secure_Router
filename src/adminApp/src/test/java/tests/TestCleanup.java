package tests;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ackdev.DataDictionary.repository.Repository;
import com.ackdev.common.model.DataSourceModel;

public class TestCleanup {
	private Logger log = Logger.getLogger(TestCleanup.class);

	@Test
	public void cleanUp() {
		Repository repo = Repository.getInstance();
		Collection<DataSourceModel> dataSourceModels = repo.getDataSources(false);
		for (DataSourceModel ds : dataSourceModels) {
			if (ds.getDs_name().equalsIgnoreCase("JUnit Test")) {
				log.debug("delete DS: " + ds.getDs_name() + ":" + ds.getId());
				repo.deleteDataSource(ds.getId());
			}
		}
		//repo.close();
	}
}
