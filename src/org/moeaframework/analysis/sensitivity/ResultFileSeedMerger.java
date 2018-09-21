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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for merging the approximation sets in one or more result
 * files across the seeds.  For example, if {@link Evaluator} was run with 25
 * different seeds and 100 different parameterizations, then each of the 25
 * output files (from each seed) will contain 100 records.  This command
 * produces a single file with 100 records, where each record is produced by
 * combining the approximation sets for that record across all seeds.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.ResultFileSeedMerger <options> <files>}
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
 * </table>
 */
public class ResultFileSeedMerger extends CommandLineUtility {

	/**
	 * Constructs the command line utility for merging the approximation sets
	 * in one or more result files across the seeds.
	 */
	public ResultFileSeedMerger() {
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
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.create('e'));

		return options;
	}

	/**
	 * Returns all non-dominated approximation sets in the specified result 
	 * file.
	 * 
	 * @param file the result file
	 * @param problem the problem
	 * @return all non-dominated approximation sets in the specified result 
	 *         file
	 * @throws IOException if an I/O error occurred
	 */
	private List<NondominatedPopulation> load(File file, Problem problem)
			throws IOException {
		ResultFileReader reader = null;

		try {
			reader = new ResultFileReader(problem, file);
			List<NondominatedPopulation> data = 
					new ArrayList<NondominatedPopulation>();

			while (reader.hasNext()) {
				data.add(reader.next().getPopulation());
			}

			return data;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		List<List<NondominatedPopulation>> entries = 
				new ArrayList<List<NondominatedPopulation>>();
		Problem problem = null;
		ResultFileWriter writer = null;

		try {
			// setup the problem
			if (commandLine.hasOption("problem")) {
				problem = ProblemFactory.getInstance().getProblem(commandLine
						.getOptionValue("problem"));
			} else {
				problem = new ProblemStub(Integer.parseInt(commandLine
						.getOptionValue("dimension")));
			}

			// load data from all input files
			for (String filename : commandLine.getArgs()) {
				entries.add(load(new File(filename), problem));
			}

			// validate the inputs
			if (entries.isEmpty()) {
				throw new IllegalArgumentException(
						"requires at least one file");
			}

			int numberOfEntries = -1;

			for (int i = 0; i < entries.size(); i++) {
				if (numberOfEntries < 0) {
					numberOfEntries = entries.get(i).size();
				} else if (numberOfEntries != entries.get(i).size()) {
					throw new IllegalArgumentException(
							"unbalanced number of entries: "
									+ commandLine.getArgs()[i]);
				}
			}

			// process and output the merged sets
			try {
				writer = new ResultFileWriter(problem, new File(
						commandLine.getOptionValue("output")));

				for (int i = 0; i < numberOfEntries; i++) {
					NondominatedPopulation mergedSet = null;

					// configure epsilon-dominance
					if (commandLine.hasOption("epsilon")) {
						double[] epsilon = TypedProperties.withProperty(
								"epsilon", commandLine.getOptionValue(
								"epsilon")).getDoubleArray("epsilon", null);
						mergedSet = new EpsilonBoxDominanceArchive(epsilon);
					} else {
						mergedSet = new NondominatedPopulation();
					}

					for (int j = 0; j < entries.size(); j++) {
						mergedSet.addAll(entries.get(j).get(i));
					}

					writer.append(new ResultEntry(mergedSet));
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
	}

	/**
	 * Starts the command line utility for merging the approximation sets in one
	 * or more result files across the seeds.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileSeedMerger().start(args);
	}

}
