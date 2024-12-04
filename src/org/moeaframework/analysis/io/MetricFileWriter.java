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
import java.io.Writer;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;

/**
 * Writes metric files. A metric file is the output of {@code Evaluator} and contains on each line one or more
 * metrics separated by whitespace from one parameterization.
 * 
 * @see MetricFileReader
 */
public class MetricFileWriter extends ResultWriter {
	
	/**
	 * Enumeration of metrics that are written to the metric file.  This also specifies the order of the columns.
	 */
	public enum Metric {
		
		/**
		 * Hypervolume.
		 */
		Hypervolume,
		
		/**
		 * Generational distance (GD).
		 */
		GenerationalDistance,
		
		/**
		 * Inverted generational distance (IGD).
		 */
		InvertedGenerationalDistance,
		
		/**
		 * Spacing.
		 */
		Spacing,
		
		/**
		 * Additive epsilon-indicator (AEI).
		 */
		EpsilonIndicator,
		
		/**
		 * Maximum Pareto front error.
		 */
		MaximumParetoFrontError;
		
		/**
		 * Returns the number of metrics written to the metric file.
		 * 
		 * @return the number of metrics
		 */
		public static int getNumberOfMetrics() {
			return values().length;
		}
		
		/**
		 * Determine the metric from its string representation using case-insensitive matching.
		 * 
		 * @param value the string representation of the metric
		 * @return the metric
		 * @throws IllegalArgumentException if the metric is not supported
		 */
		public static Metric fromString(String value) {
			return TypedProperties.getEnumFromString(Metric.class, value);
		}
	}
	
	/**
	 * The tokenizer for formatting lines.
	 */
	private final Tokenizer tokenizer;

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
	 * Constructs an output writer for writing metric files to the specified  file.  If the file already exists,
	 * a cleanup operation is first performed.  The cleanup operation removes any invalid entries from the file.
	 * The {@link #getNumberOfEntries()} can then be used to resume evaluation from the last recorded entry.
	 * 
	 * @param indicators the indicators to evaluate
	 * @param file the file to which the metrics are written
	 * @throws IOException if an I/O error occurred
	 */
	public MetricFileWriter(Indicators indicators, File file) throws IOException {
		this(indicators, new BufferedWriter(new FileWriter(file)));
	}

	/**
	 * Constructs an output writer for writing metric files to the specified  file.  If the file already exists,
	 * a cleanup operation is first performed.  The cleanup operation removes any invalid entries from the file.
	 * The {@link #getNumberOfEntries()} can then be used to resume evaluation from the last recorded entry.
	 * 
	 * @param indicators the indicators to evaluate
	 * @param writer the writer
	 * @throws IOException if an I/O error occurred
	 */
	public MetricFileWriter(Indicators indicators, Writer writer) throws IOException {
		super();
		this.indicators = indicators;
		this.writer = new PrintWriter(writer);
		
		numberOfEntries = 0;
		tokenizer = new Tokenizer();

		printHeader();
	}

	@Override
	public int getNumberOfEntries() {
		return numberOfEntries;
	}
	
	/**
	 * Evaluates the specified non-dominated population and outputs the resulting metrics to the file.
	 */
	@Override
	public void write(ResultEntry entry) {
		IndicatorValues result = indicators.apply(new NondominatedPopulation(entry.getPopulation()));
		Metric[] metrics = Metric.values();
		double[] values = new double[metrics.length];
		
		for (int i = 0; i < metrics.length; i++) {
			values[i] = switch (metrics[i]) {
				case Hypervolume -> result.getHypervolume();
				case GenerationalDistance -> result.getGenerationalDistance();
				case InvertedGenerationalDistance -> result.getInvertedGenerationalDistance();
				case Spacing -> result.getSpacing();
				case EpsilonIndicator -> result.getAdditiveEpsilonIndicator();
				case MaximumParetoFrontError -> result.getMaximumParetoFrontError();
			};
		}

		write(values);
		numberOfEntries++;
	}
	
	/**
	 * Writes a line to the metric file containing the given decimal values separated by whitespace.
	 * 
	 * @param values the values
	 */
	protected void write(double[] values) {
		write(DoubleStream.of(values).mapToObj(Double::toString).toArray(String[]::new));
	}
	
	/**
	 * Writes a line to the metric file containing the given strings separated by whitespace.
	 * 
	 * @param values the values
	 */
	protected void write(String[] values) {
		writer.println(tokenizer.encode(values));
		writer.flush();
	}
	
	/**
	 * Writes the header line to the file.
	 */
	protected void printHeader() {
		writer.print("# ");
		write(Stream.of(Metric.values()).map(Metric::name).toArray(String[]::new));
	}
	
	/**
	 * Gets the index of the metric, either from its name or the column index.  This should match the order of values
	 * in {@link Metric}.
	 * 
	 * @param value the metric name or column index
	 * @return the index of the metric
	 */
	public static int getMetricIndex(String value) {
		if (value.matches("[0-9]+")) {
			int index = Integer.parseInt(value);
			Validate.that("index", index).isBetween(0, Metric.getNumberOfMetrics()-1);
			return index;
		}
		
		return Metric.fromString(value).ordinal();
	}

	@Override
	public void close() {
		writer.close();
	}
	
	/**
	 * Opens the metric file in append mode.  If the file already exists, any invalid entries will be removed by
	 * calling {@link #repair(File)}.  Check {@link #getNumberOfEntries()} to determine the number of valid
	 * entries in the file.
	 * 
	 * @param indicators the indicators to evaluate
	 * @param file the file
	 * @return the metric file writer
	 * @throws IOException if an I/O error occurred
	 */
	public static MetricFileWriter append(Indicators indicators, File file) throws IOException {
		if (!file.exists()) {
			return open(indicators, file);
		}
		
		int numberOfEntries = repair(file);
		
		MetricFileWriter writer = new MetricFileWriter(indicators, new BufferedWriter(new FileWriter(file, true))) {

			@Override
			protected void printHeader() {
				// skip header when appending
			}
			
		};
		
		writer.numberOfEntries = numberOfEntries;
		return writer;
	}
	
	/**
	 * Opens the metric file.  Any existing file will be replaced.
	 * 
	 * @param indicators the indicators to evaluate
	 * @param file the file
	 * @return the metric file writer
	 * @throws IOException if an I/O error occurred
	 */
	public static MetricFileWriter open(Indicators indicators, File file) throws IOException {
		return new MetricFileWriter(indicators, file);
	}
	
	/**
	 * Repairs the contents of the metric file, removing any incomplete or invalid entries from the file.
	 * 
	 * @param file the file
	 * @return the number of valid entries in the file
	 * @throws IOException if an I/O error occurred
	 */
	public static int repair(File file) throws IOException {
		if (!file.exists()) {
			return 0;
		}
		
		File tempFile = File.createTempFile("temp", null);
		int numberOfEntries = 0;
			
		try (MetricFileReader reader = new MetricFileReader(file);
				MetricFileWriter writer = new MetricFileWriter(null, tempFile)) {
			while (reader.hasNext()) {
				double[] data = reader.next();
				writer.write(data);
				numberOfEntries++;
			}
		}

		// replace the original only if any changes were made
		replace(tempFile, file);

		return numberOfEntries;
	}

}
