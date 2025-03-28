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
package org.moeaframework.analysis.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

import org.moeaframework.analysis.io.MetricFileWriter.Metric;
import org.moeaframework.util.io.MatrixReader;

/**
 * Reader for metric files produced by {@link MetricFileWriter}.  The file can contain commented lines starting with
 * '#' characters.
 * <p>
 * By default, this reader will suppress any errors reading the contents, unless a serious I/O error occurred.
 * Consequently, reading stops when invalid or incomplete data is detected.  Callers should use
 * {@link #getErrorHandler()} to check if any errors occurred or to change the error handling behavior.
 * 
 * @see MetricFileWriter
 */
public class MetricFileReader extends MatrixReader {

	/**
	 * Constructs a metric file reader for reading metric files from the specified file.
	 * 
	 * @param file the metric file
	 * @throws FileNotFoundException if the file was not found
	 */
	public MetricFileReader(File file) throws FileNotFoundException {
		super(file, Metric.getNumberOfMetrics());
		getErrorHandler().setWarningsAreFatal(false);
		getErrorHandler().setErrorsAreFatal(false);
	}

	/**
	 * Constructs a metric file reader for reading metric files from the underlying reader.
	 * 
	 * @param reader the underlying reader
	 */
	public MetricFileReader(Reader reader) {
		super(reader, Metric.getNumberOfMetrics());
		getErrorHandler().setWarningsAreFatal(false);
		getErrorHandler().setErrorsAreFatal(false);
	}
	
	/**
	 * Opens a metric file for reading.
	 * 
	 * @param file the metric file
	 * @return the reader
	 * @throws FileNotFoundException if the file was not found
	 */
	public static MetricFileReader open(File file) throws FileNotFoundException {
		return new MetricFileReader(file);
	}

}
