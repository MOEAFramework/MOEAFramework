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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.Timer;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.validate.Validate;

/**
 * Command line utility for evaluating an algorithm using many parameterizations.  Unlike {@link EndOfRunEvaluator},
 * this class outputs runtime data.  Each run is stored in a separate file.
 */
public class RuntimeEvaluator extends CommandLineUtility {

	private RuntimeEvaluator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
		OptionUtils.addEpsilonOption(options);
		OptionUtils.addPropertiesOption(options);

		options.addOption(Option.builder("p")
				.longOpt("parameterFile")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("i")
				.longOpt("input")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("a")
				.longOpt("algorithm")
				.hasArg()
				.argName("name")
				.required()
				.build());
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("f")
				.longOpt("frequency")
				.hasArg()
				.argName("nfe")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException, ParseException {
		File parameterFile = new File(commandLine.getOptionValue("parameterFile"));
		File inputFile = new File(commandLine.getOptionValue("input"));
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		String outputFilePattern = commandLine.getOptionValue("output");
		Frequency frequency = Frequency.ofEvaluations(1000);
		
		if (commandLine.hasOption("frequency")) {
			frequency = Frequency.ofEvaluations(Integer.parseInt(commandLine.getOptionValue("frequency")));
		}
		
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}
		
		ParameterSet parameterSet = ParameterSet.load(parameterFile);
		Samples samples = Samples.load(inputFile, parameterSet);
		
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, false)) {
			for (int i = 0; i < samples.size(); i++) {
				String outputFileName = String.format(outputFilePattern, i+1);
				File outputFile = new File(outputFileName);
				
				System.out.print("Processing sample " + (i+1) + " of " + samples.size() + " (" + outputFileName + ")...");
				Timer timer = Timer.startNew();
						
				try (ResultFileWriter output = ResultFileWriter.open(problem, outputFile)) {
					TypedProperties defaultProperties = OptionUtils.getProperties(commandLine);
	
					if (epsilons != null) {
						defaultProperties.setDoubleArray("epsilon", epsilons.toArray());
					}
	
					TypedProperties properties = samples.get(i);
					properties.addAll(defaultProperties);
	
					process(commandLine.getOptionValue("algorithm"), properties, problem, frequency, output);
				}
				
				System.out.print("done!");
				
				Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
				System.out.print(" (");
				System.out.print(DurationUtils.formatHighResolution(elapsedTime));
				System.out.println(")");
			}
		}
		
		System.out.println("Finished!");
	}

	private void process(String algorithmName, TypedProperties properties, Problem problem, Frequency frequency,
			ResultFileWriter output) throws IOException {
		int maxEvaluations = properties.getTruncatedInt("maxEvaluations");
		Validate.that("maxEvaluations", maxEvaluations).isGreaterThanOrEqualTo(0);
		
		Instrumenter instrumenter = new Instrumenter()
				.withFrequency(frequency)
				.attachElapsedTimeCollector();
		
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties, problem);
		InstrumentedAlgorithm<?> instrumentedAlgorithm = instrumenter.instrument(algorithm);
		
		instrumentedAlgorithm.run(maxEvaluations);
		
		ResultSeries series = instrumentedAlgorithm.getSeries();

		for (IndexedResult result : series) {
			output.write(result.getEntry());
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new RuntimeEvaluator().start(args);
	}

}
