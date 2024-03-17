/* Copyright 2009-2024 David Hadka
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
import java.io.PrintStream;

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
		TabularData<T> data = asTabularData();
		data.display(out);
	}
	
	/**
	 * Saves the contents of this object to a CSV file.
	 * 
	 * @param file the resulting file
	 * @throws IOException if an I/O error occurred while writing the file
	 */
	public default void saveCSV(File file) throws IOException {
		asTabularData().saveCSV(file);
	}

}
