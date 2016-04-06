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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

import static org.moeaframework.analysis.sensitivity.MetricFileWriter.NUMBER_OF_METRICS;

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
public class MetricFileReader extends MatrixReader {

	/**
	 * Constructs a metric file reader for reading metric files from the
	 * specified file.
	 * 
	 * @param file the metric file
	 * @throws FileNotFoundException if the file was not found
	 */
	public MetricFileReader(File file) throws FileNotFoundException {
		super(file, NUMBER_OF_METRICS);
		setSuppressExceptions(true);
	}

	/**
	 * Constructs a metric file reader for reading metric files from the 
	 * underlying reader.
	 * 
	 * @param reader the underlying reader
	 */
	public MetricFileReader(Reader reader) {
		super(reader, NUMBER_OF_METRICS);
		setSuppressExceptions(true);
	}

}
