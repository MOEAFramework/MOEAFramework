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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CloseShieldWriter;
import org.apache.commons.io.output.WriterOutputStream;

/**
 * Interface used by classes that display content, either to standard output or an output stream.
 */
public interface Displayable {
	
	/**
	 * Displays the contents of this object to the given output stream.  This method does not close the underlying
	 * stream; the caller is responsible for disposing it.
	 * 
	 * @param out the output stream
	 */
	public void display(PrintStream out);
	
	/**
	 * Formats and prints the content of this object to standard output.  Avoid overriding this method, instead
	 * implements the display logic in {@link #display(PrintStream)}.
	 */
	public default void display() {
		display(System.out);
	}
	
	/**
	 * Displays the contents of this object to the given writer.  This method does not close the underlying
	 * writer; the caller is responsible for disposing it.
	 * 
	 * @param out the writer
	 * @throws IOException if an I/O error occurred
	 */
	public default void display(Writer out) throws IOException {
		try (WriterOutputStream writer = WriterOutputStream.builder().setWriter(CloseShieldWriter.wrap(out)).get()) {
			display(writer);
		}
	}

	/**
	 * Displays the contents of this object to the given output stream.  This method does not close the underlying
	 * stream; the caller is responsible for disposing it.
	 * 
	 * @param out the output writer
	 * @throws IOException if an I/O error occurred
	 */
	public default void display(OutputStream out) throws IOException {
		try (PrintStream writer = new PrintStream(CloseShieldOutputStream.wrap(out))) {
			display(writer);
		}
	}

}
