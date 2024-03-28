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
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.indicator.Contribution;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.format.NumberFormatter;

/**
 * Command line utility for reporting the number of solutions in a set that are contained within a reference set.  The
 * common use-case of this utility is to determine the percent makeup of the individual approximation sets used during
 * the reference set construction.
 */
public class SetContribution extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for reporting the number of solutions in a set that are contained within a
	 * reference set.
	 */
	public SetContribution() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("r")
				.longOpt("reference")
				.hasArg()
				.argName("file")
				.required()
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
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet(
				new File(commandLine.getOptionValue("reference")));
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		Contribution contribution = null;

		if (epsilons != null) {
			contribution = new Contribution(referenceSet, epsilons);
		} else {
			contribution = new Contribution(referenceSet);
		}

		try (OutputLogger output = new OutputLogger(commandLine.getOptionValue("output"))) {
			for (String filename : commandLine.getArgs()) {
				NondominatedPopulation approximationSet = NondominatedPopulation.loadReferenceSet(new File(filename));
	
				System.out.print(filename);
				System.out.print(' ');
				System.out.println(formatter.format(contribution.evaluate(approximationSet)));
			}
		}
	}
	
	/**
	 * Starts the command line utility for reporting the number of solutions in a set that are contained within a
	 * reference set.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SetContribution().start(args);
	}

}
