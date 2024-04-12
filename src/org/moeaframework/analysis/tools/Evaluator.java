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
import org.moeaframework.analysis.io.MetricFileWriter;
import org.moeaframework.analysis.io.OutputWriter;
import org.moeaframework.analysis.io.ParameterFile;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.io.ResultFileWriter.ResultFileWriterSettings;
import org.moeaframework.analysis.io.SampleReader;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.TimingProblem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for evaluating an algorithm using many parameterizations.
 */
public class Evaluator extends CommandLineUtility {

	/**
	 * The output writer where end-of-run results are stored.
	 */
	protected OutputWriter output;

	/**
	 * Constructs the command line utility for evaluating an algorithm using many parameterizations.
	 */
	public Evaluator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options, false);
		OptionUtils.addReferenceSetOption(options);
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
		options.addOption(Option.builder("m")
				.longOpt("metrics")
				.build());
		options.addOption(Option.builder("n")
				.longOpt("novariables")
				.build());
		options.addOption(Option.builder("f")
				.longOpt("force")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		File outputFile = new File(commandLine.getOptionValue("output"));
		File inputFile = new File(commandLine.getOptionValue("input"));
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);

		ParameterFile parameterFile = new ParameterFile(new File(commandLine.getOptionValue("parameterFile")));

		// sanity check to ensure input hasn't been modified after the output
		if (!commandLine.hasOption("force") && (outputFile.lastModified() > 0L) && 
				(inputFile.lastModified() > outputFile.lastModified())) {
			throw new FrameworkException("input appears to be newer than output");
		}

		// open the resources and begin processing
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, false);
				SampleReader input = new SampleReader(new FileReader(inputFile), parameterFile)) {
			try {
				if (commandLine.hasOption("metrics")) {
					NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine);
					Indicators indicators = Indicators.standard(problem, referenceSet);

					output = MetricFileWriter.append(indicators, outputFile);
				} else {
					output = new ResultFileWriter(problem, outputFile, ResultFileWriterSettings.from(commandLine));
				}

				// resume at the last good output
				for (int i = 0; i < output.getNumberOfEntries(); i++) {
					if (input.hasNext()) {
						input.next();
					} else {
						throw new FrameworkException("output has more entries than input");
					}
				}

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

				// process the remaining runs
				while (input.hasNext()) {
					TypedProperties properties = input.next();
					properties.addAll(defaultProperties);

					process(commandLine.getOptionValue("algorithm"), properties, problem);
				}
			} finally {
				if (output != null) {
					output.close();
				}
			}
		}
	}

	/**
	 * Performs a single run of the specified algorithm using the parameters.
	 * 
	 * @param algorithmName the algorithm name
	 * @param properties the parameters stored in a properties object
	 * @param problem the problem being evaluated
	 * @throws IOException if an I/O error occurred
	 */
	protected void process(String algorithmName, TypedProperties properties, Problem problem) throws IOException {
		// instrument the problem to record timing information
		TimingProblem timingProblem = new TimingProblem(problem);
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties, timingProblem);

		// find the maximum NFE to run
		int maxEvaluations = properties.getTruncatedInt("maxEvaluations");
		Validate.greaterThanOrEqualToZero("maxEvaluations", maxEvaluations);

		// run the algorithm
		long startTime = System.nanoTime();
		algorithm.run(maxEvaluations);
		long endTime = System.nanoTime();

		// extract the result and free any resources
		NondominatedPopulation result = algorithm.getResult();
		algorithm.terminate();

		// apply epsilon-dominance if required
		if (properties.contains("epsilon")) {
			Epsilons epsilons = new Epsilons(properties.getDoubleArray("epsilon"));
			result = EpsilonBoxDominanceArchive.of(result, epsilons);
		}

		// record instrumented data
		TypedProperties timingData = new TypedProperties();
		timingData.setDouble("EvaluationTime", timingProblem.getSeconds());
		timingData.setDouble("TotalTime", (endTime - startTime) / 1e9);

		// write result to output
		output.append(new ResultEntry(result, timingData));
	}

	/**
	 * Starts the command line utility for evaluating an algorithm using many parameterizations.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Evaluator().start(args);
	}

}
