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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ParameterFile;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.sequence.LatinHypercube;
import org.moeaframework.util.sequence.Saltelli;
import org.moeaframework.util.sequence.Sequence;
import org.moeaframework.util.sequence.Sobol;
import org.moeaframework.util.sequence.Uniform;

/**
 * Command line utility for producing randomly-generated parameters for use by the {@link Evaluator} or
 * {@link RuntimeEvaluator}.  The output is called a parameter sample file.
 */
public class SampleGenerator extends CommandLineUtility {

	/**
	 * Constructs the command line utility for producing randomly-generated parameters for use by the {@link Evaluator}.
	 */
	public SampleGenerator() {
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

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		ParameterFile parameterFile = new ParameterFile(new File(commandLine.getOptionValue("parameterFile")));

		int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));
		int D = parameterFile.size();
		
		if (N <= 0) {
			throw new IllegalArgumentException("numberOfSamples must be positive");
		}
		
		if (D <= 0) {
			throw new IllegalArgumentException("parameter file contains no parameters");
		}

		Sequence sequence = null;

		if (commandLine.hasOption("method")) {
			OptionCompleter completer = new OptionCompleter("uniform", "latin", "sobol", "saltelli");
			String method = completer.lookup(commandLine.getOptionValue("method"));
			
			if (method == null) {
				throw new IllegalArgumentException("invalid method: " + commandLine.getOptionValue("method"));
			}
			
			switch (method) {
				case "uniform" -> sequence = new Uniform();
				case "latin" -> sequence = new LatinHypercube();
				case "sobol" -> sequence = new Sobol();
				case "saltelli" -> {
					N *= (2 * D + 2);
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

		try (OutputLogger output = new OutputLogger(commandLine.getOptionValue("output"))) {
			double[][] samples = sequence.generate(N, D);

			for (int i = 0; i < N; i++) {
				for (int j = 0; j < D; j++) {
					if (j > 0) {
						output.print(' ');
					}
					
					output.print(parameterFile.get(j).scale(samples[i][j]));
				}

				output.println();
			}
		}
	}

	/**
	 * Command line utility for producing randomly-generated parameters for use by the {@link Evaluator}.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SampleGenerator().start(args);
	}

}
