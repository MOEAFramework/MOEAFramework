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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.OptionCompleter;

 /**
 * Command line utility for computing statistics across multiple data files.
 * The data files should only contain numeric values, and each file must
 * contain the same number of rows and columns.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.SimpleStatistics <options> <files>}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
 *   <tr>
 *     <td>{@code -m, --mode}</td>
 *     <td>The mode of operation, such as {@code minimum}, {@code maximum}, 
		   {@code average}, {@code stdev}, or {@code count} (required).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>Location where the output is saved.  If not given, the output is
 *         printed to the console.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -i, --ignore}</td>
 *     <td>Ignore infinite or NaN values.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -x, --maximum}</td>
 *     <td>The value used to replace any infinity values.  If infinity values
 *         are not replaced, some calculations, such as the average, can be
 *         skewed.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code <files>}</td>
 *     <td>The files to analyze.</td>
 *   </tr>
 * </table>
 */
public class SimpleStatistics extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for computing statistics across
	 * multiple data files.
	 */
	public SimpleStatistics() {
		super();
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("mode")
				.hasArg()
				.create('m'));
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("ignore")
				.create('i'));
		options.addOption(OptionBuilder
				.withLongOpt("maximum")
				.hasArg()
				.withArgName("value")
				.create('x'));
		
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
		MatrixReader reader = null;
		
		try {
			reader = new MatrixReader(file);
			List<double[]> data = new ArrayList<double[]>();
			
			while (reader.hasNext()) {
				data.add(reader.next());
			}
			
			return data.toArray(new double[0][]);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String mode = null;
		PrintStream out = null;
		List<double[][]> entries = new ArrayList<double[][]>();
		SummaryStatistics statistics = new SummaryStatistics();
		OptionCompleter completer = new OptionCompleter("minimum", "maximum", 
				"average", "stdev", "count");
		
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
					throw new IllegalArgumentException("empty file: " + 
							commandLine.getArgs()[i]);
				}
			} else if (numberOfRows != entries.get(i).length) {
				throw new IllegalArgumentException("unbalanced rows: " +
						commandLine.getArgs()[i]);
			}
			
			if (numberOfColumns == -1) {
				numberOfColumns = entries.get(i)[0].length;
			} else if (numberOfColumns != entries.get(i)[0].length) {
				throw new IllegalArgumentException("unbalanced columns: " + 
						commandLine.getArgs()[i]);
			}
		}

		//setup the mode
		if (commandLine.hasOption("mode")) {
			mode = completer.lookup(commandLine.getOptionValue("mode"));
			
			if (mode == null) {
				throw new IllegalArgumentException("invalid mode");
			}
		} else {
			mode = "average";
		}
		
		try {
			//instantiate the writer
			if (commandLine.hasOption("output")) {
				out = new PrintStream(commandLine.getOptionValue("output"));
			} else {
				out = System.out;
			}
		
			//compute the statistics
			for (int i=0; i<numberOfRows; i++) {
				for (int j=0; j<numberOfColumns; j++) {
					statistics.clear();
					
					for (int k=0; k<entries.size(); k++) {
						double value = entries.get(k)[i][j];
						
						if (Double.isInfinite(value) &&
								commandLine.hasOption("maximum")) {
							value = Double.parseDouble(
									commandLine.getOptionValue("maximum"));
						}
						
						if ((Double.isInfinite(value) || Double.isNaN(value)) &&
								commandLine.hasOption("ignore")) {
							// ignore infinity or NaN values
						} else {
							statistics.addValue(value);
						}
					}
					
					if (j > 0) {
						out.print(' ');
					}
					
					if (mode.equals("minimum")) {
						out.print(statistics.getMin());
					} else if (mode.equals("maximum")) {
						out.print(statistics.getMax());
					} else if (mode.equals("average")) {
						out.print(statistics.getMean());
					} else if (mode.equals("stdev")) {
						out.print(statistics.getStandardDeviation());
					} else if (mode.equals("count")) {
						out.print(statistics.getN());
					} else {
						throw new IllegalArgumentException("unknown mode: " +
								mode);
					}
				}
				
				out.println();
			}
		} finally {
			if ((out != null) && (out != System.out)) {
				out.close();
			}
		}
	}
	
	/**
	 * Starts the command line utility for computing statistics across multiple
	 * data files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SimpleStatistics().start(args);
	}

}
