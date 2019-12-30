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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.io.FileUtils;

/**
 * Command line utility for merging the approximation sets stored in one or more
 * result files.  The output is a single approximation set containing the
 * non-dominated solutions from all input files.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.ResultFileMerger <options> <files>}
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
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the extract data will be saved.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -e, --epsilon}</td>
 *     <td>The epsilon values for limiting the size of the results.  This
 *         epsilon value is also used for any algorithms that include an
 *         epsilon parameter.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -r, --resultFile}</td>
 *     <td>Output a result file, which includes all of the decision variables
 *         when combined with the -b option.</td>
 *   </tr>
 * </table>
 */
public class ResultFileMerger extends CommandLineUtility {

	/**
	 * Constructs the command line utility for merging the approximation sets 
	 * stored in one or more result files.
	 */
	public ResultFileMerger() {
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
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.create('e'));
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("resultFile")
				.create('r'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		Problem problem = null;
		NondominatedPopulation mergedSet = null;
		ResultFileReader reader = null;

		// setup the merged non-dominated population
		if (commandLine.hasOption("epsilon")) {
			double[] epsilon = TypedProperties.withProperty("epsilon",
					commandLine.getOptionValue("epsilon")).getDoubleArray(
					"epsilon", null);
			mergedSet = new EpsilonBoxDominanceArchive(epsilon);
		} else {
			mergedSet = new NondominatedPopulation();
		}

		try {
			// setup the problem
			if (commandLine.hasOption("problem")) {
				problem = ProblemFactory.getInstance().getProblem(commandLine
						.getOptionValue("problem"));
			} else {
				problem = new ProblemStub(Integer.parseInt(commandLine
						.getOptionValue("dimension")));
			}

			// read in result files
			for (String filename : commandLine.getArgs()) {
				try {
					reader = new ResultFileReader(problem, new File(filename));

					while (reader.hasNext()) {
						mergedSet.addAll(reader.next().getPopulation());
					}
				} finally {
					if (reader != null) {
						reader.close();
					}
				}
			}
			
			File output = new File(commandLine.getOptionValue("output"));

			// output merged set
			if (commandLine.hasOption("resultFile")) {
				ResultFileWriter writer = null;
				
				//delete the file to avoid appending
				FileUtils.delete(output);
				
				try {
					writer = new ResultFileWriter(problem, output);
					
					writer.append(new ResultEntry(mergedSet));
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
			} else {
				PopulationIO.writeObjectives(output, mergedSet);
			}

		} finally {
			if (problem != null) {
				problem.close();
			}
		}
	}
	
	/**
	 * Starts the command line utility for merging the approximation sets 
	 * stored in one or more result files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileMerger().start(args);
	}

}
