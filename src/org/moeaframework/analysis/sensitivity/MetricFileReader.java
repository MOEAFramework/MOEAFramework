/* Copyright 2009-2012 David Hadka
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
 * Reader for metric files produced by {@link MetricFileWriter}. The file can
 * contain commented lines starting with '#' characters.
 * <p>
 * This reader is expected to gracefully recover from incomplete or improperly
 * formatted files. Unless a serious I/O error occurred, this reader will
 * attempt to load the file to the last valid entry. This requirement enables a
 * {@code MetricFileWriter} to resume processing at a valid state.
 * 
 * @see MetricFileWriter
 */
public class MetricFileReader implements Iterable<double[]>,
		Iterator<double[]>, Closeable {

	/**
	 * The underlying reader.
	 */
	private final CommentedLineReader reader;

	/**
	 * The next result to be returned; or {@code null} if the next result has
	 * not yet been read.
	 */
	private double[] nextResult;

	/**
	 * {@code true} if an error occurred parsing the metric file; {@code false}
	 * otherwise.
	 */
	private boolean error;

	/**
	 * Constructs a metric file reader for reading metric files from the
	 * specified file.
	 * 
	 * @param file the metric file
	 * @throws FileNotFoundException if the file was not found
	 */
	public MetricFileReader(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	/**
	 * Constructs a metric file reader for reading metric files from the 
	 * underlying reader.
	 * 
	 * @param reader the underlying reader
	 */
	public MetricFileReader(Reader reader) {
		super();

		if (reader instanceof CommentedLineReader) {
			this.reader = (CommentedLineReader)reader;
		} else {
			this.reader = new CommentedLineReader(reader);
		}
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

			if (nextResult == null) {
				nextResult = readNextResult();
			}

			return nextResult != null;
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public double[] next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		double[] result = nextResult;
		nextResult = null;
		return result;
	}

	/**
	 * Returns the next result from the metric file; or {@code null} if an
	 * end-of-file or a malformed line was reached.
	 * 
	 * @return the next result from the metric file; or {@code null} if an
	 *         end-of-file or a malformed line was reached
	 * @throws IOException if an I/O error occurred
	 */
	private double[] readNextResult() throws IOException {
		String line = reader.readLine();

		if (line == null) {
			return null;
		}

		String[] tokens = line.split("\\s+");

		if (tokens.length != MetricFileWriter.NUMBER_OF_METRICS) {
			error = true;
			return null;
		}

		double[] entry = new double[tokens.length];

		try {
			for (int i = 0; i < tokens.length; i++) {
				entry[i] = Double.parseDouble(tokens[i]);
			}
		} catch (NumberFormatException e) {
			error = true;
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
