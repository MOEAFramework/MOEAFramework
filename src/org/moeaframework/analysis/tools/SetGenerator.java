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
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for generating reference sets for a given problem.  The reference set is created by randomly
 * sampling points and building a non-dominated set.  Only problems that implement the {@link AnalyticalProblem}
 * interface can be used.
 */
public class SetGenerator extends CommandLineUtility {

	/**
	 * Constructs the command line utility for generating reference sets for a given problem.
	 */
	public SetGenerator() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options, false);
		OptionUtils.addEpsilonOption(options);
		
		options.addOption(Option.builder("n")
				.longOpt("numberOfPoints")
				.hasArg()
				.argName("value")
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
				.required()
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		NondominatedPopulation set = OptionUtils.getArchive(commandLine);
		
		int numberOfPoints = Integer.parseInt(commandLine.getOptionValue("numberOfPoints"));
		
		// seed the pseudo-random number generator
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}
		
		//generate the points
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, false)) {
			if (problem instanceof AnalyticalProblem analyticalProblem) {
				for (int i=0; i<numberOfPoints; i++) {
					set.add(analyticalProblem.generate());
				}
			} else {
				throw new FrameworkException("problem does not have an analytical solution");
			}
		}
		
		//output set
		set.saveObjectives(new File(commandLine.getOptionValue("output")));
	}
	
	/**
	 * Starts the command line utility for generating reference sets for a given problem.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SetGenerator().start(args);
	}
	
}
