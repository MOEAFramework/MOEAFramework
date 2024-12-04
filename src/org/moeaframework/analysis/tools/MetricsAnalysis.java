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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.MetricFileReader;
import org.moeaframework.analysis.io.MetricFileWriter;
import org.moeaframework.analysis.parameter.NumericParameter;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for calculating the best, probability of attainment, efficiency and controllability metrics.
 * <p>
 * References:
 * <ol>
 *   <li>Hadka, D. and Reed, P.  "Diagnostic Assessment of Search Controls and Failure Modes in Many-Objective
 *       Evolutionary Optimization."  Evolutionary Computation, 20(3):423-452, 2012.
 * </ol>
 */
public class MetricsAnalysis extends CommandLineUtility {
	
	/**
	 * The parameter set.
	 */
	private ParameterSet parameterSet;
	
	/**
	 * The parameter samples.
	 */
	private Samples samples;

	/**
	 * The metrics.
	 */
	private double[][] metrics;
	
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
	
	private MetricsAnalysis() {
		super();
	}
	
	/**
	 * Returns an array of the parameter values in the same order as they appear in {@code parameterFile}.
	 * 
	 * @param sample the sample
	 * @return an array of the parameter values in the same order as they appear in {@code parameterFile}
	 */
	private double[] toArray(Sample sample) {
		double[] result = new double[parameterSet.size()];
		
		for (int i=0; i<parameterSet.size(); i++) {
			NumericParameter<?> parameter = (NumericParameter<?>)parameterSet.get(i);
			result[i] = parameter.readValue(sample).doubleValue();
		}
		
		return result;
	}
	
	/**
	 * Returns a matrix of the parameter values.
	 * 
	 * @param samples the samples
	 * @return the matrix of parameter values
	 */
	private double[][] toMatrix(Samples samples) {
		double[][] result = new double[samples.size()][];
		
		for (int i = 0; i < samples.size(); i++) {
			result[i] = toArray(samples.get(i));
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
	 * Normalizes the parameters, bounding all entries inside the unit hypervolume.  The parameters are normalized in
	 * place, modifying the array passed as an argument.
	 * 
	 * @param parameters the parameters to normalize
	 * @return the normalized parameters
	 */
	private double[][] normalize(double[][] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			for (int j = 0; j < parameters[i].length; j++) {
				NumericParameter<?> parameter = (NumericParameter<?>)parameterSet.get(j);
				
				parameters[i][j] = (parameters[i][j] - parameter.getLowerBound().doubleValue()) /
						(parameter.getUpperBound().doubleValue() - parameter.getLowerBound().doubleValue());
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
	private double[][] threshold(int metric, double threshold, double[][] parameters) {
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
		double[][] parameters = toMatrix(samples);
		double[][] attainmentVolume = threshold(metric, threshold, parameters);

		return FractalDimension.computeDimension(normalize(attainmentVolume)) /
				FractalDimension.computeDimension(toMatrix(samples));
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

		for (int i=0; i<parameterSet.size(); i++) {
			NumericParameter<?> parameter = (NumericParameter<?>)parameterSet.get(i);
			
			if (parameter.getName().equals("maxEvaluations")) {
				max = parameter.getUpperBound().intValue();
				evalIndex = i;
				break;
			}
		}
		
		if (evalIndex == -1) {
			throw new FrameworkException("no maxEvaluations parameter");
		}

		//find lowest band reaching attainment
		double[][] parameters = toMatrix(samples);
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
		parameterSet = ParameterSet.load(new File(commandLine.getOptionValue("parameterFile")));
		
		for (Parameter<?> parameter : parameterSet) {
			if (!(parameter instanceof NumericParameter<?>)) {
				throw new FrameworkException("only supports numeric parameters");
			}
		}
		
		samples = Samples.load(new File(commandLine.getOptionValue("parameters")), parameterSet);
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
		
		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
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
		new MetricsAnalysis().start(args);
	}

}
