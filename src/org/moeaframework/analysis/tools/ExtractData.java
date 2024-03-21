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
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for extracting data from a result file.  The data that can be extracted includes any properties
 * by providing its full name, or any of the following metrics if given the name of the indicator, such as
 * {@code Hypervolume} or {@code GenerationalDistance}.
 */
public class ExtractData extends CommandLineUtility {

	/**
	 * Constructs the command line utility for extracting data from a result file.
	 */
	public ExtractData() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options, true);
		OptionUtils.addReferenceSetOption(options);
		OptionUtils.addEpsilonOption(options);
		
		options.addOption(Option.builder("i")
				.longOpt("input")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("s")
				.longOpt("separator")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("n")
				.longOpt("noheader")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String separator = commandLine.hasOption("separator") ? commandLine.getOptionValue("separator") : " ";

		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		String[] fields = commandLine.getArgs();

		// indicators are prepared, run the data extraction routine
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				ResultFileReader input = new ResultFileReader(problem, new File(commandLine.getOptionValue("input")));
				OutputLogger output = new OutputLogger(commandLine.getOptionValue("output"))) {
			NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine);
			Indicators indicators = getIndicators(problem, referenceSet, fields);

			if (epsilons != null) {
				indicators.withEpsilons(epsilons);
			}
			
			// optionally print header line
			if (!commandLine.hasOption("noheader")) {
				output.print('#');
						
				for (int i = 0; i < fields.length; i++) {
					if (i > 0) {
						output.print(separator);
					}

					output.print(fields[i]);
				}

				output.println();
			}

			// process entries
			while (input.hasNext()) {
				ResultEntry entry = input.next();
				TypedProperties properties = entry.getProperties();
				IndicatorValues values = indicators.apply(entry.getPopulation());

				for (int i = 0; i < fields.length; i++) {
					if (i > 0) {
						output.print(separator);
					}

					if (properties.contains(fields[i])) {
						output.print(properties.getString(fields[i]));
						continue;
					}
					
					double value = getValue(values, fields[i]);
					
					if (!Double.isNaN(value)) {
						output.print(value);
						continue;
					}
					
					throw new IllegalArgumentException("missing field '" + fields[i] + "'");
				}

				output.println();
			}
		}
	}
	
	private Indicators getIndicators(Problem problem, NondominatedPopulation referenceSet, String[] fields) {
		Indicators indicators = Indicators.of(problem, referenceSet);
		
		OptionCompleter completer = new OptionCompleter("Hypervolume", "GenerationalDistance",
				"InvertedGenerationalDistance", "AdditiveEpsilonIndicator", "MaximumParetoFrontError", "Spacing",
				"Contribution", "R1Indicator", "R2Indicator", "R3Indicator");
		
		for (String field : fields) {
			String option = completer.lookup(field);
			
			if (option == null) {
				continue;
			}
			
			switch (option) {
				case "Hypervolume" -> indicators.includeHypervolume();
				case "GenerationalDistance" -> indicators.includeGenerationalDistance();
				case "InvertedGenerationalDistance" -> indicators.includeInvertedGenerationalDistance();
				case "AdditiveEpsilonIndicator" -> indicators.includeAdditiveEpsilonIndicator();
				case "MaximumParetoFrontError" -> indicators.includeMaximumParetoFrontError();
				case "Spacing" -> indicators.includeSpacing();
				case "Contribution" -> indicators.includeContribution();
				case "R1Indicator" -> indicators.includeR1();
				case "R2Indicator" -> indicators.includeR2();
				case "R3Indicator" -> indicators.includeR3();
				default -> throw new IllegalStateException();
			}
		}

		return indicators;
	}
	
	private double getValue(IndicatorValues values, String indicator) {
		OptionCompleter completer = new OptionCompleter("Hypervolume", "GenerationalDistance",
				"InvertedGenerationalDistance", "AdditiveEpsilonIndicator", "MaximumParetoFrontError", "Spacing",
				"Contribution", "R1Indicator", "R2Indicator", "R3Indicator");
		
		String option = completer.lookup(indicator);
		
		if (option == null) {
			return Double.NaN;
		}
		
		return switch (option) {
			case "Hypervolume" -> values.getHypervolume();
			case "GenerationalDistance" -> values.getGenerationalDistance();
			case "InvertedGenerationalDistance" -> values.getInvertedGenerationalDistance();
			case "AdditiveEpsilonIndicator" -> values.getAdditiveEpsilonIndicator();
			case "MaximumParetoFrontError" -> values.getMaximumParetoFrontError();
			case "Spacing" -> values.getSpacing();
			case "Contribution" -> values.getContribution();
			case "R1Indicator" -> values.getR1();
			case "R2Indicator" -> values.getR2();
			case "R3Indicator" -> values.getR3();
			default -> throw new IllegalStateException();
		};
	}
	
	/**
	 * Starts the command line utility for extracting data from a result file.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ExtractData().start(args);
	}

}
