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
package org.moeaframework.analysis.sensitivity;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.AdditiveEpsilonIndicator;
import org.moeaframework.core.indicator.Contribution;
import org.moeaframework.core.indicator.GenerationalDistance;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.indicator.InvertedGenerationalDistance;
import org.moeaframework.core.indicator.MaximumParetoFrontError;
import org.moeaframework.core.indicator.R1Indicator;
import org.moeaframework.core.indicator.R2Indicator;
import org.moeaframework.core.indicator.R3Indicator;
import org.moeaframework.core.indicator.Spacing;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.OptionCompleter;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for extracting data from a result file.  The data that
 * can be extracted includes any properties by providing its full name, or any
 * of the following metrics if given the designated {@code +option}.  The
 * available options include:
 * <ul>
 *   <li>{@code +hypervolume} for {@link Hypervolume}
 *   <li>{@code +generational} for {@link GenerationalDistance}
 *   <li>{@code +inverted} for {@link InvertedGenerationalDistance}
 *   <li>{@code +epsilon} for {@link AdditiveEpsilonIndicator}
 *   <li>{@code +error} for {@link MaximumParetoFrontError}
 *   <li>{@code +spacing} for {@link Spacing}
 *   <li>{@code +contribution} for {@link Contribution}
 *   <li>{@code +R1} for {@link R1Indicator}
 *   <li>{@code +R2} for {@link R2Indicator}
 *   <li>{@code +R3} for {@link R3Indicator}
 * </ul>
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.ExtractData <options> <fields>}
 * 
 * <table>
 *   <caption style="text-align: left">Arguments:</caption>
 *   <tr>
 *     <td>{@code -b, --problem}</td>
 *     <td>The name of the problem.  This name should reference one of the
 *         problems recognized by the MOEA Framework.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -d, --dimension}</td>
 *     <td>The number of objectives (use instead of -b).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -i, --input}</td>
 *     <td>The result file containing the input data.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the extract data will be saved.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -s, --separator}</td>
 *     <td>The character used to separate entries in the output file.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -e, --epsilon}</td>
 *     <td>The epsilon values for limiting the size of the results.  This
 *         epsilon value is also used for any algorithms that include an
 *         epsilon parameter.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -r, --reference}</td>
 *     <td>Location of the reference file used when computing the performance
 *         metrics (required if -m is set).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -n, --noheader}</td>
 *     <td>Do not include a header line indicating the data stored in each
 *         column.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code <fields>}</td>
 *     <td>The names of the fields to extract from the data, or one of the
 *         {@code +options} listed above.</td>
 *   </tr>
 * </table>
 */
public class ExtractData extends CommandLineUtility {

	/**
	 * Constructs the command line utility for extracting data from a result
	 * file.
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

		double[] epsilon = OptionUtils.getEpsilon(commandLine);
		String[] fields = commandLine.getArgs();

		// indicators are prepared, run the data extraction routine
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				ResultFileReader input = new ResultFileReader(problem, new File(commandLine.getOptionValue("input")));
				OutputLogger output = new OutputLogger(commandLine.getOptionValue("output"))) {
			NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine);
			Indicators indicators = getIndicators(problem, referenceSet, fields);

			if (epsilon != null) {
				indicators.withEpsilon(epsilon);
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
					} else if (fields[i].startsWith("+")) {
						output.print(getValue(values, fields[i].substring(1)));
					} else {
						throw new IllegalArgumentException("missing field '" + fields[i] + "'");
					}
				}

				output.println();
			}
		}
	}
	
	private Indicators getIndicators(Problem problem, NondominatedPopulation referenceSet, String[] fields) {
		Indicators indicators = Indicators.of(problem, referenceSet);
		
		OptionCompleter completer = new OptionCompleter("hypervolume", "generational", "inverted", "epsilon",
				"error", "spacing", "contribution", "R1", "R2", "R3");
		
		for (String field : fields) {
			if (field.startsWith("+")) {
				String option = completer.lookup(field.substring(1));
				
				if (option.equals("hypervolume")) {
					indicators.includeHypervolume();
				} else if (option.equals("generational")) {
					indicators.includeGenerationalDistance();
				} else if (option.equals("inverted")) {
					indicators.includeInvertedGenerationalDistance();
				} else if (option.equals("epsilon")) {
					indicators.includeAdditiveEpsilonIndicator();
				} else if (option.equals("error")) {
					indicators.includeMaximumParetoFrontError();
				} else if (option.equals("spacing")) {
					indicators.includeSpacing();
				} else if (option.equals("contribution")) {
					indicators.includeContribution();
				} else if (option.equals("R1")) {
					indicators.includeR1();
				} else if (option.equals("R2")) {
					indicators.includeR2();
				} else if (option.equals("R3")) {
					indicators.includeR3();
				} else {
					throw new IllegalArgumentException("Unrecognized argument '" + field + "'");
				}
			}
		}
		
		return indicators;
	}
	
	private double getValue(IndicatorValues values, String indicator) {
		OptionCompleter completer = new OptionCompleter("hypervolume", "generational", "inverted", "epsilon",
				"error", "spacing", "contribution", "R1", "R2", "R3");
		
		String option = completer.lookup(indicator);
		
		if (option.equals("hypervolume")) {
			return values.getHypervolume();
		} else if (option.equals("generational")) {
			return values.getGenerationalDistance();
		} else if (option.equals("inverted")) {
			return values.getInvertedGenerationalDistance();
		} else if (option.equals("epsilon")) {
			return values.getAdditiveEpsilonIndicator();
		} else if (option.equals("error")) {
			return values.getMaximumParetoFrontError();
		} else if (option.equals("spacing")) {
			return values.getSpacing();
		} else if (option.equals("contribution")) {
			return values.getContribution();
		} else if (option.equals("R1")) {
			return values.getR1();
		} else if (option.equals("R2")) {
			return values.getR2();
		} else if (option.equals("R3")) {
			return values.getR3();
		} else {
			throw new IllegalArgumentException("Unrecognized argument '" + indicator + "'");
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
