/* Copyright 2009-2022 David Hadka
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Create and parse command line options shared by multiple tools.
 */
public class OptionUtils {
	
	private OptionUtils() {
		super();
	}
	
	/**
	 * Adds an option group for specifying problems, either explicitly by name or
	 * by the dimensionality (using a problem stub).
	 * 
	 * @param options the current set of options
	 * @param allowStub if {@code true}, use a problem stub to handle data files
	 *        just using the problem dimensionality
	 */
	public static void addProblemOption(Options options, boolean allowStub) {
		if (allowStub) {
			OptionGroup group = new OptionGroup();
			group.setRequired(true);
			group.addOption(Option.builder("b")
					.longOpt("problem")
					.hasArg()
					.argName("name")
					.build());
			group.addOption(Option.builder("d")
					.longOpt("dimension")
					.hasArg()
					.argName("number")
					.build());
			options.addOptionGroup(group);
		} else {
			options.addOption(Option.builder("b")
					.longOpt("problem")
					.hasArg()
					.argName("name")
					.build());
		}
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
	 * Adds an option for setting the epsilon value used in epsilon-dominated
	 * archives.
	 * 
	 * @param options the current set of options
	 */
	public static void addEpsilonOption(Options options) {
		options.addOption(Option.builder("e")
				.longOpt("epsilon")
				.hasArg()
				.argName("e1,e2,...")
				.build());
	}
	
	/**
	 * Creates the problem instance specified on the command line.
	 * 
	 * @param commandLine the command line inputs
	 * @param allowStub if {@code true}, use a problem stub to handle data files
	 *        just using the problem dimensionality
	 * @return the problem instance
	 */
	public static Problem getProblemInstance(CommandLine commandLine, boolean allowStub) {
		if (commandLine.hasOption("problem")) {
			return ProblemFactory.getInstance().getProblem(commandLine.getOptionValue("problem"));
		}
		
		if (allowStub && commandLine.hasOption("dimension")) {
			return new ProblemStub(Integer.parseInt(commandLine.getOptionValue("dimension")));
		}
		
		throw new FrameworkException("no problem specified");
	}
	
	/**
	 * Loads the reference set based on the command line inputs.  This will either read from a file or
	 * load the predefined reference set.
	 * 
	 * @param commandLine the command line inputs
	 * @return the loaded reference set
	 * @throws IOException if an I/O error occurred
	 */
	public static NondominatedPopulation getReferenceSet(CommandLine commandLine) throws IOException {
		NondominatedPopulation referenceSet = null;
		
		if (commandLine.hasOption("reference")) {
			referenceSet = new NondominatedPopulation(PopulationIO.readObjectives(
					new File(commandLine.getOptionValue("reference"))));
		} else {
			referenceSet = ProblemFactory.getInstance().getReferenceSet(
					commandLine.getOptionValue("problem"));
		}

		if (referenceSet == null) {
			throw new FrameworkException("no reference set available");
		}
		
		return referenceSet;
	}
	
	/**
	 * Returns the array of epsilon values specified on the command line, if any.
	 * 
	 * @param commandLine the command line input
	 * @return the epsilon values or {@code null} if unspecified
	 */
	public static double[] getEpsilon(CommandLine commandLine) {
		if (commandLine.hasOption("epsilon")) {
			TypedProperties properties = TypedProperties.withProperty("epsilon",
					commandLine.getOptionValue("epsilon"));
			
			return properties.getDoubleArray("epsilon");
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
		double[] epsilon = getEpsilon(commandLine);
		
		if (epsilon != null) {
			return new EpsilonBoxDominanceArchive(epsilon);
		} else {
			return new NondominatedPopulation();
		}
	}

}
