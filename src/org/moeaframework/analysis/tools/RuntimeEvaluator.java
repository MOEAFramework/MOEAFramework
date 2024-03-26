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
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.ApproximationSetCollector;
import org.moeaframework.analysis.collector.ElapsedTimeCollector;
import org.moeaframework.analysis.collector.Observation;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.analysis.io.ParameterFile;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.io.ResultFileWriter.ResultFileWriterSettings;
import org.moeaframework.analysis.io.SampleReader;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for evaluating an algorithm using many parameterizations.  Unlike {@link Evaluator}, this class
 * outputs runtime data.  Each run is stored in a separate file.
 */
public class RuntimeEvaluator extends CommandLineUtility {

	/**
	 * Constructs the command line utility for evaluating an algorithm using many parameterizations.
	 */
	public RuntimeEvaluator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options, false);
		OptionUtils.addEpsilonOption(options);

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
		options.addOption(Option.builder("f")
				.longOpt("frequency")
				.hasArg()
				.argName("nfe")
				.build());
		options.addOption(Option.builder("x")
				.longOpt("properties")
				.hasArgs()
				.argName("p1=v1;p2=v2;...")
				.valueSeparator(';')
				.build());
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("n")
				.longOpt("novariables")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		String outputFilePattern = commandLine.getOptionValue("output");
		ParameterFile parameterFile = new ParameterFile(new File(commandLine.getOptionValue("parameterFile")));
		File inputFile = new File(commandLine.getOptionValue("input"));
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		
		int frequency = 1000;
		
		if (commandLine.hasOption("frequency")) {
			frequency = Integer.parseInt(commandLine.getOptionValue("frequency"));
		}
		
		// open the resources and begin processing
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, false);
				SampleReader input = new SampleReader(new FileReader(inputFile), parameterFile)) {
			int count = 1;

			while (input.hasNext()) {
				String outputFileName = String.format(outputFilePattern, count);
				System.out.print("Processing " + outputFileName + "...");
				File outputFile = new File(outputFileName);
						
				if (outputFile.exists()) {
					outputFile.delete();
				}	
						
				try (ResultFileWriter output = new ResultFileWriter(problem, outputFile,
						ResultFileWriterSettings.from(commandLine))) {
					// setup any default parameters
					TypedProperties defaultProperties = new TypedProperties();
	
					if (commandLine.hasOption("properties")) {
						for (String property : commandLine.getOptionValues("properties")) {
							String[] tokens = property.split("=");
								
							if (tokens.length == 2) {
								defaultProperties.setString(tokens[0], tokens[1]);
							} else {
								throw new FrameworkException("malformed property argument");
							}
						}
					}
	
					if (epsilons != null) {
						defaultProperties.setDoubleArray("epsilon", epsilons.toArray());
					}
	
					// seed the pseudo-random number generator
					if (commandLine.hasOption("seed")) {
						PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
					}
	
					TypedProperties properties = input.next();
					properties.addAll(defaultProperties);
	
					process(commandLine.getOptionValue("algorithm"), properties, problem, frequency, output);
						
					System.out.println("done.");
				}
					
				count++;
			}
		}
		
		System.out.println("Finished!");
	}

	private void process(String algorithmName, TypedProperties properties, Problem problem, int frequency,
			ResultFileWriter output) throws IOException {
		int maxEvaluations = properties.getTruncatedInt("maxEvaluations");
		
		Validate.greaterThanOrEqualToZero("maxEvaluations", maxEvaluations);
		
		Instrumenter instrumenter = new Instrumenter()
				.withProblem(problem)
				.withFrequency(frequency)
				.attachApproximationSetCollector()
				.attachElapsedTimeCollector();

		new Executor()
				.withSameProblemAs(instrumenter)
				.withAlgorithm(algorithmName)
				.withMaxEvaluations(maxEvaluations)
				.withInstrumenter(instrumenter)
				.withProperties(properties)
				.run();

		Observations observations = instrumenter.getObservations();

		for (Observation observation : observations) {
			TypedProperties metadata = new TypedProperties();
			metadata.setInt("NFE", observation.getNFE());
			metadata.setDouble("ElapsedTime", ElapsedTimeCollector.getElapsedTime(observation));
			
			NondominatedPopulation result = ApproximationSetCollector.getApproximationSet(observation);
			
			output.append(new ResultEntry(result, metadata));
		}
	}

	/**
	 * Starts the command line utility for evaluating an algorithm using many parameterizations.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new RuntimeEvaluator().start(args);
	}

}
