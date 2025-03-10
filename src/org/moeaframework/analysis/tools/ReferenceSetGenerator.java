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
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for generating reference sets for a given problem.  The reference set is created by randomly
 * sampling points and building a non-dominated set.  Only problems that implement the {@link AnalyticalProblem}
 * interface can be used.
 */
public class ReferenceSetGenerator extends CommandLineUtility {

	private ReferenceSetGenerator() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
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
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException, MissingOptionException {
		NondominatedPopulation set = OptionUtils.getArchive(commandLine);
		
		int numberOfPoints = Integer.parseInt(commandLine.getOptionValue("numberOfPoints"));
		
		// seed the pseudo-random number generator
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}
		
		//generate the points
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, false)) {
			if (problem instanceof AnalyticalProblem analyticalProblem) {
				for (int i = 0; i < numberOfPoints; i++) {
					set.add(analyticalProblem.generate());
				}
			} else {
				fail("ERROR: problem must implement " + AnalyticalProblem.class.getSimpleName());
			}
		}
		
		//output set
		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			set.save(output);
		}
	}
	
	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ReferenceSetGenerator().start(args);
	}
	
}
