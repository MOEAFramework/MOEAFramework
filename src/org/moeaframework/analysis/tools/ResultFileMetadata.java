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
import java.util.EnumSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.series.IndexType;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.analysis.series.ResultSeries;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TableFormat;
import org.moeaframework.util.format.TabularData;

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

		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				ResultFileReader input = ResultFileReader.open(problem, new File(commandLine.getOptionValue("input")))) {
			ResultSeries series = ResultSeries.of(input);
			
			// compute any indicators and merge into the properties
			EnumSet<StandardIndicator> selectedIndicators = getSelectedIndicators(fields);
			
			if (!selectedIndicators.isEmpty()) {
				Indicators indicators = Indicators.of(problem, OptionUtils.getReferenceSet(commandLine, false));
				indicators.include(selectedIndicators);

				if (epsilons != null) {
					indicators.withEpsilons(epsilons);
				}
				
				series.forEach(x -> {
					IndicatorValues values = indicators.apply(new NondominatedPopulation(x.getPopulation()));
					x.getProperties().addAll(values.asProperties());
				});
			}
			
			// create a table with the selected fields
			TabularData<IndexedResult> table = new TabularData<>(series);
			
			if (!series.getIndexType().equals(IndexType.Singleton)) {
				table.addColumn(new Column<>(series.getIndexType().name(), IndexedResult::getIndex));
			}
			
			for (String field : fields) {
				if (series.getIndexType().equals(IndexType.NFE) && field.equals(ResultEntry.NFE)) {
					continue;
				}
				
				table.addColumn(new Column<>(field, x -> x.getProperties().getString(field)));
			}
			
			// output the table
			try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
				table.save(format, output);
			}
		}
	}
	
	private EnumSet<StandardIndicator> getSelectedIndicators(String[] fields) {
		EnumSet<StandardIndicator> selection = EnumSet.noneOf(StandardIndicator.class);
		
		for (String field : fields) {
			try {
				selection.add(TypedProperties.getEnumFromString(StandardIndicator.class, field));
			} catch (IllegalArgumentException e) {
				// skip as no matching indicator found
			}
		}
		
		return selection;
	}
	
	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileMetadata().start(args);
	}

}
