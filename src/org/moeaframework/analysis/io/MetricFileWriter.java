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
package org.moeaframework.analysis.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.util.io.FileUtils;

/**
 * Writes metric files. A metric file is the output of {@code Evaluator} and contains on each line one or more
 * metrics separated by whitespace from one parameterization.
 * <p>
 * This writer can append the results to the file, if a previous file exists.  By reading the previous file with a
 * {@link MetricFileReader}, this writer will begin appending after the last valid entry. Query the
 * {@link #getNumberOfEntries()} to determine how many valid entries are contained in the file.
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
	 * The indicators to evaluate.
	 */
	private final Indicators indicators;

	/**
	 * The number of lines in the file.
	 */
	private int numberOfEntries;

	/**
	 * Constructs an output writer for writing metric files to the specified  file. If the file already exists,
	 * a cleanup operation is first performed. The cleanup operation removes the last line if incomplete and
	 * records the number of correct lines in the file. The {@link #getNumberOfEntries()} can then be used to
	 * resume evaluation from the last recorded entry.
	 * 
	 * @param indicators the indicators to evaluate
	 * @param file the file to which the metrics are written
	 * @param settings the settings for writing metric files
	 * @throws IOException if an I/O error occurred
	 */
	public MetricFileWriter(Indicators indicators, File file, MetricFileWriterSettings settings) throws IOException {
		super();
		this.indicators = indicators;

		if (settings.isAppend()) {
			// when appending, first move the file to a temporary location
			File existingFile = settings.getUncleanFile(file);
			
			if (existingFile.exists()) {
				if (settings.getCleanupStrategy().equals(CleanupStrategy.RESTORE)) {
					if (file.exists()) {
						FileUtils.delete(existingFile);
					} else {
						// do nothing, the unclean file is ready for recovery
					}
				} else if (settings.getCleanupStrategy().equals(CleanupStrategy.OVERWRITE)) {
					FileUtils.delete(existingFile);
				} else {
					throw new FrameworkException(ResultFileWriter.EXISTING_FILE);
				}
			}
			
			if (file.exists()) {
				FileUtils.move(file, existingFile);
			}
		}
		
		// prepare this class for writing
		numberOfEntries = 0;
		writer = new PrintWriter(new BufferedWriter(new FileWriter(file)), true);
		
		writer.println("#Hypervolume GenerationalDistance InvertedGenerationalDistance Spacing EpsilonIndicator MaximumParetoFrontError");

		if (settings.isAppend()) {
			// when appending, copy valid entries out of temporary file
			File existingFile = settings.getUncleanFile(file);
			
			if (existingFile.exists()) {
				try (MetricFileReader reader = new MetricFileReader(existingFile)) {
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
				}
	
				FileUtils.delete(existingFile);
			}
		}
	}

	@Override
	public int getNumberOfEntries() {
		return numberOfEntries;
	}
	
	/**
	 * Evaluates the specified non-dominated population and outputs the resulting metrics to the file.
	 */
	@Override
	public void append(ResultEntry entry) {
		IndicatorValues result = indicators.apply(entry.getPopulation());

		writer.print(result.getHypervolume());
		writer.print(' ');
		writer.print(result.getGenerationalDistance());
		writer.print(' ');
		writer.print(result.getInvertedGenerationalDistance());
		writer.print(' ');
		writer.print(result.getSpacing());
		writer.print(' ');
		writer.print(result.getAdditiveEpsilonIndicator());
		writer.print(' ');
		writer.print(result.getMaximumParetoFrontError());
		writer.println();

		numberOfEntries++;
	}
	
	/**
	 * Gets the index of the metric.  This should match the order that columns are written in
	 * {@link #append(ResultEntry)}.
	 * 
	 * @param value the metric
	 * @return the index of the metric
	 */
	public static int getMetricIndex(String value) {
		if (value.matches("[0-9]+")) {
			return Integer.parseInt(value);
		} else if (value.equalsIgnoreCase("Hypervolume")) {
			return 0;
		} else if (value.equalsIgnoreCase("GenerationalDistance")) {
			return 1;
		} else if (value.equalsIgnoreCase("InvertedGenerationalDistance")) {
			return 2;
		} else if (value.equalsIgnoreCase("Spacing")) {
			return 3;
		} else if (value.equalsIgnoreCase("EpsilonIndicator")) {
			return 4;
		} else if (value.equalsIgnoreCase("MaximumParetoFrontError")) {
			return 5;
		} else {
			throw new FrameworkException("Unsupported metric '" + value + "'");
		}
	}

	@Override
	public void close() {
		writer.close();
	}
	
	/**
	 * Opens the metric file in append mode.  If the file already exists, this writer will validate the contents,
	 * remove any invalid entries at the end of the file, and report the number of valid entries in the file.
	 * 
	 * @param indicators the indicators to evaluate
	 * @param file the file
	 * @return the metric file writer
	 * @throws IOException if an I/O error occurred
	 */
	public static MetricFileWriter append(Indicators indicators, File file) throws IOException {
		return new MetricFileWriter(indicators, file, MetricFileWriterSettings.getDefault());
	}
	
	/**
	 * Opens the metric file in overwrite mode.  Any existing file will be deleted.
	 * 
	 * @param indicators the indicators to evaluate
	 * @param file the file
	 * @return the metric file writer
	 * @throws IOException if an I/O error occurred
	 */
	public static MetricFileWriter overwrite(Indicators indicators, File file) throws IOException {
		return new MetricFileWriter(indicators, file, MetricFileWriterSettings.noAppend());
	}
	
	/**
	 * The settings used when writing metric files.
	 */
	public static class MetricFileWriterSettings extends OutputWriterSettings {
		
		/**
		 * Constructs the default settings object.
		 */
		public MetricFileWriterSettings() {
			super();
		}
		
		/**
		 * Constructs a new settings object.
		 * 
		 * @param append {@code true} to enable append mode, {@code false} otherwise
		 * @param cleanupStrategy the cleanup strategy
		 */
		public MetricFileWriterSettings(Optional<Boolean> append, Optional<CleanupStrategy> cleanupStrategy) {
			super(append, cleanupStrategy);
		}

		/**
		 * Returns the default settings for writing metric files.
		 * 
		 * @return the default settings for writing metric files
		 */
		public static MetricFileWriterSettings getDefault() {
			return new MetricFileWriterSettings();
		}
		
		/**
		 * Returns the settings with append mode disabled.
		 * 
		 * @return the settings with append mode disabled
		 */
		public static MetricFileWriterSettings noAppend() {
			return new MetricFileWriterSettings(Optional.of(false), Optional.empty());
		}
		
	}

}
