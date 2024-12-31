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
package org.moeaframework.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.moeaframework.util.ErrorHandler;

/**
 * Reader of files containing matrices.  A matrix contains numerical data separated into rows and columns.  The values
 * in a row are separated by whitespace.
 * <p>
 * The file can contain commented lines starting with '#' characters.
 * <p>
 * Parsing stops at the first error.  Check the {@code error} flag to determine if an error was encountered.  An
 * exception may or may not be thrown, depending on the type of error and the value of the {@code suppressExceptions}
 * flag.  If exceptions are suppressed, a warning message will be printed.
 */
public class MatrixReader implements Iterable<double[]>, Iterator<double[]>, Closeable {
	
	/**
	 * Constant used to indicate the number of columns is not known.
	 */
	private static final int UNSPECIFIED_COLUMN_COUNT = -1;

	/**
	 * The underlying reader.
	 */
	private final LineReader reader;
	
	/**
	 * The tokenizer for parsing each line.
	 */
	private final Tokenizer tokenizer;
	
	/**
	 * The expected number of columns; or {@code -1} if the matrix has no fixed column count.
	 */
	private final int numberOfColumns;

	/**
	 * The next row to be returned; or {@code null} if the next row has not yet been read.
	 */
	private double[] nextRow;

	/**
	 * The error handler.
	 */
	private final ErrorHandler errorHandler;

	/**
	 * Constructs a reader for loading a matrix contained in the specified file.
	 * 
	 * @param file the file containing the matrix
	 * @throws FileNotFoundException if the file was not found
	 */
	public MatrixReader(File file) throws FileNotFoundException {
		this(new FileReader(file), UNSPECIFIED_COLUMN_COUNT);
	}
	
	/**
	 * Constructs a reader for loading a matrix contained in the specified file.
	 * 
	 * @param file the file containing the matrix
	 * @param numberOfColumns the expected number of columns; or {@value #UNSPECIFIED_COLUMN_COUNT} if the matrix has
	 *        no fixed column count
	 * @throws FileNotFoundException if the file was not found
	 */
	public MatrixReader(File file, int numberOfColumns) throws FileNotFoundException {
		this(new FileReader(file), numberOfColumns);
	}
	
	/**
	 * Constructs a reader for loading a matrix accessed through the underlying reader.
	 * 
	 * @param reader the underlying reader
	 */
	public MatrixReader(Reader reader) {
		this(reader, UNSPECIFIED_COLUMN_COUNT);
	}

	/**
	 * Constructs a reader for loading a matrix accessed through the underlying reader.
	 * 
	 * @param reader the underlying reader
	 * @param numberOfColumns the expected number of columns; or {@value #UNSPECIFIED_COLUMN_COUNT} if the matrix has
	 *        no fixed column count
	 */
	public MatrixReader(Reader reader, int numberOfColumns) {
		super();
		this.reader = LineReader.wrap(reader).skipComments().trim();
		this.numberOfColumns = numberOfColumns;
		
		tokenizer = new Tokenizer();
		
		errorHandler = new ErrorHandler();
		errorHandler.setSuppressDuplicates(true);
		errorHandler.setWarningsAreFatal(false);
		errorHandler.setErrorsAreFatal(true);
	}
	
	/**
	 * Returns the error handler used by this reader.
	 * 
	 * @return the error handler
	 */
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	@Override
	public Iterator<double[]> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		try {
			if (errorHandler.isError()) {
				return false;
			}

			if (nextRow == null) {
				nextRow = readNextRow();
			}

			return nextRow != null;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
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
	 * Returns the next row from the matrix; or {@code null} if an end-of-file or a malformed line was reached.
	 * 
	 * @return the next row from the matrix; or {@code null} if an end-of-file or a malformed line was reached
	 * @throws IOException if an I/O error occurred
	 */
	private double[] readNextRow() throws IOException {
		String line = reader.readLine();

		if (line == null) {
			return null;
		}

		String[] tokens = tokenizer.decodeToArray(line);

		if ((numberOfColumns >= 0) && (tokens.length != numberOfColumns)) {
			errorHandler.error("Insufficient number of entries in row, expected {0} but was {1}",
					numberOfColumns, tokens.length);
			return null;
		}

		double[] entry = new double[tokens.length];

		try {
			for (int i = 0; i < tokens.length; i++) {
				entry[i] = Double.parseDouble(tokens[i]);
			}
		} catch (NumberFormatException e) {
			errorHandler.error("Entry not parseable as a number: {0}", e.getMessage());
			return null;
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

}
