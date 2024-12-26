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
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.format.NumberFormatter;
import org.moeaframework.util.validate.Validate;

/**
 * Command line utility for calculating an indicator on approximation sets.
 */
public class CalculateIndicator extends CommandLineUtility {
	
	private CalculateIndicator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(Option.builder("i")
				.longOpt("indicator")
				.hasArg()
				.argName("name")
				.required()
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		
		OptionUtils.addProblemOption(options);
		OptionUtils.addReferenceSetOption(options);
		OptionUtils.addEpsilonOption(options);
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		NumberFormatter formatter = NumberFormatter.getInstance();
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine, false);
		
		OptionCompleter completer = new OptionCompleter(StandardIndicator.class);
		String indicatorName = completer.lookup(commandLine.getOptionValue("indicator"));
		
		if (indicatorName == null) {
			Validate.that("indicator", commandLine.getOptionValue("indicator")).failUnsupportedOption(completer.getOptions());
		}
		
		StandardIndicator indicator = StandardIndicator.valueOf(indicatorName);

		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			for (String filename : commandLine.getArgs()) {
				try (ResultFileReader reader = ResultFileReader.openLegacy(problem, new File(filename))) {
					NondominatedPopulation approximationSet = new NondominatedPopulation(
							Iterators.last(reader.iterator()).getPopulation());
					
					if (epsilons != null) {
						approximationSet = new EpsilonBoxDominanceArchive(epsilons, approximationSet);
					}

					Indicators indicators = Indicators.of(reader.getProblem(), referenceSet);
					
					indicators.include(indicator);
					indicators.withEpsilons(epsilons);
					
					IndicatorValues indicatorValues = indicators.apply(approximationSet);
					
					output.print(filename);
					output.print(' ');
					output.println(formatter.format(indicatorValues.get(indicator)));
				}
			}
		}
	}
	
	/**
	 * Starts the command line utility for calculating an indicator on approximation sets.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new CalculateIndicator().start(args);
	}

}
