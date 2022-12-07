package org.moeaframework.util.format;

/**
 * Formats an object of some type into a string.
 * 
 * @param <T> the type of object this can format
 */
public interface Formatter<T> {
	
	/**
	 * Returns the class (or classes using inheritance) this formatter supports.
	 * 
	 * @return the class
	 */
	public Class<T> getType();
	
	/**
	 * Formats the given value into a string representation.
	 * 
	 * @param value the value
	 * @return the string representation
	 */
	public String format(Object value);

}
