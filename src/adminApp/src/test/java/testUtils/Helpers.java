package testUtils;

import java.lang.reflect.Type;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import com.ackdev.common.model.DataSourceModel;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Helpers {
	private static Gson gson = new Gson();

	public static void setupDS() {
		SimpleNamingContextBuilder builder = null;

		BasicDataSource myPool = new BasicDataSource();
		myPool.setDriverClassName("com.mysql.cj.jdbc.Driver");
		myPool.setUsername("demouser");
		myPool.setPassword("demopassword");
		myPool.setUrl(
				"jdbc:mysql://localhost:3306/data_dictionary?useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false");
		myPool.setMaxActive(10);
		myPool.setMaxIdle(5);
		myPool.setInitialSize(5);
		myPool.setValidationQuery("SELECT 1");
		BasicDataSource ds = myPool;
		builder = new SimpleNamingContextBuilder();
		builder.bind("java:/comp/env/jdbc/AckDevDataDictionary", ds);

		//
		try {
			builder.activate();
		} catch (IllegalStateException e) {
		} catch (NamingException e) {
		}

		
	}

	public static void clearDS() {
		try {
			SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String mapCompare(DataSourceModel eval2, DataSourceModel dataSourceModel) {
		String j1 = gson.toJson(eval2);
		String j2 = gson.toJson(dataSourceModel);
		Gson g = new Gson();
		Type mapType = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> firstMap = g.fromJson(j1, mapType);
		Map<String, Object> secondMap = g.fromJson(j2, mapType);
		return Maps.difference(firstMap, secondMap).toString();
	}

	public static boolean objDBCompare(DataSourceModel eval2, DataSourceModel dataSourceModel) {
		String j1 = gson.toJson(eval2);
		String j2 = gson.toJson(dataSourceModel);

		return j1.equals(j2);
	}
}
