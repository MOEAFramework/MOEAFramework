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
import org.moeaframework.core.population.Population;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.format.TableFormat;

/**
 * Converts a result file into a different file format, such as CSV, Markdown, Latex, ARFF, etc.  If the result file
 * contains multiple entries, only the last entry is converted.
 */
public class ResultFileConverter extends CommandLineUtility {
	
	private ResultFileConverter() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
		OptionUtils.addFormatOption(options);
		
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
		
		return options;
	}
	
	@Override
	public void run(CommandLine commandLine) throws Exception {
		TableFormat format = OptionUtils.getFormat(commandLine);
		
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				ResultFileReader input = ResultFileReader.openLegacy(problem, new File(commandLine.getOptionValue("input")));
				PrintWriter output = createOutputWriter(commandLine.getOptionValue("output"))) {
			Population population = Iterators.last(input.iterator()).getPopulation();
			
			if (population == null || population.isEmpty()) {
				fail("ERROR: Result file is empty");
			}
			
			population.save(format, output);
		}
	}
	
	/**
	 * Starts the command line utility for converting result files into different file formats.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileConverter().start(args);
	}

}
