package org.moeaframework.analysis.store;

/**
 * The application intent that determines what operations are permitted on the data store.
 */
public enum Intent {
	
	/**
	 * Indicates only read operations are permitted.
	 */
	READ_ONLY,
	
	/**
	 * Indicates read and write operations are permitted.
	 */
	READ_WRITE

}
