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
import java.nio.file.Files;
import java.time.Duration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.Timer;
import org.moeaframework.util.validate.Validate;

/**
 * Command line utility for evaluating an algorithm using many parameterizations.
 */
public class EndOfRunEvaluator extends CommandLineUtility {

	private EndOfRunEvaluator() {
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
		options.addOption(Option.builder()
				.longOpt("overwrite")
				.build());
		options.addOption(Option.builder()
				.longOpt("force")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		File parameterFile = new File(commandLine.getOptionValue("parameterFile"));
		File outputFile = new File(commandLine.getOptionValue("output"));
		File inputFile = new File(commandLine.getOptionValue("input"));
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		
		if (commandLine.hasOption("overwrite")) {
			Files.deleteIfExists(outputFile.toPath());
		}
		
		if (!commandLine.hasOption("force")) {
			ResultFileWriter.failIfOutdated(this, inputFile, outputFile);
		}
		
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}
		
		ParameterSet parameterSet = ParameterSet.load(parameterFile);
		Samples samples = Samples.load(inputFile, parameterSet);

		try (Problem problem = OptionUtils.getProblemInstance(commandLine, false);
				ResultFileWriter output = ResultFileWriter.append(problem, outputFile)) {
			TypedProperties defaultProperties = OptionUtils.getProperties(commandLine);

			if (epsilons != null) {
				defaultProperties.setDoubleArray("epsilon", epsilons.toArray());
			}
			
			if (output.getNumberOfEntries() > 0) {
				System.out.println("Resuming from existing result file " + outputFile);
				System.out.println(output.getNumberOfEntries() + " valid entries");
			}

			for (int i = output.getNumberOfEntries(); i < samples.size(); i++) {
				System.out.print("Processing sample " + (i+1) + " of " + samples.size() + "...");
				
				Timer timer = Timer.startNew();
				TypedProperties properties = samples.get(i);
				properties.addAll(defaultProperties);

				process(commandLine.getOptionValue("algorithm"), properties, problem, output);
				
				System.out.print("done!");
				
				Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
				System.out.print(" (");
				System.out.print(DurationUtils.formatHighResolution(elapsedTime));
				System.out.println(")");
			}
		}
		
		System.out.println("Finished!");
	}

	private void process(String algorithmName, TypedProperties properties, Problem problem, ResultFileWriter output)
			throws IOException {
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(algorithmName, properties, problem);

		// find the maximum NFE to run
		int maxEvaluations = properties.getTruncatedInt("maxEvaluations");
		Validate.that("maxEvaluations", maxEvaluations).isGreaterThanOrEqualTo(0);

		// run the algorithm
		Timer timer = Timer.startNew();
		algorithm.run(maxEvaluations);
		timer.stop();

		// extract the result
		NondominatedPopulation result = algorithm.getResult();

		// apply epsilon-dominance if required
		if (properties.contains("epsilon")) {
			Epsilons epsilons = new Epsilons(properties.getDoubleArray("epsilon"));
			result = EpsilonBoxDominanceArchive.of(result, epsilons);
		}

		// record metadata
		TypedProperties metadata = new TypedProperties();
		metadata.setInt(ResultEntry.NFE, algorithm.getNumberOfEvaluations());
		metadata.setDouble(ResultEntry.ElapsedTime, timer.getElapsedTime());

		// write result to output
		output.write(new ResultEntry(result, metadata));
	}

	/**
	 * Starts the command line utility for evaluating an algorithm using many parameterizations.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new EndOfRunEvaluator().start(args);
	}

}
