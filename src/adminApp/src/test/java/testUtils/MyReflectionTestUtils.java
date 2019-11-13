package testUtils;

import java.beans.Beans;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Class that utilizes reflection to help in testing
 * 
 * @author bchild
 * 
 */
public class MyReflectionTestUtils {

	private static final String NOT_EQUALS = "%s: [ %s ] != [ %s ]";
	private static final String REFLECT_ERROR = "%s: ERROR -> %s";

	@SuppressWarnings("rawtypes")
	public static void setSetters(Object object, List<String> ignoreFields) {

		if (ignoreFields == null) {
			ignoreFields = new ArrayList<String>();
		}
		ignoreFields.add("class");

		List<Method> setters = new ArrayList<Method>();

		// get getters
		Method[] methods = object.getClass().getMethods();
		Integer inte = 0;
		for (Method method : methods) {
			if (isSetter(method) && !listContainsString(ignoreFields, method.getName())) {
				setters.add(method);

				try {
					if (method.getParameterTypes()[0].equals(Integer.class)) {
						method.invoke(object, new Random().nextInt());
					} else if (method.getParameterTypes()[0].equals(Boolean.class)) {
						method.invoke(object, true);
					} else if (method.getParameterTypes()[0].equals(Long.class)) {
						method.invoke(object, new Random().nextLong());
					} else {
						method.invoke(object, RandomStringUtils.randomAlphabetic(10));
					}

				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * Uses reflection to get a list of getters from <b>base</b> and invokes that
	 * method on <b>base</b> and <b>compareTo</b>. If the results of the getters !=,
	 * or the compareTo class doesn't have that getter, then that method name would
	 * be returned in the list
	 * 
	 * @param base      object to get the list of getters
	 * @param compareTo object that the list of getters is compared to
	 * @return list of methods that weren't equal
	 */
	public static List<String> compareGetters(Object base, Object compareTo, List<String> ignoreFields) {
		List<String> notEquals = new ArrayList<String>();

		for (Method method : getGetters(base.getClass(), ignoreFields)) {
			try {
				Method compareMethod = compareTo.getClass().getMethod(method.getName());

				Object baseResult = method.invoke(base);
				Object compareResult = compareMethod.invoke(compareTo);

				if (!new EqualsBuilder().append(baseResult, compareResult).isEquals()) {
					notEquals.add(String.format(NOT_EQUALS, method.getName(), baseResult, compareResult));
				}
			} catch (NullPointerException e) {
				throw e;
			} catch (Exception e) {
				notEquals.add(String.format(REFLECT_ERROR, method.getName(), e.getMessage()));
			}
		}

		return notEquals;
	}

	/**
	 * finds the getters of a class
	 * 
	 * @param clazz
	 * @return list of Method
	 */
	@SuppressWarnings("rawtypes")
	public static List<Method> getGetters(Class clazz, List<String> ignoreFields) {

		if (ignoreFields == null) {
			ignoreFields = new ArrayList<String>();
		}
		ignoreFields.add("class");

		List<Method> getters = new ArrayList<Method>();

		// get getters
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (isGetter(method) && !listContainsString(ignoreFields, method.getName())) {
				getters.add(method);
			}
		}

		return getters;
	}

	/**
	 * Runs an upper case contains for each element of <b>list</b> for <b>string</b>
	 * i.e.: <code>item.toUpperCase().contains(string.toUpperCase())</code>
	 * 
	 * @param list
	 * @param string
	 * @return true or false
	 */
	private static boolean listContainsString(List<String> list, String string) {
		boolean contains = false;
		for (String item : list) {
			if (string.toUpperCase().contains(item.toUpperCase())) {
				contains = true;
				break;
			}
		}

		return contains;
	}

	/**
	 * @param method
	 * @return true or false if the method is a getter
	 */
	public static boolean isGetter(Method method) {
		if (!method.getName().startsWith("get")) {
			return false;
		}
		if (method.getParameterTypes().length != 0) {
			return false;
		}
		if (void.class.equals(method.getReturnType())) {
			return false;
		}
		return true;
	}

	public static boolean isSetter(Method method) {
		if (!method.getName().startsWith("set")) {
			return false;
		}
		if (method.getParameterTypes().length == 0) {
			return false;
		}
		if (!void.class.equals(method.getReturnType())) {
			return false;
		}
		return true;
	}

}
