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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for merging the approximation sets in one or more result files across the seeds.  For example,
 * if {@link EndOfRunEvaluator} was run with 25 different seeds and 100 different parameterizations, then each of the 25
 * output files (from each seed) will contain 100 records.  This command produces a single file with 100 records, where
 * each record is produced by combining the approximation sets for that record across all seeds.
 */
public class ResultFileSeedMerger extends CommandLineUtility {

	private ResultFileSeedMerger() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		OptionUtils.addProblemOption(options);
		OptionUtils.addEpsilonOption(options);

		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.required()
				.build());

		return options;
	}

	/**
	 * Returns all non-dominated approximation sets in the specified result file.
	 * 
	 * @param file the result file
	 * @param problem the problem
	 * @return all non-dominated approximation sets in the specified result file
	 * @throws IOException if an I/O error occurred
	 */
	private List<NondominatedPopulation> load(File file, Problem problem) throws IOException {
		try (ResultFileReader reader = ResultFileReader.open(problem, file)) {
			List<NondominatedPopulation> data = new ArrayList<>();

			while (reader.hasNext()) {
				data.add(new NondominatedPopulation(reader.next().getPopulation()));
			}

			return data;
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		List<List<NondominatedPopulation>> entries = new ArrayList<>();
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);


		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true)) {
			// load data from all input files
			for (String filename : commandLine.getArgs()) {
				entries.add(load(new File(filename), problem));
			}

			// validate the inputs
			if (entries.isEmpty()) {
				throw new ParseException("Requires at least one result file");
			}

			int numberOfEntries = -1;

			for (int i = 0; i < entries.size(); i++) {
				if (numberOfEntries < 0) {
					numberOfEntries = entries.get(i).size();
				} else if (numberOfEntries != entries.get(i).size()) {
					throw new IOException(commandLine.getArgs()[i] +
							" is not valid, all input files must contains the same number of entries");
				}
			}

			// process and output the merged sets
			try (ResultFileWriter writer = ResultFileWriter.append(problem,
					new File(commandLine.getOptionValue("output")))) {
				for (int i = 0; i < numberOfEntries; i++) {
					NondominatedPopulation mergedSet = null;

					// configure epsilon-dominance
					if (epsilons != null) {
						mergedSet = new EpsilonBoxDominanceArchive(epsilons);
					} else {
						mergedSet = new NondominatedPopulation();
					}

					for (int j = 0; j < entries.size(); j++) {
						mergedSet.addAll(entries.get(j).get(i));
					}

					writer.write(new ResultEntry(mergedSet));
				}
			}
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileSeedMerger().start(args);
	}

}
