package org.moeaframework.util.format;

import java.io.PrintStream;

/**
 * Interface used by classes containing tabular data (think like a spreadsheet) that
 * can be formatted and rendered in various ways.
 *
 * @param <T> the type of records
 */
public interface Formattable<T> extends Displayable {
	
	/**
	 * Returns the contents of this object as a {@link TabularData} instance, which can
	 * be used to save, print, or format the data in variou ways.
	 * 
	 * @return the {@link TabularData} instance
	 */
	public TabularData<T> asTabularData();
	
	@Override
	public default void display(PrintStream out) {
		TabularData<T> data = asTabularData();
		data.display(out);
	}

}
