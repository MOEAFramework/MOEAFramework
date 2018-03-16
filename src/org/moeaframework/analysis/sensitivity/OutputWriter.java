/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.analysis.sensitivity;

import java.io.Closeable;
import java.io.IOException;

/**
 * Writes output files.  The allow the {@link Evaluator} to automatically
 * resume itself at the last known good result, output writers are expected to
 * cleanup the file and return the number of valid entries through
 * {@link #getNumberOfEntries()}.
 */
public interface OutputWriter extends Closeable {
	
	/**
	 * Returns the number of entries in the file. If the file already existed,
	 * this returns the number of complete entries in the output file. This
	 * value is incremented on every invocation to the
	 * {@link #append} method.
	 * 
	 * @return the number of entries in the file
	 */
	public int getNumberOfEntries();
	
	/**
	 * Appends the specified non-dominated population and optional attributes
	 * to the file.
	 * 
	 * @param entry the non-dominated population and optional attributes
	 * @throws IOException if an I/O error occurred
	 */
	public void append(ResultEntry entry) throws IOException;

}
