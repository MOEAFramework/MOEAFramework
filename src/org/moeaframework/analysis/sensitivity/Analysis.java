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
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for calculating the best, probability of attainment,
 * efficiency and controllability metrics.  These search control metrics are
 * discussed in detail in [1].
 * <p>
 * References:
 * <ol>
 *   <li>Hadka, D. and Reed, P.  "Diagnostic Assessment of Search Controls and
 *       Failure Modes in Many-Objective Evolutionary Optimization."  In Review
 *       at Evolutionary Computation.
 * </ol>
 */
public class Analysis extends CommandLineUtility {
	
	/**
	 * The width of NFE bands when calculating efficiency.
	 */
	private static final int BAND = 10000;
	
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
	private double threshold;
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Analysis() {
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
				double[] parameters = toArray(reader.next());
				
				//normalize parameters to be in [0, 1]
				for (int i = 0; i < parameters.length; i++) {
					Parameter parameter = parameterFile.get(i);
					parameters[i] = (parameters[i] - parameter.getLowerBound())
							/ (parameter.getUpperBound() - parameter.getLowerBound());
				}
				
				parameterList.add(parameters);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return parameterList.toArray(new double[0][]);
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

		return FractalDimension.computeDimension(attainmentVolume) / 
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
		int max = 1000000;
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
			return -1;
		}

		//find lowest band reaching attainment
		int band = max;

		for (int i=0; i<=max-BAND; i+=BAND) {
			int count = 0;
			int total = 0;
			
			for (int j=0; j<metrics.length; j++) {
				if ((parameters[j][evalIndex] >= i) && 
						(parameters[j][evalIndex] <= i+BAND-1)) {
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
				.withDescription("Parameter file")
				.isRequired()
				.create('p'));
		options.addOption(OptionBuilder
				.withLongOpt("parameters")
				.hasArg()
				.withArgName("file")
				.withDescription("Parameter samples")
				.isRequired()
				.create('i'));
		options.addOption(OptionBuilder
				.withLongOpt("metric")
				.hasArg()
				.withArgName("value")
				.withDescription("Column in metric file to evaluate")
				.isRequired()
				.create('m'));
		options.addOption(OptionBuilder
				.withLongOpt("hypervolume")
				.hasArg()
				.withArgName("value")
				.withDescription("Ideal or target hypervolume")
				.create('t'));
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.withDescription("Output file")
				.create('o'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		PrintStream output = null;
		
		//setup the parameters
		parameterFile = new ParameterFile(new File(
				commandLine.getOptionValue("parameterFile")));
		parameters = loadParameters(new File(
				commandLine.getOptionValue("parameters")));
		metric = Integer.parseInt(commandLine.getOptionValue("metric"));
		threshold = 0.75;
		
		//if analyzing hypervolume, require the hypervolume option
		if (metric == 0) {
			if (commandLine.hasOption("hypervolume")) {
				threshold = 0.75 * Double.parseDouble(
						commandLine.getOptionValue("hypervolume"));
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
				
				output.println(filenames[i]);
				output.print("  Best: ");
				output.println(calculateBest());
				output.print("  Attainment: ");
				output.println(calculateAttainment());
				output.print("  Controllability: ");
				output.println(calculateControllability());
				output.print("  Efficiency: ");
				output.println(calculateEfficiency());
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
	 */
	public static void main(String[] args) {
		new Analysis().start(args);
	}

}
