package org.moeaframework.util.format;

import java.io.PrintStream;

/**
 * Interface used by classes containing tabular data (think like a spreadsheet) that
 * can be formatted and rendered in various ways.
 *
 * @param <T> the type of records
 */
public interface Formattable<T> {
	
	/**
	 * Returns the contents of this object as a {@link TabularData} instance, which can
	 * be used to save, print, or format the data in variou ways.
	 * 
	 * @return the {@link TabularData} instance
	 */
	public TabularData<T> asTabularData();
	
	/**
	 * Formats and prints the content of this object to standard output.
	 */
	public default void display() {
		display(System.out);
	}
	
	/**
	 * Formats and prints the contents of this object to the given output stream.
	 * 
	 * @param out the output stream
	 */
	public default void display(PrintStream out) {
		TabularData<T> data = asTabularData();
		data.display(out);
	}

}
