/* Copyright 2009-2011 David Hadka
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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.io.CommentedLineReader;

/**
 * Reads the parameter samples from the output of {@link SampleGenerator}. The
 * column ordering in the sample file matches the ordering of parameters in a
 * {@link ParameterFile}. The read {@link Properties} map the parameter name
 * to the parameter value.
 * 
 * @see SampleGenerator
 * @see ParameterFile
 */
public class SampleReader implements Iterable<Properties>,
		Iterator<Properties>, Closeable {

	/**
	 * The underlying reader.
	 */
	private final CommentedLineReader reader;

	/**
	 * The parameter definition file.
	 */
	private final ParameterFile parameterFile;

	/**
	 * The next parameters to be returned; or {@code null} if the next
	 * parameters has not yet been read.
	 */
	private Properties nextParameters;

	/**
	 * {@code true} if an error occurred parsing the parameter sample file;
	 * {@code false} otherwise.
	 */
	private boolean error;

	/**
	 * Constructs a sample reader for reading parameter samples from the
	 * specified file.
	 * 
	 * @param file the parameter sample file
	 * @throws IOException if an I/O error occurred
	 */
	public SampleReader(File file, ParameterFile parameterFile)
			throws IOException {
		this(new FileReader(file), parameterFile);
	}

	/**
	 * Constructs a sample reader for reading parameter samples from the
	 * underlying reader.
	 * 
	 * @param reader the underlying reader
	 */
	public SampleReader(Reader reader, ParameterFile parameterFile) {
		super();
		this.parameterFile = parameterFile;

		if (reader instanceof CommentedLineReader) {
			this.reader = (CommentedLineReader)reader;
		} else {
			this.reader = new CommentedLineReader(reader);
		}
	}

	@Override
	public Iterator<Properties> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		try {
			if (error) {
				return false;
			}

			if (nextParameters == null) {
				nextParameters = readNextParameters();
			}

			return nextParameters != null;
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public Properties next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		Properties parameters = nextParameters;
		nextParameters = null;
		return parameters;
	}

	/**
	 * Returns the next parameter set from the parameter sample file; or
	 * {@code null} if the end-of-file was reached.
	 * 
	 * @return the next parameter set from the parameter sample file; or
	 *         {@code null} if the end-of-file was reached
	 * @throws IOException if an I/O error occurred
	 */
	private Properties readNextParameters() throws IOException {
		String line = reader.readLine();

		if (line == null) {
			return null;
		}

		String[] tokens = line.split("\\s+");

		if (tokens.length != parameterFile.size()) {
			error = true;
			throw new IOException("insufficient number of entries");
		}

		Properties parameters = new Properties();

		try {
			for (int i = 0; i < tokens.length; i++) {
				double value = Double.parseDouble(tokens[i]);
				Parameter parameter = parameterFile.get(i);

				if ((value < parameter.getLowerBound())
						|| (value > parameter.getUpperBound())) {
					throw new IOException("parameter out of bounds");
				}

				parameters.setProperty(parameterFile.get(i).getName(),
						tokens[i]);
			}
		} catch (NumberFormatException e) {
			error = true;
			throw e;
		}

		return parameters;
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
