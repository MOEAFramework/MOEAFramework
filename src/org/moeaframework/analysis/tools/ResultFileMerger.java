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
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for merging the approximation sets stored in one or more result files.  The output is a single
 * approximation set containing the non-dominated solutions from all input files.
 * <p>
 * Usage: {@code java -classpath "lib/*" org.moeaframework.analysis.tools.ResultFileMerger <options> <files>}
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
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the extract data will be saved.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -e, --epsilon}</td>
 *     <td>The epsilon values for limiting the size of the results.  This epsilon value is also used for any algorithms
 *         that include an epsilon parameter.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -r, --resultFile}</td>
 *     <td>Output a result file, which includes all of the decision variables when combined with the -b option.</td>
 *   </tr>
 * </table>
 */
public class ResultFileMerger extends CommandLineUtility {

	/**
	 * Constructs the command line utility for merging the approximation sets stored in one or more result files.
	 */
	public ResultFileMerger() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options, true);
		OptionUtils.addEpsilonOption(options);
		
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.required()
				.build());
		options.addOption(Option.builder("r")
				.longOpt("resultFile")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		NondominatedPopulation mergedSet = OptionUtils.getArchive(commandLine);

		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true)) {
			// read in result files
			for (String filename : commandLine.getArgs()) {
				try (ResultFileReader reader = new ResultFileReader(problem, new File(filename))) {
					while (reader.hasNext()) {
						mergedSet.addAll(reader.next().getPopulation());
					}
				}
			}
			
			File output = new File(commandLine.getOptionValue("output"));

			// output merged set
			if (commandLine.hasOption("resultFile")) {			
				try (ResultFileWriter writer = ResultFileWriter.overwrite(problem, output)) {
					writer.append(new ResultEntry(mergedSet));
				}
			} else {
				PopulationIO.writeObjectives(output, mergedSet);
			}

		}
	}
	
	/**
	 * Starts the command line utility for merging the approximation sets stored in one or more result files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileMerger().start(args);
	}

}
