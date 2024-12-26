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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.MatrixIO;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;
import org.moeaframework.util.io.Tokenizer;
import org.moeaframework.util.validate.Validate;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;
import org.moeaframework.util.weights.RandomGenerator;
import org.moeaframework.util.weights.UniformDesignGenerator;

/**
 * Outputs weights produced by a weight generator.
 */
public class WeightGenerator extends CommandLineUtility {

	private WeightGenerator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("m")
				.longOpt("method")
				.hasArg()
				.argName("name")
				.required()
				.build());
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
		options.addOption(Option.builder("g")
				.longOpt("generalized")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException, ParseException, InterruptedException {
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}

		OptionCompleter options = new OptionCompleter("random", "uniformdesign", "normalboundaryintersection");
		String method = options.lookup(commandLine.getOptionValue("method"));

		if (method == null) {
			Validate.that("method", commandLine.getOptionValue("method")).failUnsupportedOption(options.getOptions());
		}

		int D = Integer.parseInt(commandLine.getOptionValue("dimension"));
		Validate.that("dimension", D).isGreaterThan(0);

		List<double[]> weights = switch (method) {
			case "random" -> {
				if (!commandLine.hasOption("numberOfSamples")) {
					throw new MissingOptionException("Missing --numberOfSamples");
				}
	
				int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));			
				Validate.that("numberOfSamples", N).isGreaterThan(0);
	
				yield new RandomGenerator(D, N).generate();
			}
			case "uniformdesign" -> {
				if (!commandLine.hasOption("numberOfSamples")) {
					throw new MissingOptionException("Missing --numberOfSamples");
				}
	
				int N = Integer.parseInt(commandLine.getOptionValue("numberOfSamples"));			
				Validate.that("numberOfSamples", N).isGreaterThan(0);
	
				yield new UniformDesignGenerator(D, N).generate();
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
	
				yield new NormalBoundaryIntersectionGenerator(D, divisions).generate();
			}
			default -> throw new IllegalStateException();
		};
		
		if (commandLine.hasOption("generalized")) {
			weights = toGeneralizedDecomposition(weights);
		}

		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			MatrixIO.save(output, weights);
		}
	}
	
	private List<double[]> toGeneralizedDecomposition(List<double[]> weights) throws IOException, InterruptedException {
		String scriptName = "gd.py";
		File file = Resources.asFile(WeightGenerator.class, scriptName, ResourceOption.REQUIRED,
				ResourceOption.TEMPORARY);
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(Settings.getPythonCommand(), file.getAbsolutePath());
		processBuilder.redirectError(Redirect.INHERIT);
		
		Process process = processBuilder.start();
		Tokenizer tokenizer = new Tokenizer();
		List<double[]> modifiedWeights = new ArrayList<>();
		
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(process.getOutputStream()));
				LineReader reader = LineReader.wrap(new InputStreamReader(process.getInputStream()))) {
			for (double[] weight : weights) {
				String input = tokenizer.encode(DoubleStream.of(weight).mapToObj(Double::toString));
				writer.println(input);
				writer.flush();
				
				String output = reader.readLine();
				
				if (output == null) {
					// unexpected end of output - exit loop so we can fail on process termination
					break;
				}
				
				modifiedWeights.add(tokenizer.decode(output).stream().mapToDouble(Double::parseDouble).toArray());
			}
		}
		
		if (!process.waitFor(30, TimeUnit.SECONDS)) {
			throw new IOException(scriptName + " failed to terminate after processing all inputs");
		}
		
		if (process.exitValue() != 0) {
			throw new IOException(scriptName + " failed with exit code " + process.exitValue());
		}
		
		if (modifiedWeights.size() != weights.size()) {
			throw new FrameworkException("unexpected number of weights after generalized decomposition (expected: " +
					weights.size() + ", actual: " + modifiedWeights.size() + ")");
		}
		
		return modifiedWeights;
	}

	/**
	 * Command line utility for producing randomly-generated weights.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new WeightGenerator().start(args);
	}

}
