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
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.MetricFileReader;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for validating the contents of a metrics file.
 */
public class MetricsValidator extends CommandLineUtility {
	
	private MetricsValidator() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
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

		try (PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			for (String filename : commandLine.getArgs()) {
				try (MetricFileReader reader = MetricFileReader.open(new File(filename))) {
					int count = 0;
						
					while (reader.hasNext()) {
						reader.next();
						count++;
					}
					
					failed |= count != expectedCount;

					output.println(filename + " " + (count == expectedCount ? "PASS" :
						"FAIL (incorrect number of entries: " + count + ")"));
				}
			}
		}
		
		if (failed) {
			fail("Detected invalid metrics file, see logs for details");
		}
	}
	
	/**
	 * Starts the command line utility for counting the number of entries in a metrics file.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new MetricsValidator().start(args);
	}

}
