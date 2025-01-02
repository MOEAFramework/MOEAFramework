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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for validating the contents of a result file.
 */
public class ResultFileValidator extends CommandLineUtility {
	
	private ResultFileValidator() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);

		options.addOption(Option.builder("c")
				.longOpt("count")
				.hasArg()
				.argName("N")
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
		boolean failed = false;
		int expectedCount = Integer.parseInt(commandLine.getOptionValue("count"));

		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			for (String filename : commandLine.getArgs()) {
				try (ResultFileReader reader = ResultFileReader.openLegacy(problem, new File(filename))) {
					int count = Iterators.count(reader.iterator());
					failed |= count != expectedCount;

					output.println(filename + " " + (count == expectedCount ? "PASS" :
						"FAIL (incorrect number of entries: " + count + ")"));
				}
			}
		}
		
		if (failed) {
			fail("Detected invalid result file, see logs for details");
		}
	}
	
	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileValidator().start(args);
	}

}
