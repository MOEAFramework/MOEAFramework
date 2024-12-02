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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TableFormat;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.validate.Validate;

/**
 * Command line utility for extracting metadata from a result file.  The metadata that can be extracted includes any
 * properties by providing its full name, or any of the following metrics if given the name of the indicator, such as
 * {@code Hypervolume} or {@code GenerationalDistance}.
 */
public class ResultFileMetadata extends CommandLineUtility {

	private ResultFileMetadata() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
		OptionUtils.addReferenceSetOption(options);
		OptionUtils.addEpsilonOption(options);
		OptionUtils.addFormatOption(options);
		
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
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);
		TableFormat format = OptionUtils.getFormat(commandLine);
		String[] fields = commandLine.getArgs();

		// indicators are prepared, run the data extraction routine
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				ResultFileReader input = ResultFileReader.open(problem, new File(commandLine.getOptionValue("input")))) {
			NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine, false);
			Indicators indicators = getIndicators(input.getProblem(), referenceSet, fields);

			if (epsilons != null) {
				indicators.withEpsilons(epsilons);
			}
			
			// collect the metadata
			List<Object[]> metadata = new ArrayList<>();
			
			while (input.hasNext()) {
				ResultEntry entry = input.next();
				TypedProperties properties = entry.getProperties();
				IndicatorValues values = indicators.apply(new NondominatedPopulation(entry.getPopulation()));
				
				Object[] row = new Object[fields.length];

				for (int i = 0; i < fields.length; i++) {
					if (properties.contains(fields[i])) {
						row[i] = properties.getString(fields[i]);
						continue;
					}
					
					double value = getIndicatorValue(values, fields[i]);
					
					if (!Double.isNaN(value)) {
						row[i] = value;
						continue;
					}
					
					Validate.that("field", fields[i]).fails("Field name not found in data");
				}

				metadata.add(row);
			}
			
			// format the output
			TabularData<Object[]> table = new TabularData<>(metadata);
			
			for (int i = 0; i < fields.length; i++) {
				final int fieldIndex = i;
				table.addColumn(new Column<Object[], Object>(fields[i], x -> x[fieldIndex]));
			}
			
			try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
				table.save(format, output);
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
	 * Starts the command line utility for extracting metadata from a result file.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileMetadata().start(args);
	}

}
