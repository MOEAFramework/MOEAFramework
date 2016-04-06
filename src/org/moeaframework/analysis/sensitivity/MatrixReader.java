/* Copyright 2009-2016 David Hadka
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.io.CommentedLineReader;

/**
 * Reader of files containing matrices.  A matrix contains numerical data
 * separated into rows and columns.  The values in a row are separated by
 * whitespace.
 * <p>
 * The file can contain commented lines starting with '#' characters.
 * <p>
 * Parsing stops at the first error.  Check the {@code error} flag to determine
 * if an error was encountered.  An exception may or may not be thrown,
 * depending on the type of error and the value of the 
 * {@code suppressExceptions} flag.  If exceptions are suppressed, a warning
 * message will be printed.
 */
class MatrixReader implements Iterable<double[]>, Iterator<double[]>, 
Closeable {

	/**
	 * The underlying reader.
	 */
	private final CommentedLineReader reader;
	
	/**
	 * The expected number of columns; or {@code -1} if the matrix has no
	 * fixed column count.
	 */
	private final int numberOfColumns;

	/**
	 * The next row to be returned; or {@code null} if the next row has
	 * not yet been read.
	 */
	private double[] nextRow;

	/**
	 * {@code true} if an error occurred parsing the file; {@code false}
	 * otherwise.
	 */
	private boolean error;
	
	/**
	 * {@code true} if errors are suppressed; {@code false} otherwise.  I/O
	 * errors will still be thrown.
	 */
	private boolean suppressExceptions;

	/**
	 * Constructs a reader for loading a matrix contained in the specified file.
	 * 
	 * @param file the file containing the matrix
	 * @throws FileNotFoundException if the file was not found
	 */
	public MatrixReader(File file) throws FileNotFoundException {
		this(new FileReader(file), -1);
	}
	
	/**
	 * Constructs a reader for loading a matrix contained in the specified file.
	 * 
	 * @param file the file containing the matrix
	 * @param numberOfColumns the expected number of columns; or {@code -1} if
	 *        the matrix has no fixed column count
	 * @throws FileNotFoundException if the file was not found
	 */
	public MatrixReader(File file, int numberOfColumns)
			throws FileNotFoundException {
		this(new FileReader(file), numberOfColumns);
	}
	
	/**
	 * Constructs a reader for loading a matrix accessed through the underlying
	 * reader.
	 * 
	 * @param reader the underlying reader
	 */
	public MatrixReader(Reader reader) {
		this(reader, -1);
	}

	/**
	 * Constructs a reader for loading a matrix accessed through the underlying
	 * reader.
	 * 
	 * @param reader the underlying reader
	 * @param numberOfColumns the expected number of columns; or {@code -1} if
	 *        the matrix has no fixed column count
	 */
	public MatrixReader(Reader reader, int numberOfColumns) {
		super();

		if (reader instanceof CommentedLineReader) {
			this.reader = (CommentedLineReader)reader;
		} else {
			this.reader = new CommentedLineReader(reader);
		}
		
		this.numberOfColumns = numberOfColumns;
	}

	@Override
	public Iterator<double[]> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		try {
			if (error) {
				return false;
			}

			if (nextRow == null) {
				nextRow = readNextRow();
			}

			return nextRow != null;
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public double[] next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		double[] result = nextRow;
		nextRow = null;
		return result;
	}

	/**
	 * Returns the next row from the matrix; or {@code null} if an end-of-file
	 * or a malformed line was reached.
	 * 
	 * @return the next row from the matrix; or {@code null} if an end-of-file
	 *         or a malformed line was reached
	 * @throws IOException if an I/O error occurred
	 */
	private double[] readNextRow() throws IOException {
		String line = reader.readLine();

		if (line == null) {
			return null;
		}

		String[] tokens = line.trim().split("\\s+");

		if ((numberOfColumns >= 0) && (tokens.length != numberOfColumns)) {
			error = true;
			
			if (suppressExceptions) {
				System.err.println("insufficient number of entries in row, ignoring remaining rows in the file");
				return null;
			} else {
				throw new IOException("insufficient number of entries in row");
			}
		}

		double[] entry = new double[tokens.length];

		try {
			for (int i = 0; i < tokens.length; i++) {
				entry[i] = Double.parseDouble(tokens[i]);
			}
		} catch (NumberFormatException e) {
			error = true;
			
			if (suppressExceptions) {
				return null;
			} else {
				throw new IOException("invalid entry in row", e);
			}
		}

		return entry;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	/**
	 * Returns {@code true} if exceptions are suppressed; {@code false}
	 * otherwise.  Low-level I/O exceptions will still be thrown.  
	 * 
	 * @return {@code true} if exceptions are suppressed; {@code false}
	 *         otherwise
	 * @deprecated use {@link #isSuppressExceptions()} instead
	 */
	@Deprecated
	boolean isSupressExceptions() {
		return suppressExceptions;
	}

	/**
	 * Set to {@code true} to suppress exceptions; {@code false} otherwise.
	 * Low-level I/O exceptions will still be thrown.
	 * 
	 * @param supressExceptions {@code true} if exceptions are suppressed; 
	 *        {@code false} otherwise
	 * @deprecated use {@link #setSuppressExceptions(boolean)} instead
	 */
	@Deprecated
	void setSupressExceptions(boolean supressExceptions) {
		this.suppressExceptions = supressExceptions;
	}
	
	/**
	 * Returns {@code true} if exceptions are suppressed; {@code false}
	 * otherwise.  Low-level I/O exceptions will still be thrown.  
	 * 
	 * @return {@code true} if exceptions are suppressed; {@code false}
	 *         otherwise
	 */
	boolean isSuppressExceptions() {
		return suppressExceptions;
	}

	/**
	 * Set to {@code true} to suppress exceptions; {@code false} otherwise.
	 * Low-level I/O exceptions will still be thrown.
	 * 
	 * @param supressExceptions {@code true} if exceptions are suppressed; 
	 *        {@code false} otherwise
	 */
	void setSuppressExceptions(boolean suppressExceptions) {
		this.suppressExceptions = suppressExceptions;
	}

}
