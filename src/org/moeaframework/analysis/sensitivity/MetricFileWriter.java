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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.util.io.FileUtils;

/**
 * Writes metric files. A metric file is the output of {@code Evaluator} and
 * contains on each line one or more metrics separated by whitespace from one
 * parameterization.
 * <p>
 * This writer will append the results to the file, if a previous file exists.
 * By reading the previous file with a {@link MetricFileReader}, this writer
 * will being appending after the last valid entry. Query the
 * {@link #getNumberOfEntries()} method to determine how many valid entries are
 * contained in the file.
 * 
 * @see MetricFileReader
 */
public class MetricFileWriter implements OutputWriter {
	
	/**
	 * The number of metrics supported by {@code MetricFileWriter}.
	 */
	public static final int NUMBER_OF_METRICS = 6;

	/**
	 * The stream for appending data to the file.
	 */
	private final PrintWriter writer;

	/**
	 * The quality indicator for producing the metrics.
	 */
	private final QualityIndicator qualityIndicator;

	/**
	 * The number of lines in the file.
	 */
	private int numberOfEntries;

	/**
	 * Constructs an output writer for writing metric files to the specified 
	 * file. If the file already exists, a cleanup operation is first performed.
	 * The cleanup operation removes the last line if incomplete and records the
	 * number of correct lines in the file. The {@link #getNumberOfEntries()} 
	 * can then be used to resume evaluation from the last recorded entry.
	 * 
	 * @param qualityIndicator the quality indicator for producing the metrics
	 * @param file the file to which the metrics are written
	 * @throws IOException if an I/O error occurred
	 */
	public MetricFileWriter(QualityIndicator qualityIndicator, File file)
			throws IOException {
		super();
		this.qualityIndicator = qualityIndicator;

		// if the file already exists, move it to a temporary location
		File existingFile = new File(file.getParent(), "." + file.getName()
				+ ".unclean");
		
		if (existingFile.exists()) {
			if (Settings.getCleanupStrategy().equalsIgnoreCase("restore")) {
				if (file.exists()) {
					FileUtils.delete(existingFile);
				} else {
					// do nothing, the unclean file is ready for recovery
				}
			} else if (Settings.getCleanupStrategy().equalsIgnoreCase("overwrite")) {
				FileUtils.delete(existingFile);
			} else {
				throw new FrameworkException(ResultFileWriter.EXISTING_FILE);
			}
		}
		
		if (file.exists()) {
			FileUtils.move(file, existingFile);
		}
		
		// prepare this class for writing
		numberOfEntries = 0;
		writer = new PrintWriter(new BufferedWriter(new FileWriter(file)), 
				true);
		
		writer.println("#Hypervolume GenerationalDistance InvertedGenerationalDistance Spacing EpsilonIndicator MaximumParetoFrontError");

		// if the file already existed, copy all complete entries
		if (existingFile.exists()) {
			MetricFileReader reader = null;

			try {
				reader = new MetricFileReader(existingFile);

				while (reader.hasNext()) {
					double[] data = reader.next();

					writer.print(data[0]);

					for (int i = 1; i < data.length; i++) {
						writer.print(' ');
						writer.print(data[i]);
					}

					writer.println();

					numberOfEntries++;
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			FileUtils.delete(existingFile);
		}
	}

	@Override
	public int getNumberOfEntries() {
		return numberOfEntries;
	}
	
	/**
	 * Evaluates the specified non-dominated population and outputs the
	 * resulting metrics to the file.
	 */
	@Override
	public void append(ResultEntry entry) {
		qualityIndicator.calculate(entry.getPopulation());

		writer.print(qualityIndicator.getHypervolume());
		writer.print(' ');
		writer.print(qualityIndicator.getGenerationalDistance());
		writer.print(' ');
		writer.print(qualityIndicator.getInvertedGenerationalDistance());
		writer.print(' ');
		writer.print(qualityIndicator.getSpacing());
		writer.print(' ');
		writer.print(qualityIndicator.getAdditiveEpsilonIndicator());
		writer.print(' ');
		writer.print(qualityIndicator.getMaximumParetoFrontError());
		writer.println();

		numberOfEntries++;
	}

	@Override
	public void close() {
		writer.close();
	}

}
