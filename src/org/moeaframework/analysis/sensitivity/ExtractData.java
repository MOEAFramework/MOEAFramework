/* Copyright 2009-2018 David Hadka
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
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.AdditiveEpsilonIndicator;
import org.moeaframework.core.indicator.Contribution;
import org.moeaframework.core.indicator.GenerationalDistance;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.indicator.InvertedGenerationalDistance;
import org.moeaframework.core.indicator.MaximumParetoFrontError;
import org.moeaframework.core.indicator.R1Indicator;
import org.moeaframework.core.indicator.R2Indicator;
import org.moeaframework.core.indicator.R3Indicator;
import org.moeaframework.core.indicator.Spacing;
import org.moeaframework.core.spi.ProblemFactory;
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
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
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
	 * The problem.
	 */
	private Problem problem;
	
	/**
	 * The reference set; {@code null} if the reference set has not yet been
	 * loaded.
	 */
	private NondominatedPopulation referenceSet;

	/**
	 * Constructs the command line utility for extracting data from a result
	 * file.
	 */
	public ExtractData() {
		super();
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionGroup group = new OptionGroup();
		group.setRequired(true);
		group.addOption(OptionBuilder
				.withLongOpt("problem")
				.hasArg()
				.withArgName("name")
				.create('b'));
		group.addOption(OptionBuilder
				.withLongOpt("dimension")
				.hasArg()
				.withArgName("number")
				.create('d'));
		options.addOptionGroup(group);
		
		options.addOption(OptionBuilder
				.withLongOpt("input")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('i'));
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("separator")
				.hasArg()
				.withArgName("value")
				.create('s'));
		options.addOption(OptionBuilder
				.withLongOpt("reference")
				.hasArg()
				.withArgName("file")
				.create('r'));
		options.addOption(OptionBuilder
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.create('e'));
		options.addOption(OptionBuilder
				.withLongOpt("noheader")
				.create('n'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String separator = commandLine.hasOption("separator") ? commandLine
				.getOptionValue("separator") : " ";

		String[] fields = commandLine.getArgs();

		// indicators are prepared, run the data extraction routine
		ResultFileReader input = null;
		PrintStream output = null;

		try {
			// setup the problem
			if (commandLine.hasOption("problem")) {
				problem = ProblemFactory.getInstance().getProblem(commandLine
						.getOptionValue("problem"));
			} else {
				problem = new ProblemStub(Integer.parseInt(commandLine
						.getOptionValue("dimension")));
			}
			
			try {
				input = new ResultFileReader(problem, new File(commandLine
						.getOptionValue("input")));

				try {
					output = commandLine.hasOption("output") ? new PrintStream(
							new File(commandLine.getOptionValue("output"))) :
								System.out;

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
						Properties properties = entry.getProperties();

						for (int i = 0; i < fields.length; i++) {
							if (i > 0) {
								output.print(separator);
							}

							if (properties.containsKey(fields[i])) {
								output.print(properties.getProperty(fields[i]));
							} else if (fields[i].startsWith("+")) {
								output.print(evaluate(fields[i].substring(1),
										entry, commandLine));
							} else {
								throw new FrameworkException("missing field");
							}
						}

						output.println();
					}
				} finally {
					if ((output != null) && (output != System.out)) {
						output.close();
					}
				}
			} finally {
				if (input != null) {
					input.close();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
	}
	
	/**
	 * Evaluates the special commands.  The {@code +} prefix should be removed
	 * prior to calling this method.  An {@link OptionCompleter} is used to
	 * auto-complete the commands, so only the unique prefix must be provided.
	 * 
	 * @param command the command identifier
	 * @param entry the entry in the result file
	 * @param commandLine the command line options
	 * @return the value of the special command
	 * @throws FrameworkException if the command is not supported
	 * @throws IOException if an I/O error occurred
	 */
	protected String evaluate(String command, ResultEntry entry, 
			CommandLine commandLine) throws IOException {
		OptionCompleter completer = new OptionCompleter("hypervolume",
				"generational", "inverted", "epsilon", "error", "spacing",
				"contribution", "R1", "R2", "R3");
		String option = completer.lookup(command);
		
		if (option == null) {
			throw new FrameworkException("unsupported command");
		}
		
		//load the reference set
		if (referenceSet == null) {
			if (commandLine.hasOption("reference")) {
				referenceSet = new NondominatedPopulation(
						PopulationIO.readObjectives(new File(
								commandLine.getOptionValue("reference"))));
			} else {
				referenceSet = ProblemFactory.getInstance().getReferenceSet(
						commandLine.getOptionValue("problem"));
			}
			
			if (referenceSet == null) {
				throw new FrameworkException("no reference set available");
			}
		}
		
		//create the indicator, these should perhaps be cached for speed
		Indicator indicator = null;
		
		if (option.equals("hypervolume")) {
			indicator = new Hypervolume(problem, referenceSet);
		} else if (option.equals("generational")) {
			indicator = new GenerationalDistance(problem, referenceSet);
		} else if (option.equals("inverted")) {
			indicator = new InvertedGenerationalDistance(problem, referenceSet);
		} else if (option.equals("epsilon")) {
			indicator = new AdditiveEpsilonIndicator(problem, referenceSet);
		} else if (option.equals("error")) {
			indicator = new MaximumParetoFrontError(problem, referenceSet);
		} else if (option.equals("spacing")) {
			indicator = new Spacing(problem);
		} else if (option.equals("contribution")) {
			if (commandLine.hasOption("epsilon")) {
				double[] epsilon = TypedProperties.withProperty("epsilon",
						commandLine.getOptionValue("epsilon")).getDoubleArray(
						"epsilon", null);
				indicator = new Contribution(referenceSet, epsilon);
			} else {
				indicator = new Contribution(referenceSet);
			}
		} else if (option.equals("R1")) {
			indicator = new R1Indicator(problem,
					R1Indicator.getDefaultSubdivisions(problem), referenceSet);
		} else if (option.equals("R2")) {
			indicator = new R2Indicator(problem,
					R2Indicator.getDefaultSubdivisions(problem), referenceSet);
		} else if (option.equals("R3")) {
			indicator = new R3Indicator(problem,
					R3Indicator.getDefaultSubdivisions(problem), referenceSet);
		} else {
			throw new IllegalStateException();
		}
		
		return Double.toString(indicator.evaluate(entry.getPopulation()));
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
