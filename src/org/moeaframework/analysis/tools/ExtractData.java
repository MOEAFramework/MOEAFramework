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
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.validate.Validate;

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
		
		OptionUtils.addProblemOption(options);
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
				PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine);
			Indicators indicators = getIndicators(input.getProblem(), referenceSet, fields);

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
				IndicatorValues values = indicators.apply(new NondominatedPopulation(entry.getPopulation()));

				for (int i = 0; i < fields.length; i++) {
					if (i > 0) {
						output.print(separator);
					}

					if (properties.contains(fields[i])) {
						output.print(properties.getString(fields[i]));
						continue;
					}
					
					double value = getIndicatorValue(values, fields[i]);
					
					if (!Double.isNaN(value)) {
						output.print(value);
						continue;
					}
					
					Validate.that("field", fields[i]).fails("Field name not found in data");
				}

				output.println();
			}
		}
	}
	
	private Indicators getIndicators(Problem problem, NondominatedPopulation referenceSet, String[] fields) {
		Indicators indicators = Indicators.of(problem, referenceSet);
		
		for (String field : fields) {
			try {
				StandardIndicator indicator = TypedProperties.getEnumFromPartialString(StandardIndicator.class, field);
				indicators.include(indicator);
			} catch (IllegalArgumentException e) {
				// skip as no matching indicator found
			}
		}

		return indicators;
	}
	
	private double getIndicatorValue(IndicatorValues values, String name) {
		try {
			StandardIndicator indicator = TypedProperties.getEnumFromPartialString(StandardIndicator.class, name);
			return values.get(indicator);
		} catch (IllegalArgumentException e) {
			return Double.NaN;
		}
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
