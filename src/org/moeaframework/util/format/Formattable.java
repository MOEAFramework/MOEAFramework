/* Copyright 2009-2025 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.util.format;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

/**
 * Interface used by classes containing tabular data (think like a spreadsheet) that can be formatted and rendered in
 * various ways.
 *
 * @param <T> the type of records
 */
public interface Formattable<T> extends Displayable {
	
	/**
	 * Returns the contents of this object as a {@link TabularData} instance, which can be used to save, print, or
	 * format the data in various ways.
	 * 
	 * @return the {@link TabularData} instance
	 */
	public TabularData<T> asTabularData();
	
	@Override
	public default void display(PrintStream out) {
		asTabularData().display(out);
	}
	
	/**
	 * Displays the data in the given format to the terminal.
	 * 
	 * @param tableFormat the table format
	 */
	public default void display(TableFormat tableFormat) {
		asTabularData().display(tableFormat);
	}
	
	/**
	 * Displays the data in the given format.
	 * 
	 * @param tableFormat the table format
	 * @param out the output stream
	 */
	public default void display(TableFormat tableFormat, PrintStream out) {
		asTabularData().display(tableFormat, out);
	}
	
	/**
	 * Saves the data to a file in the requested format.
	 * 
	 * @param tableFormat the resulting table format
	 * @param file the resulting file
	 * @throws IOException if an I/O error occurred
	 */
	public default void save(TableFormat tableFormat, File file) throws IOException {
		asTabularData().save(tableFormat, file);
	}
	
	/**
	 * Saves the data to an output stream in the requested format.
	 * 
	 * @param tableFormat the resulting table format
	 * @param out the output stream
	 * @throws IOException if an I/O error occurred
	 */
	public default void save(TableFormat tableFormat, OutputStream out) throws IOException {
		asTabularData().save(tableFormat, out);
	}
	
	/**
	 * Saves the data to a writer in the requested format.
	 * 
	 * @param tableFormat the resulting table format
	 * @param writer the writer
	 * @throws IOException if an I/O error occurred
	 */
	public default void save(TableFormat tableFormat, Writer writer) throws IOException {
		asTabularData().save(tableFormat, writer);
	}

}
