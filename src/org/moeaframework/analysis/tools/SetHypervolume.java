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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.problem.ProblemStub;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.format.NumberFormatter;

/**
 * Command line utility for calculating the hypervolume of approximation sets.  A reference set can be given if all
 * approximation sets belong to the same problem.
 */
public class SetHypervolume extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for calculating the hypervolume of approximation sets.
	 */
	public SetHypervolume() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("r")
				.longOpt("reference")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		
		OptionUtils.addEpsilonOption(options);
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		NumberFormatter formatter = NumberFormatter.getDefault();
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		
		Hypervolume hypervolume = null;
		
		if (commandLine.hasOption("reference")) {
			NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet(
					new File(commandLine.getOptionValue("reference")));
			
			hypervolume = new Hypervolume(new ProblemStub(referenceSet.get(0).getNumberOfObjectives()), referenceSet);
		}
		
		try (OutputLogger output = new OutputLogger(commandLine.getOptionValue("output"))) {
			for (String filename : commandLine.getArgs()) {
				NondominatedPopulation set = NondominatedPopulation.loadReferenceSet(new File(filename));
				
				if (epsilons != null) {
					set = new EpsilonBoxDominanceArchive(epsilons, set);
				}
				
				output.print(filename);
				output.print(' ');
				
				if (hypervolume == null) {
					output.println(formatter.format(
							new Hypervolume(new ProblemStub(set.get(0).getNumberOfObjectives()), set).evaluate(set)));
				} else {
					output.println(formatter.format(hypervolume.evaluate(set)));
				}
			}
		}
	}
	
	/**
	 * Starts the command line utility for calculating the hypervolume of approximation sets.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SetHypervolume().start(args);
	}

}
