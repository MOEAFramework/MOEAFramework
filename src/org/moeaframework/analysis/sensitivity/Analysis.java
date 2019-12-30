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
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for calculating the best, probability of attainment,
 * efficiency and controllability metrics.  These search control metrics are
 * discussed in detail in [1].
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.Analysis <options> <files>}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
 *   <tr>
 *     <td>{@code -p, --parameterFile}</td>
 *     <td>Location of the parameter configuration file (required)</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -i, --parameters}</td>
 *     <td>Location of the parameter sample file (required)</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -m, --metric}</td>
 *     <td>The metric index (or column) to use for the analysis (required).
 *         Indices are 0-based, so 0 indicates the first column in the input.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -v, --hypervolume}</td>
 *     <td>The target or ideal hypervolume value, used to normalize the
 *         hypervolume in the analysis.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>Location where the output file containing the analysis results is
 *         saved.  Of not given, the output is printed to the console.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -e, --efficiency}</td>
 *     <td>Include the efficiency metric in the analysis results.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -b, --band}</td>
 *     <td>The width of the NFE band.  The efficiency metric groups samples
 *         with similar NFE together (i.e., those in the same band).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -c, --controllability}</td>
 *     <td>Include the controllability metric in the analysis results.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -t, --threshold}</td>
 *     <td>The performance threshold as a percentage when computing the
 *         attainment, efficiency, and controllability metrics.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code <files>}</td>
 *     <td>One or more files containing the metric results (i.e., the output
 *         from {@link ResultFileEvaluator}).
 * </table>
 * <p>
 * References:
 * <ol>
 *   <li>Hadka, D. and Reed, P.  "Diagnostic Assessment of Search Controls and
 *       Failure Modes in Many-Objective Evolutionary Optimization."
 *       Evolutionary Computation.
 * </ol>
 */
public class Analysis extends CommandLineUtility {
	
	/**
	 * The parameter description file.
	 */
	private ParameterFile parameterFile;

	/**
	 * The metrics.
	 */
	private double[][] metrics;
	
	/**
	 * The parameters.
	 */
	private double[][] parameters;
	
	/**
	 * The index of the metric being analyzed.
	 */
	private int metric;
	
	/**
	 * The threshold value.
	 */
	private double threshold = 0.75;
	
	/**
	 * The width of NFE bands when calculating efficiency.
	 */
	private int bandWidth = 10000;
	
	/**
	 * Constructs the command line utility for calculating the best, probability
	 * of attainment, efficiency and controllability metrics.
	 */
	public Analysis() {
		super();
	}
	
	/**
	 * Returns an array of the parameters in the same order as they appear in
	 * {@code parameterFile}.
	 * 
	 * @param properties the parameters
	 * @return an array of the parameters in the same order as they appear in
	 *         {@code parameterFile}
	 */
	private double[] toArray(Properties properties) {
		double[] result = new double[parameterFile.size()];
		
		for (int i=0; i<parameterFile.size(); i++) {
			result[i] = Double.parseDouble(properties.getProperty(
					parameterFile.get(i).getName()));
		}
		
		return result;
	}
	
	/**
	 * Returns the normalized contents of the specified metric file.
	 * Normalization converts all metrics to reside in the range {@code [0, 1]},
	 * with {@code 1} representing the optimal metric value.
	 * 
	 * @param file the metric file
	 * @return the normalized contents of the specified metric file
	 * @throws IOException if an I/O error occurred
	 */
	private double[][] loadMetrics(File file) throws IOException {
		MetricFileReader reader = null;
		List<double[]> metricList = new ArrayList<double[]>();
		
		try {
			reader = new MetricFileReader(file);
			
			while (reader.hasNext()) {
				double[] metrics = reader.next();
				
				//normalize metrics to be in [0, 1] with 1 the ideal result
				for (int i=0; i<metrics.length; i++) {
					if ((i == 1) || (i == 2) || (i == 4) || (i == 5) || (i == 6)) {
						metrics[i] = Math.max(0.0, 1.0 - metrics[i]);
					}
				}
				
				metricList.add(metrics);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return metricList.toArray(new double[0][]);
	}
	
	/**
	 * Returns the parameters from the specified file.
	 * 
	 * @param file the parameter file
	 * @return the parameters from the specified file
	 * @throws IOException if an I/O error occurred
	 */
	private double[][] loadParameters(File file) throws IOException {
		SampleReader reader = null;
		List<double[]> parameterList = new ArrayList<double[]>();
		
		try {
			reader = new SampleReader(file, parameterFile);
			
			while (reader.hasNext()) {
				parameterList.add(toArray(reader.next()));
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return parameterList.toArray(new double[0][]);
	}
	
	/**
	 * Normalizes the parameters, bounding all entries inside the unit 
	 * hypervolume.  The parameters are normalized in place, modifying the
	 * array passed as an argument.
	 * 
	 * @param parameters the parameters to normalize
	 * @return the normalized parameters
	 */
	private double[][] normalize(double[][] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			for (int j = 0; j < parameters[i].length; j++) {
				Parameter parameter = parameterFile.get(j);
				
				parameters[i][j] = 
						(parameters[i][j] - parameter.getLowerBound()) / 
						(parameter.getUpperBound() - parameter.getLowerBound());
			}
		}
		
		return parameters;
	}
	
	/**
	 * Returns only those parameters with a corresponding metric value meeting
	 * or exceeding a threshold value.
	 * 
	 * @param metric the metric
	 * @param threshold the threshold value
	 * @return only those parameters with a corresponding metric value meeting
	 *         or exceeding a threshold value
	 */
	private double[][] threshold(int metric, double threshold) {
		int count = 0;
		
		for (int i = 0; i < metrics.length; i++) {
			if (metrics[i][metric] >= threshold) {
				count++;
			}
		}

		double[][] result = new double[count][];
		count = 0;
		
		for (int i = 0; i < metrics.length; i++) {
			if (metrics[i][metric] >= threshold) {
				result[count] = parameters[i];
				count++;
			}
		}

		return result;
	}

	/**
	 * Returns the best achieved metric value.
	 * 
	 * @return the best achieved metric value
	 */
	private double calculateBest() {
		double best = 0.0;
				
		for (int i = 0; i < metrics.length; i++) {
			best = Math.max(metrics[i][metric], best);
		}

		return best;
	}

	/**
	 * Returns the probability of attaining metric values meeting or exceeding
	 * the threshold value.
	 * 
	 * @return the probability of attaining metric values meeting or exceeding
	 *         the threshold value
	 */
	private double calculateAttainment() {
		int count = 0;

		for (int i = 0; i < metrics.length; i++) {
			if (metrics[i][metric] >= threshold) {
				count++;
			}
		}

		return count / (double)metrics.length;
	}
	
	/**
	 * Returns the measure of controllability, which is the correlation 
	 * dimension of the parameters whose corresponding metric value meets or 
	 * exceeds the threshold value.
	 * 
	 * @return the measure of controllability
	 */
	private double calculateControllability() {
		double[][] attainmentVolume = threshold(metric, threshold);

		return FractalDimension.computeDimension(normalize(attainmentVolume)) /
				FractalDimension.computeDimension(parameters);
	}
	
	/**
	 * Returns the measure of efficiency, which is the lowest NFE band where
	 * over 90% of its parameters met or exceeded the threshold value.  This
	 * method returns {@code -1} if {@code maxEvaluations} is not a parameter.
	 * 
	 * @return the measure of efficiency
	 */
	private double calculateEfficiency() {
		//find the max evaluations parameter index
		int max = -1;
		int evalIndex = -1;

		for (int i=0; i<parameterFile.size(); i++) {
			Parameter parameter = parameterFile.get(i);
			
			if (parameter.getName().equals("maxEvaluations")) {
				max = (int)parameter.getUpperBound();
				evalIndex = i;
				break;
			}
		}
		
		if (evalIndex == -1) {
			throw new FrameworkException("no maxEvaluations parameter");
		}

		//find lowest band reaching attainment
		int band = max;

		for (int i=0; i<=max-bandWidth; i+=bandWidth) {
			int count = 0;
			int total = 0;
			
			for (int j=0; j<metrics.length; j++) {
				if ((parameters[j][evalIndex] >= i) && 
						(parameters[j][evalIndex] <= i+bandWidth-1)) {
					total++;
					
					if (metrics[j][metric] > threshold) {
						count++;
						
						if (count/(double)total >= 0.9) {
							band = i;
							break;
						}
					}
				}
			}
		}
		
		return (max - band) / (double)max;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("parameterFile")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('p'));
		options.addOption(OptionBuilder
				.withLongOpt("parameters")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('i'));
		options.addOption(OptionBuilder
				.withLongOpt("metric")
				.hasArg()
				.withArgName("value")
				.isRequired()
				.create('m'));
		options.addOption(OptionBuilder
				.withLongOpt("hypervolume")
				.hasArg()
				.withArgName("value")
				.create('t'));
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("efficiency")
				.create('e'));
		options.addOption(OptionBuilder
				.withLongOpt("band")
				.hasArg()
				.withArgName("width")
				.create('b'));
		options.addOption(OptionBuilder
				.withLongOpt("controllability")
				.create('c'));
		options.addOption(OptionBuilder
				.withLongOpt("threshold")
				.hasArg()
				.withArgName("percent")
				.create('t'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		PrintStream output = null;
		
		//parse required parameters
		parameterFile = new ParameterFile(new File(
				commandLine.getOptionValue("parameterFile")));
		parameters = loadParameters(new File(
				commandLine.getOptionValue("parameters")));
		metric = Integer.parseInt(commandLine.getOptionValue("metric"));
		
		//parse optional parameters
		if (commandLine.hasOption("band")) {
			bandWidth = Integer.parseInt(commandLine.getOptionValue("band"));
		}
		
		if (commandLine.hasOption("threshold")) {
			threshold = Double.parseDouble(commandLine.getOptionValue(
					"threshold"));
		}
		
		//if analyzing hypervolume, require the hypervolume option
		if (metric == 0) {
			if (commandLine.hasOption("hypervolume")) {
				threshold *= Double.parseDouble(commandLine.getOptionValue(
						"hypervolume"));
			} else {
				throw new MissingOptionException("requires hypervolume option");
			}
		}
		
		try {
			//setup the output stream
			if (commandLine.hasOption("output")) {
				output = new PrintStream(new File(
						commandLine.getOptionValue("output")));
			} else {
				output = System.out;
			}
			
			//process all the files listed on the command line
			String[] filenames = commandLine.getArgs();
			
			for (int i=0; i<filenames.length; i++) {
				if (i > 0) {
					output.println();
				}
				
				metrics = loadMetrics(new File(filenames[i]));
				
				output.print(filenames[i]);
				output.println(":");
				output.print("  Best: ");
				output.println(calculateBest());
				output.print("  Attainment: ");
				output.println(calculateAttainment());
				
				if (commandLine.hasOption("controllability")) {
					output.print("  Controllability: ");
					output.println(calculateControllability());
				}
				
				if (commandLine.hasOption("efficiency")) {
					output.print("  Efficiency: ");
					output.println(calculateEfficiency());
				}
			}
		} finally {
			if ((output != null) && (output != System.out)) {
				output.close();
			}
		}
	}
	
	/**
	 * Starts the command line utility for calculating the best, probability 
	 * of attainment, efficiency and controllability metrics.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Analysis().start(args);
	}

}
