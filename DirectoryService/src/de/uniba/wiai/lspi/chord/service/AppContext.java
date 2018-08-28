package de.uniba.wiai.lspi.chord.service;

import java.util.Properties;

/**
 * @author Sunniel (From Jushafang)
 */
public class AppContext {

	/**
	 * common property separator
	 */
	public static String SEPARATOR = ".";

	/**
	 * Properties read from file
	 */
	private static Properties appCtx;

	/**
	 * Application root directory
	 */
	private static String appRoot;

	/**
	 * Initialize with the set of properties defined. If this is called again, the
	 * properties are added to it
	 * 
	 * @param props
	 *            the set of properties read from file
	 */
	public static void init(Properties props) {
		appCtx = props;
	}

	/**
	 * @param key
	 * @return String
	 */
	public static String getValue(String key) {
		return (String) appCtx.get(key);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return String
	 */
	public static String getValue(String key, String defaultValue) {
		return (getValue(key) != null) ? getValue(key) : defaultValue;
	}
	
	/**
	 * @param key
	 * @param value
	 * @return String
	 */
	public static void setValue(String key, String value) {
		appCtx.setProperty(key, value);
	}

	/**
	 * Get absolute path of application root directory, ending with '/' or '\\'
	 * 
	 * @return String Application root directory ending with '/' or '\\'
	 */
	public static String getAppRoot() {
		return appRoot;
	}

	/**
	 * @param appRoot
	 */
	public static void setAppRoot(String appRoot) {
		AppContext.appRoot = appRoot;
	}

	/**
	 * Resets the properties
	 */
	public static void clear() {
		appCtx.clear();
		appCtx = null;
	}
}
