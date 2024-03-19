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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.moeaframework.analysis.io.MatrixReader;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.OptionCompleter;

 /**
 * Command line utility for computing statistics across multiple data files.  The data files should only contain
 * numeric values, and each file must contain the same number of rows and columns.
 */
public class SimpleStatistics extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for computing statistics across multiple data files.
	 */
	public SimpleStatistics() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("m")
				.longOpt("mode")
				.hasArg()
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("i")
				.longOpt("ignore")
				.build());
		options.addOption(Option.builder("x")
				.longOpt("maximum")
				.hasArg()
				.argName("value")
				.build());
		
		return options;
	}
	
	/**
	 * Loads the data from the specified file.
	 * 
	 * @param file the file containing numeric data
	 * @return the data from the specified file
	 * @throws IOException if an I/O error occurred
	 */
	private double[][] load(File file) throws IOException {
		try (MatrixReader reader = new MatrixReader(file)) {
			return reader.readAll();
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String mode = null;
		List<double[][]> entries = new ArrayList<double[][]>();
		SummaryStatistics statistics = new SummaryStatistics();
		OptionCompleter completer = new OptionCompleter("minimum", "maximum", "average", "stdev", "count");
		
		//load data from all input files
		for (String filename : commandLine.getArgs()) {
			entries.add(load(new File(filename)));
		}
		
		//validate the inputs
		if (entries.isEmpty()) {
			throw new IllegalArgumentException("requires at least one file");
		}
		
		int numberOfRows = -1;
		int numberOfColumns = -1;
		
		for (int i=0; i<entries.size(); i++) {
			if (numberOfRows == -1) {
				numberOfRows = entries.get(i).length;
				
				if (numberOfRows == 0) {
					throw new IllegalArgumentException("empty file: " + commandLine.getArgs()[i]);
				}
			} else if (numberOfRows != entries.get(i).length) {
				throw new IllegalArgumentException("unbalanced rows: " + commandLine.getArgs()[i]);
			}
			
			if (numberOfColumns == -1) {
				numberOfColumns = entries.get(i)[0].length;
			} else if (numberOfColumns != entries.get(i)[0].length) {
				throw new IllegalArgumentException("unbalanced columns: " + commandLine.getArgs()[i]);
			}
		}

		//setup the mode
		if (commandLine.hasOption("mode")) {
			mode = completer.lookup(commandLine.getOptionValue("mode"));
			
			if (mode == null) {
				throw new IllegalArgumentException("invalid mode '" + commandLine.getOptionValue("mode") + "'");
			}
		} else {
			mode = "average";
		}
		
		try (OutputLogger output = new OutputLogger(commandLine.getOptionValue("output"))) {
			//compute the statistics
			for (int i=0; i<numberOfRows; i++) {
				for (int j=0; j<numberOfColumns; j++) {
					statistics.clear();
					
					for (int k=0; k<entries.size(); k++) {
						double value = entries.get(k)[i][j];
						
						if (Double.isInfinite(value) && commandLine.hasOption("maximum")) {
							value = Double.parseDouble(commandLine.getOptionValue("maximum"));
						}
						
						if ((Double.isInfinite(value) || Double.isNaN(value)) && commandLine.hasOption("ignore")) {
							// ignore infinity or NaN values
						} else {
							statistics.addValue(value);
						}
					}
					
					if (j > 0) {
						output.print(' ');
					}
					
					switch (mode) {
						case "minimum" -> output.print(statistics.getMin());
						case "maximum" -> output.print(statistics.getMax());
						case "average" -> output.print(statistics.getMean());
						case "stdev" -> output.print(statistics.getStandardDeviation());
						case "count" -> output.print(statistics.getN());
						default -> throw new IllegalStateException();
					}
				}
				
				output.println();
			}
		}
	}
	
	/**
	 * Starts the command line utility for computing statistics across multiple data files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SimpleStatistics().start(args);
	}

}
