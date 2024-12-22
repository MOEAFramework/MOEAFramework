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

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.io.MatrixIO;
import org.moeaframework.util.sequence.LatinHypercube;
import org.moeaframework.util.sequence.Saltelli;
import org.moeaframework.util.sequence.Sequence;
import org.moeaframework.util.sequence.Sobol;
import org.moeaframework.util.sequence.Uniform;
import org.moeaframework.util.validate.Validate;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;
import org.moeaframework.util.weights.RandomGenerator;
import org.moeaframework.util.weights.UniformDesignGenerator;
import org.moeaframework.util.weights.WeightGenerator;

//TODO: Add tests for new CLI tool
//TODO: Add to docs and localization

/**
 * Outputs the raw values from a {@link Sequence} or {@link WeightGenerator}.
 */
public class SequenceGenerator extends CommandLineUtility {

	private SequenceGenerator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionGroup methodGroup = new OptionGroup();
		methodGroup.addOption(Option.builder()
				.longOpt("sequence")
				.hasArg()
				.argName("name")
				.required()
				.build());
		methodGroup.addOption(Option.builder()
				.longOpt("weights")
				.hasArg()
				.argName("name")
				.required()
				.build());
		options.addOptionGroup(methodGroup);

		options.addOption(Option.builder("n")
				.longOpt("numberOfSamples")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("d")
				.longOpt("dimension")
				.hasArg()
				.argName("value")
				.required()
				.build());
		options.addOption(Option.builder()
				.longOpt("divisions")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder()
				.longOpt("divisionsInner")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder()
				.longOpt("divisionsOuter")
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
	public void run(CommandLine commandLine) throws IOException, ParseException {
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}

		if (commandLine.hasOption("sequence")) {
			OptionCompleter options = new OptionCompleter("uniform", "latin", "sobol", "saltelli");
			String method = options.lookup(commandLine.getOptionValue("sequence"));
			
			if (method == null) {
				Validate.that("sequence", commandLine.getOptionValue("sequence")).failUnsupportedOption(options.getOptions());
			}
			
			if (!commandLine.hasOption("numberOfSamples")) {
				throw new MissingOptionException("Missing --numberOfSamples");
			}
			
			int D = Integer.parseInt(commandLine.getOptionValue("dimension"));
			Validate.that("dimension", D).isGreaterThan(0);

			int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));
			Validate.that("numberOfSamples", N).isGreaterThan(0);
			
			Sequence sequence = switch (method) {
				case "uniform" -> new Uniform();
				case "latin" -> new LatinHypercube();
				case "sobol" -> new Sobol();
				case "saltelli" -> {
					N *= (2 * D + 2);
					yield new Saltelli();
				}
				default -> throw new IllegalStateException();
			};
			
			try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
				MatrixIO.save(output, sequence.generate(N, D));
			}
		} else if (commandLine.hasOption("weights")) {
			OptionCompleter options = new OptionCompleter("random", "uniformdesign", "normalboundaryintersection");
			String method = options.lookup(commandLine.getOptionValue("weights"));
			
			if (method == null) {
				Validate.that("weights", commandLine.getOptionValue("weights")).failUnsupportedOption(options.getOptions());
			}
			
			int D = Integer.parseInt(commandLine.getOptionValue("dimension"));
			Validate.that("dimension", D).isGreaterThan(0);
			
			WeightGenerator generator = switch (method) {
				case "random" -> {
					if (!commandLine.hasOption("numberOfSamples")) {
						throw new MissingOptionException("Missing --numberOfSamples");
					}
					
					int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));			
					Validate.that("numberOfSamples", N).isGreaterThan(0);
					
					yield new RandomGenerator(D, N);
				}
				case "uniformdesign" -> {
					if (!commandLine.hasOption("numberOfSamples")) {
						throw new MissingOptionException("Missing --numberOfSamples");
					}
					
					int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));			
					Validate.that("numberOfSamples", N).isGreaterThan(0);
					
					yield new UniformDesignGenerator(D, N);
				}
				case "normalboundaryintersection" -> {
					TypedProperties properties = new TypedProperties();
					
					if (commandLine.hasOption("divisions")) {
						properties.setString("divisions", commandLine.getOptionValue("divisions"));
					}
					
					if (commandLine.hasOption("divisionsInner")) {
						properties.setString("divisionsInner", commandLine.getOptionValue("divisionsInner"));
					}
					
					if (commandLine.hasOption("divisionsOuter")) {
						properties.setString("divisionsOuter", commandLine.getOptionValue("divisionsOuter"));
					}
					
					NormalBoundaryDivisions divisions = NormalBoundaryDivisions.tryFromProperties(properties);
					
					if (divisions == null) {
						if (commandLine.hasOption("numberOfSamples")) {
							int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));			
							Validate.that("numberOfSamples", N).isGreaterThan(0);
							
							System.err.println("Using `--numberOfSamples " + N + "` as the number of divisions.");
							divisions = new NormalBoundaryDivisions(N);
						} else {
							throw new MissingOptionException("Missing --divisions");
						}
					}
					
					yield new NormalBoundaryIntersectionGenerator(D, divisions);
				}
				default -> throw new IllegalStateException();
			};
			
			try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
				MatrixIO.save(output, generator.generate());
			}
		} else {
			Validate.fail("Must specify --sequence or --weights");
		}
	}

	/**
	 * Command line utility for producing randomly-generated sequences or weights.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SequenceGenerator().start(args);
	}

}
