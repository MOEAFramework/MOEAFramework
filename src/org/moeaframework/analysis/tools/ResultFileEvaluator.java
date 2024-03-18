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
import org.moeaframework.analysis.EpsilonHelper;
import org.moeaframework.analysis.io.MetricFileWriter;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for evaluating the approximation sets stored in a result file and computing its metric file.
 * <p>
 * Usage: {@code java -classpath "lib/*" org.moeaframework.analysis.tools.ResultFileEvaluator <options>}
 * 
 * <table>
 *   <caption style="text-align: left">Arguments:</caption>
 *   <tr>
 *     <td>{@code -b, --problem}</td>
 *     <td>The name of the problem.  This name should reference one of the problems recognized by the MOEA
 *         Framework.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -d, --dimension}</td>
 *     <td>The number of objectives (use instead of -b).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -i, --input}</td>
 *     <td>The result file containing the input data.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the extract data will be saved.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -e, --epsilon}</td>
 *     <td>The epsilon values for limiting the size of the results.  This epsilon value is also used for any algorithms
 *         that include an epsilon parameter.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -r, --reference}</td>
 *     <td>Location of the reference file used when computing the performance metrics (required if -m is set).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -f, --force}</td>
 *     <td>This command performs some sanity checks to ensure the data is consistent, such as checking the file
 *         modification dates.  If a consistency error is reported, use this option to continue processing even
 *         though the data may be inconsistent.</td>
 *   </tr>
 * </table>
 */
public class ResultFileEvaluator extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for evaluating the approximation sets stored in a result file and computing
	 * its metric file.
	 */
	public ResultFileEvaluator() {
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
				.required()
				.build());
		options.addOption(Option.builder("f")
				.longOpt("force")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		File inputFile = new File(commandLine.getOptionValue("input"));
		File outputFile = new File(commandLine.getOptionValue("output"));
		double[] epsilon = OptionUtils.getEpsilon(commandLine);

		// sanity check to ensure input hasn't been modified after the output
		if (!commandLine.hasOption("force") && (outputFile.lastModified() > 0L) && 
				(inputFile.lastModified() > outputFile.lastModified())) {
			throw new FrameworkException("input appears to be newer than output");
		}

		NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine);

		// open the resources and begin processing
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true)) {
			// validate the reference set
			for (Solution solution : referenceSet) {
				if (solution.getNumberOfObjectives() != problem.getNumberOfObjectives()) {
					throw new FrameworkException(
							"reference set contains invalid number of objectives");
				}
			}

			Indicators indicator = Indicators.standard(problem, referenceSet);

			try (ResultFileReader reader = new ResultFileReader(problem, inputFile);
					MetricFileWriter writer = MetricFileWriter.append(indicator, outputFile)) {
				// resume at the last good output
				for (int i = 0; i < writer.getNumberOfEntries(); i++) {
					if (reader.hasNext()) {
						reader.next();
					} else {
						throw new FrameworkException("output has more entries than input");
					}
				}

				// evaluate the remaining entries
				while (reader.hasNext()) {
					ResultEntry entry = reader.next();

					if (epsilon != null) {
						entry = new ResultEntry(EpsilonHelper.convert(entry.getPopulation(), epsilon),
								entry.getProperties());
					}
						
					writer.append(entry);
				}
			}
		}
	}
	
	/**
	 * Starts the command line utility for evaluating the approximation sets stored in a result file and computing its
	 * metric file.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileEvaluator().start(args);
	}

}
