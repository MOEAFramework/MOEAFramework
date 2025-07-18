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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for merging the approximation sets stored in one or more result files.  The output is a single
 * approximation set containing the non-dominated solutions from all input files.
 */
public class ResultFileMerger extends CommandLineUtility {

	private ResultFileMerger() {
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

	@Override
	public void run(CommandLine commandLine) throws Exception {
		if (commandLine.getArgs().length == 0) {
			throw new ParseException("Requires at least one result file");
		}
		
		File output = new File(commandLine.getOptionValue("output"));
		NondominatedPopulation mergedSet = OptionUtils.getArchive(commandLine);

		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true)) {
			Problem problemRef = null;
			
			// read in result files
			for (String filename : commandLine.getArgs()) {
				try (ResultFileReader reader = ResultFileReader.open(problem, new File(filename))) {
					if (problemRef == null) {
						problemRef = reader.getProblem();
					}
					
					while (reader.hasNext()) {
						mergedSet.addAll(reader.next().getPopulation());
					}
				}
			}
			
			// output merged set
			try (ResultFileWriter writer = ResultFileWriter.open(problemRef, output)) {
				writer.write(new ResultEntry(mergedSet));
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
		new ResultFileMerger().start(args);
	}

}
