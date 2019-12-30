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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.sequence.LatinHypercube;
import org.moeaframework.util.sequence.Saltelli;
import org.moeaframework.util.sequence.Sequence;
import org.moeaframework.util.sequence.Sobol;
import org.moeaframework.util.sequence.Uniform;

/**
 * Command line utility for producing randomly-generated parameters for use by
 * the {@link Evaluator} or {@link DetailedEvaluator}.  The output is called a
 * parameter sample file.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.SampleGenerator}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
 *   <tr>
 *     <td>{@code -n, --numberOfSamples}</td>
 *     <td>The number of samples to generate.  Depending on the selected method,
 *         more samples may be generated then given by this option (required).
 *         </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -p, --parameterFile}</td>
 *     <td>Location of the parameter configuration file (required)</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -m, --method}</td>
 *     <td>The sampling method, such as {@code latin}, {@code sobol}, or
 *         {@code saltelli} (required).  If you eventually want to use the
 *         results with {@link SobolAnalysis}, then use the {@code saltelli}
 *         method.
 *     </td>
 *   <tr>
 *     <td>{@code -s, --seed}</td>
 *     <td>The seed used to generate the parameter samples.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the parameter samples are saved.</td>
 *   </tr>
 * </table>
 */
public class SampleGenerator extends CommandLineUtility {

	/**
	 * Constructs the command line utility for producing randomly-generated
	 * parameters for use by the {@link Evaluator}.
	 */
	public SampleGenerator() {
		super();
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(OptionBuilder
				.withLongOpt("numberOfSamples")
				.hasArg()
				.withArgName("value")
				.isRequired()
				.create('n'));
		options.addOption(OptionBuilder
				.withLongOpt("parameterFile")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('p'));
		options.addOption(OptionBuilder
				.withLongOpt("method")
				.hasArg()
				.withArgName("name")
				.isRequired()
				.create('m'));
		options.addOption(OptionBuilder
				.withLongOpt("seed")
				.hasArg()
				.withArgName("value")
				.create('s'));
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.create('o'));

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		ParameterFile parameterFile = new ParameterFile(new File(
				commandLine.getOptionValue("parameterFile")));

		int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));
		int D = parameterFile.size();
		
		if (N <= 0) {
			throw new IllegalArgumentException(
					"numberOfSamples must be positive");
		}
		
		if (D <= 0) {
			throw new IllegalArgumentException(
					"parameter file contains no parameters");
		}

		Sequence sequence = null;

		if (commandLine.hasOption("method")) {
			OptionCompleter completer = new OptionCompleter("uniform", "latin",
					"sobol", "saltelli");
			String method = completer.lookup(
					commandLine.getOptionValue("method"));

			if (method == null) {
				throw new IllegalArgumentException("invalid method: "
						+ commandLine.getOptionValue("method"));
			} else if (method.equals("latin")) {
				sequence = new LatinHypercube();
			} else if (method.equals("sobol")) {
				sequence = new Sobol();
			} else if (method.equals("saltelli")) {
				N *= (2 * D + 2);
				sequence = new Saltelli();
			} else if (method.equals("uniform")) {
				sequence = new Uniform();
			} else {
				throw new IllegalArgumentException("invalid method: "
						+ commandLine.getOptionValue("method"));
			}
		} else {
			sequence = new Sobol();
		}

		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}

		PrintStream output = System.out;

		try {
			if (commandLine.hasOption("output")) {
				output = new PrintStream(new BufferedOutputStream(
						new FileOutputStream(
								commandLine.getOptionValue("output"))));
			}

			double[][] samples = sequence.generate(N, D);

			for (int i = 0; i < N; i++) {
				output.print(parameterFile.get(0).getLowerBound()
						+ samples[i][0]
						* (parameterFile.get(0).getUpperBound() -
								parameterFile.get(0).getLowerBound()));

				for (int j = 1; j < D; j++) {
					output.print(' ');
					output.print(parameterFile.get(j).getLowerBound()
							+ samples[i][j]
							* (parameterFile.get(j).getUpperBound() -
									parameterFile.get(j).getLowerBound()));
				}

				output.println();
			}
		} finally {
			if ((output != null) && (output != System.out)) {
				output.close();
			}
		}
	}

	/**
	 * Command line utility for producing randomly-generated parameters for use
	 * by the {@link Evaluator}.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SampleGenerator().start(args);
	}

}
