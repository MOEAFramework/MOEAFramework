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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for negating objective values in result files.  As the MOEA Framework only operates on
 * minimization objectives, maximization objectives must be negated prior to their use.  This utility can be used to
 * either apply or revert any negation.
 */
public class Negater extends CommandLineUtility {

	/**
	 * Constructs the command line utility for negating the objectives in result files.
	 */
	public Negater() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("d")
				.longOpt("direction")
				.hasArg()
				.argName("d1,d2,...")
				.required()
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		TypedProperties properties = TypedProperties.withProperty("direction", commandLine.getOptionValue("direction"));
		int[] directions = properties.getIntArray("direction");

		outer: for (String arg : commandLine.getArgs()) {
			File tempFile = File.createTempFile("temp", null);
			tempFile.deleteOnExit();

			try (BufferedReader reader = new BufferedReader(new FileReader(arg));
					PrintStream writer = new PrintStream(tempFile)) {
				String line = null;

				while ((line = reader.readLine()) != null) {
					if (line.startsWith("#") || line.startsWith("//")) {
						writer.println(line);
					} else {
						String[] tokens = line.split("\\s+");

						if (tokens.length != directions.length) {
							System.err.println("unable to negate values in " + arg + ", incorrect number of values in a row");
							continue outer;
						}

						for (int j = 0; j < tokens.length; j++) {
							if (j > 0) {
								writer.print(' ');
							}

							if (directions[j] == 0) {
								writer.print(tokens[j]);
							} else {
								double value = Double.parseDouble(tokens[j]);
								writer.print(-value);
							}
						}

						writer.println();
					}
				}
			} catch (NumberFormatException e) {
				System.err.println("unable to negate values in " + arg + ", unable to parse number");
				continue outer;
			}

			Files.move(tempFile.toPath(), Path.of(arg), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * Starts the command line utility for negating the objectives in result files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Negater().start(args);
	}

}
