/* Copyright 2009-2015 David Hadka
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
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for evaluating the approximation sets stored in a
 * result file and computing its metric file.
 */
public class ResultFileEvaluator extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for evaluating the approximation sets
	 * stored in a result file and computing its metric file.
	 */
	public ResultFileEvaluator() {
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
				.isRequired()
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("reference")
				.hasArg()
				.withArgName("file")
				.create('r'));
		options.addOption(OptionBuilder
				.withLongOpt("force")
				.create('f'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		ResultFileReader reader = null;
		MetricFileWriter writer = null;
		Problem problem = null;
		NondominatedPopulation referenceSet = null;

		File inputFile = new File(commandLine.getOptionValue("input"));
		File outputFile = new File(commandLine.getOptionValue("output"));

		// sanity check to ensure input hasn't been modified after the output
		if (!commandLine.hasOption("force") &&
				(outputFile.lastModified() > 0L) && 
				(inputFile.lastModified() > outputFile.lastModified())) {
			throw new FrameworkException(
					"input appears to be newer than output");
		}

		// load reference set and create the quality indicator
		if (commandLine.hasOption("reference")) {
			referenceSet = new NondominatedPopulation(PopulationIO
					.readObjectives(new File(commandLine
							.getOptionValue("reference"))));
		} else {
			referenceSet = ProblemFactory.getInstance().getReferenceSet(
					commandLine.getOptionValue("problem"));
		}

		if (referenceSet == null) {
			throw new FrameworkException("no reference set available");
		}

		// open the resources and begin processing
		try {
			// setup the problem
			if (commandLine.hasOption("problem")) {
				problem = ProblemFactory.getInstance().getProblem(commandLine
						.getOptionValue("problem"));
			} else {
				problem = new ProblemStub(Integer.parseInt(commandLine
						.getOptionValue("dimension")));
			}

			QualityIndicator indicator = new QualityIndicator(problem,
					referenceSet);

			try {
				reader = new ResultFileReader(problem, inputFile);

				try {
					writer = new MetricFileWriter(indicator, outputFile);

					// resume at the last good output
					for (int i = 0; i < writer.getNumberOfEntries(); i++) {
						if (reader.hasNext()) {
							reader.next();
						} else {
							throw new FrameworkException(
									"output has more entries than input");
						}
					}

					// evaluate the remaining entries
					while (reader.hasNext()) {
						writer.append(reader.next());
					}
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
	}
	
	/**
	 * Starts the command line utility for evaluating the approximation sets 
	 * stored in a result file and computing its metric file.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileEvaluator().start(args);
	}

}
