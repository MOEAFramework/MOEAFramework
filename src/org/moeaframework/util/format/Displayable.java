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

import java.io.PrintStream;

/**
 * Interface used by classes that display content, either to standard output or an output stream.
 */
public interface Displayable {
	
	/**
	 * Formats and prints the content of this object to standard output.  Avoid overriding this method, instead
	 * implements the display logic in {@link #display(PrintStream)}.
	 */
	public default void display() {
		display(System.out);
	}
	
	/**
	 * Displays the contents of this object to the given output stream.  This method does not close the underlying
	 * stream; the caller is responsible for disposing it.
	 * 
	 * @param out the output stream
	 */
	public void display(PrintStream out);

}
