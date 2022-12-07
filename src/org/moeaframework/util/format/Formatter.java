package org.moeaframework.util.format;

/**
 * Formats an object of some type into a string.
 * 
 * @param <T> the type of object this can format
 */
public interface Formatter<T> {
	
	public Class<T> getType();
	
	public String format(Object value);

}
