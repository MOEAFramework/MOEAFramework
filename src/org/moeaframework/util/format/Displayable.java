package org.moeaframework.util.format;

import java.io.PrintStream;

/**
 * Interface used by classes that display content, either to standard output or an
 * output stream.
 */
public interface Displayable {
	
	/**
	 * Formats and prints the content of this object to standard output.  Avoid overriding this
	 * method, instead implements the display logic in {@link #display(PrintStream)}.
	 */
	public default void display() {
		display(System.out);
	}
	
	/**
	 * Displays the contents of this object to the given output stream.  This method does not
	 * close the underlying stream; the caller is responsible for disposing it.
	 * 
	 * @param out the output stream
	 */
	public void display(PrintStream out);

}
