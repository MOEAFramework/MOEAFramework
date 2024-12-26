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
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.sequence.LatinHypercube;
import org.moeaframework.util.sequence.Saltelli;
import org.moeaframework.util.sequence.Sequence;
import org.moeaframework.util.sequence.Sobol;
import org.moeaframework.util.sequence.Uniform;
import org.moeaframework.util.validate.Validate;

/**
 * Command line utility for producing randomly-generated parameters for use by the {@link EndOfRunEvaluator} or
 * {@link RuntimeEvaluator}.  The output is called a parameter sample file.
 */
public class SampleGenerator extends CommandLineUtility {

	private SampleGenerator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("n")
				.longOpt("numberOfSamples")
				.hasArg()
				.argName("value")
				.required()
				.build());
		options.addOption(Option.builder("p")
				.longOpt("parameterFile")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("m")
				.longOpt("method")
				.hasArg()
				.argName("name")
				.required()
				.build());
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder()
				.longOpt("overwrite")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		File parameterFile = new File(commandLine.getOptionValue("parameterFile"));
		ParameterSet parameterSet = ParameterSet.load(parameterFile);

		int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));
		
		Validate.that("numberOfSamples", N).isGreaterThan(0);
		Validate.that("numberOfParameters", parameterSet.size()).isGreaterThan(0);
		
		Sequence sequence = null;

		if (commandLine.hasOption("method")) {
			OptionCompleter completer = new OptionCompleter("uniform", "latin", "sobol", "saltelli");
			String method = completer.lookup(commandLine.getOptionValue("method"));
			
			if (method == null) {
				Validate.that("method", commandLine.getOptionValue("method")).failUnsupportedOption(completer.getOptions());
			}
			
			switch (method) {
				case "uniform" -> sequence = new Uniform();
				case "latin" -> sequence = new LatinHypercube();
				case "sobol" -> sequence = new Sobol();
				case "saltelli" -> {
					N *= (2 * parameterSet.size() + 2);
					sequence = new Saltelli();
				}
				default -> throw new IllegalStateException();
			}
		} else {
			sequence = new Sobol();
		}

		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}
		
		if (commandLine.hasOption("output") && Files.exists(Path.of(commandLine.getOptionValue("output"))) &&
				!commandLine.hasOption("overwrite")) {
			System.err.println("Output file exists, skipping sample generation.  Add --overwrite to regenerate");
			System.err.println("the samples.");
			return;
		}

		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			Samples samples = parameterSet.sample(N, sequence);
			samples.save(output);
		}
	}

	/**
	 * Command line utility for producing randomly-generated parameters for use by the {@link EndOfRunEvaluator}.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SampleGenerator().start(args);
	}

}
