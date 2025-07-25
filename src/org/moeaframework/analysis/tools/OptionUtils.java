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

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.format.TableFormat;

/**
 * Create and parse command line options shared by multiple tools.
 */
class OptionUtils {
	
	private OptionUtils() {
		super();
	}
	
	/**
	 * Adds an option to specify the problem.
	 * 
	 * @param options the current set of options
	 */
	public static void addProblemOption(Options options) {
		options.addOption(Option.builder("b")
				.longOpt("problem")
				.hasArg()
				.argName("name")
				.build());
	}
	
	/**
	 * Adds an option for setting the reference set.
	 * 
	 * @param options the current set of options
	 */
	public static void addReferenceSetOption(Options options) {
		options.addOption(Option.builder("r")
				.longOpt("reference")
				.hasArg()
				.argName("file")
				.build());
	}
	
	/**
	 * Adds an option for setting the epsilon value used in epsilon-dominated archives.
	 * 
	 * @param options the current set of options
	 */
	public static void addEpsilonOption(Options options) {
		options.addOption(Option.builder("e")
				.longOpt("epsilon")
				.hasArgs()
				.argName("e1,e2,...")
				.valueSeparator(',')
				.build());
	}
	
	/**
	 * Adds an option for setting key-value properties.
	 * 
	 * @param options the current set of options
	 */
	public static void addPropertiesOption(Options options) {
		options.addOption(Option.builder("X")
				.longOpt("properties")
				.hasArgs()
				.argName("p1=v1;p2=v2;...")
				.valueSeparator(';')
				.build());
	}
	
	/**
	 * Adds an option for setting the output table format.
	 * 
	 * @param options the current set of options
	 */
	public static void addFormatOption(Options options) {
		options.addOption(Option.builder("f")
				.longOpt("format")
				.hasArg()
				.argName("fmt")
				.build());
	}
	
	/**
	 * Creates the problem instance specified on the command line.
	 * 
	 * @param commandLine the command line inputs
	 * @param allowMissing if {@code true}, returns {@code null} if the option is missing
	 * @return the problem instance
	 * @throws MissingOptionException if the option is missing and {@code allowMissing} is false
	 */
	public static Problem getProblemInstance(CommandLine commandLine, boolean allowMissing)
			throws MissingOptionException {
		if (commandLine.hasOption("problem")) {
			return ProblemFactory.getInstance().getProblem(commandLine.getOptionValue("problem"));
		}
		
		if (allowMissing) {
			return null;
		}
		
		throw new MissingOptionException("No problem specified");
	}
	
	/**
	 * Loads the reference set based on the command line inputs.  This will either read from a file or load the
	 * predefined reference set.
	 * 
	 * @param commandLine the command line inputs
	 * @param allowMissing if {@code true}, returns {@code null} if the option is missing
	 * @return the loaded reference set
	 * @throws IOException if an I/O error occurred
	 * @throws MissingOptionException if the option is missing and {@code allowMissing} is false
	 */
	public static NondominatedPopulation getReferenceSet(CommandLine commandLine, boolean allowMissing)
			throws IOException, MissingOptionException {
		NondominatedPopulation referenceSet = null;
		
		if (commandLine.hasOption("reference")) {
			referenceSet = NondominatedPopulation.load(commandLine.getOptionValue("reference"));
		} else if (commandLine.hasOption("problem")) {
			referenceSet = ProblemFactory.getInstance().getReferenceSet(commandLine.getOptionValue("problem"));
		}
		
		if (!allowMissing && referenceSet == null) {
			throw new MissingOptionException("No reference set available");
		}
		
		return referenceSet;
	}
	
	/**
	 * Returns the epsilon values specified on the command line, if any.
	 * 
	 * @param commandLine the command line input
	 * @return the epsilon values or {@code null} if unspecified
	 */
	public static Epsilons getEpsilons(CommandLine commandLine) {
		if (commandLine.hasOption("epsilon")) {
			TypedProperties properties = TypedProperties.of("epsilon", commandLine.getOptionValue("epsilon"));
			return new Epsilons(properties.getDoubleArray("epsilon"));
		}
		
		return null;
	}
	
	/**
	 * Returns an empty archive using the epsilon values specified on the command line.
	 * 
	 * @param commandLine the command line inputs
	 * @return the empty archive
	 */
	public static NondominatedPopulation getArchive(CommandLine commandLine) {
		Epsilons epsilons = getEpsilons(commandLine);
		
		if (epsilons != null) {
			return new EpsilonBoxDominanceArchive(epsilons);
		} else {
			return new NondominatedPopulation();
		}
	}
	
	/**
	 * Returns the extra properties specified on the command line.
	 * 
	 * @param commandLine the command line inputs
	 * @return the properties
	 * @throws ParseException if the properties are not formatted correctly
	 */
	public static TypedProperties getProperties(CommandLine commandLine) throws ParseException {
		TypedProperties properties = new TypedProperties();

		if (commandLine.hasOption("properties")) {
			for (String property : commandLine.getOptionValues("properties")) {
				String[] tokens = property.split("=");
					
				if (tokens.length == 2) {
					properties.setString(tokens[0], tokens[1]);
				} else {
					throw new ParseException("Malformed property argument '" + property +
							"', expected 'key=value' format");
				}
			}
		}
		
		return properties;
	}
	
	/**
	 * Returns the table format option specified on the command line.
	 * 
	 * @param commandLine the command line inputs
	 * @return the table format
	 */
	public static TableFormat getFormat(CommandLine commandLine) {
		OptionCompleter completer = new OptionCompleter(TableFormat.class);
		return TableFormat.valueOf(completer.getOrThrow("format", 
				commandLine.hasOption("format") ? commandLine.getOptionValue("format") : TableFormat.Plaintext.name()));
	}

}
