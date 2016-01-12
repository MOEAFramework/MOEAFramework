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
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import org.moeaframework.core.FrameworkException;

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
	private final MatrixReader reader;

	/**
	 * The parameter definition file.
	 */
	private final ParameterFile parameterFile;

	/**
	 * Constructs a sample reader for reading parameter samples from the
	 * specified file.
	 * 
	 * @param file the parameter sample file
	 * @param parameterFile the parameter definition file
	 * @throws IOException if an I/O error occurred
	 */
	public SampleReader(File file, ParameterFile parameterFile)
			throws IOException {
		this(new MatrixReader(file, parameterFile.size()), parameterFile);
	}

	/**
	 * Constructs a sample reader for reading parameter samples from the
	 * underlying reader.
	 * 
	 * @param reader the underlying reader
	 * @param parameterFile the parameter definition file
	 */
	public SampleReader(Reader reader, ParameterFile parameterFile) {
		this(new MatrixReader(reader, parameterFile.size()), parameterFile);
	}
	
	/**
	 * Constructs a sample reader for reading parameter samples from the
	 * underlying reader.
	 * 
	 * @param reader the underlying reader
	 * @param parameterFile the parameter definition file
	 */
	private SampleReader(MatrixReader reader, ParameterFile parameterFile) {
		super();
		this.reader = reader;
		this.parameterFile = parameterFile;
	}

	@Override
	public Iterator<Properties> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return reader.hasNext();
	}

	@Override
	public Properties next() {
		double[] values = reader.next();
		Properties parameters = new Properties();

		for (int i = 0; i < values.length; i++) {
			Parameter parameter = parameterFile.get(i);

			if ((values[i] < parameter.getLowerBound())
					|| (values[i] > parameter.getUpperBound())) {
				throw new FrameworkException("parameter out of bounds");
			}

			parameters.setProperty(parameterFile.get(i).getName(),
					Double.toString(values[i]));
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
