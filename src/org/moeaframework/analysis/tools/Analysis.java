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
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.MetricFileReader;
import org.moeaframework.analysis.io.MetricFileWriter;
import org.moeaframework.analysis.io.Parameter;
import org.moeaframework.analysis.io.ParameterFile;
import org.moeaframework.analysis.io.SampleReader;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for calculating the best, probability of attainment, efficiency and controllability metrics.
 * <p>
 * References:
 * <ol>
 *   <li>Hadka, D. and Reed, P.  "Diagnostic Assessment of Search Controls and Failure Modes in Many-Objective
 *       Evolutionary Optimization."  Evolutionary Computation.
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
	 * Constructs the command line utility for calculating the best, probability of attainment, efficiency and
	 * controllability metrics.
	 */
	public Analysis() {
		super();
	}
	
	/**
	 * Returns an array of the parameters in the same order as they appear in {@code parameterFile}.
	 * 
	 * @param properties the parameters
	 * @return an array of the parameters in the same order as they appear in {@code parameterFile}
	 */
	private double[] toArray(TypedProperties properties) {
		double[] result = new double[parameterFile.size()];
		
		for (int i=0; i<parameterFile.size(); i++) {
			String name = parameterFile.get(i).getName();
			result[i] = properties.getDouble(name);
		}
		
		return result;
	}
	
	/**
	 * Returns the normalized contents of the specified metric file.  Normalization converts all metrics to reside in
	 * the range {@code [0, 1]}, with {@code 1} representing the optimal metric value.
	 * 
	 * @param file the metric file
	 * @return the normalized contents of the specified metric file
	 * @throws IOException if an I/O error occurred
	 */
	private double[][] loadMetrics(File file) throws IOException {
		List<double[]> metricList = new ArrayList<double[]>();
		
		try (MetricFileReader reader = new MetricFileReader(file)) {
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
		}
		
		return metricList.toArray(double[][]::new);
	}
	
	/**
	 * Returns the parameters from the specified file.
	 * 
	 * @param file the parameter file
	 * @return the parameters from the specified file
	 * @throws IOException if an I/O error occurred
	 */
	private double[][] loadParameters(File file) throws IOException {
		List<double[]> parameterList = new ArrayList<double[]>();
		
		try (SampleReader reader = new SampleReader(file, parameterFile)) {
			while (reader.hasNext()) {
				parameterList.add(toArray(reader.next()));
			}
		}
		
		return parameterList.toArray(double[][]::new);
	}
	
	/**
	 * Normalizes the parameters, bounding all entries inside the unit hypervolume.  The parameters are normalized in
	 * place, modifying the array passed as an argument.
	 * 
	 * @param parameters the parameters to normalize
	 * @return the normalized parameters
	 */
	private double[][] normalize(double[][] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			for (int j = 0; j < parameters[i].length; j++) {
				Parameter parameter = parameterFile.get(j);
				
				parameters[i][j] = (parameters[i][j] - parameter.getLowerBound()) / 
						(parameter.getUpperBound() - parameter.getLowerBound());
			}
		}
		
		return parameters;
	}
	
	/**
	 * Returns only those parameters with a corresponding metric value meeting or exceeding a threshold value.
	 * 
	 * @param metric the metric
	 * @param threshold the threshold value
	 * @return only those parameters with a corresponding metric value meeting or exceeding a threshold value
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
				result[count] = parameters[i].clone();
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
	 * Returns the probability of attaining metric values meeting or exceeding the threshold value.
	 * 
	 * @return the probability of attaining metric values meeting or exceeding the threshold value
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
	 * Returns the measure of controllability, which is the correlation dimension of the parameters whose corresponding
	 * metric value meets or exceeds the threshold value.
	 * 
	 * @return the measure of controllability
	 */
	private double calculateControllability() {
		double[][] attainmentVolume = threshold(metric, threshold);

		return FractalDimension.computeDimension(normalize(attainmentVolume)) /
				FractalDimension.computeDimension(parameters);
	}
	
	/**
	 * Returns the measure of efficiency, which is the lowest NFE band where over 90% of its parameters met or exceeded
	 * the threshold value.
	 * 
	 * @return the measure of efficiency
	 * @throws FrameworkException if {@code maxEvaluations} is not a parameter
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
				if ((parameters[j][evalIndex] >= i) && (parameters[j][evalIndex] <= i+bandWidth-1)) {
					total++;
					
					if (metrics[j][metric] > threshold) {
						count++;
					}
				}
			}
			
			if (count/(double)total >= 0.9) {
				band = i;
				break;
			}
		}

		return (max - band) / (double)max;
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("p")
				.longOpt("parameterFile")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("i")
				.longOpt("parameters")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("m")
				.longOpt("metric")
				.hasArg()
				.argName("value")
				.required()
				.build());
		options.addOption(Option.builder("t")
				.longOpt("hypervolume")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("e")
				.longOpt("efficiency")
				.build());
		options.addOption(Option.builder("b")
				.longOpt("band")
				.hasArg()
				.argName("width")
				.build());
		options.addOption(Option.builder("c")
				.longOpt("controllability")
				.build());
		options.addOption(Option.builder("t")
				.longOpt("threshold")
				.hasArg()
				.argName("percent")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		//parse required parameters
		parameterFile = new ParameterFile(new File(commandLine.getOptionValue("parameterFile")));
		parameters = loadParameters(new File(commandLine.getOptionValue("parameters")));
		metric = MetricFileWriter.getMetricIndex(commandLine.getOptionValue("metric"));
		
		//parse optional parameters
		if (commandLine.hasOption("band")) {
			bandWidth = Integer.parseInt(commandLine.getOptionValue("band"));
		}
		
		if (commandLine.hasOption("threshold")) {
			threshold = Double.parseDouble(commandLine.getOptionValue("threshold"));
		}
		
		//if analyzing hypervolume, require the hypervolume option
		if (metric == 0) {
			if (commandLine.hasOption("hypervolume")) {
				threshold *= Double.parseDouble(commandLine.getOptionValue("hypervolume"));
			} else {
				throw new MissingOptionException("requires hypervolume option");
			}
		}
		
		try (OutputLogger output = new OutputLogger(commandLine.getOptionValue("output"))) {
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
		}
	}
	
	/**
	 * Starts the command line utility for calculating the best, probability of attainment, efficiency and
	 * controllability metrics.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Analysis().start(args);
	}

}
