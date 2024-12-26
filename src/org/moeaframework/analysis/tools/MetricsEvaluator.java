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
import java.nio.file.Files;
import java.time.Duration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.MetricFileWriter;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.series.ResultEntry;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.population.EpsilonBoxDominanceArchive;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.DurationUtils;
import org.moeaframework.util.Timer;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for evaluating the approximation sets stored in a result file and computing its metric file.
 */
public class MetricsEvaluator extends CommandLineUtility {
	
	private MetricsEvaluator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
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
		options.addOption(Option.builder()
				.longOpt("overwrite")
				.build());
		options.addOption(Option.builder()
				.longOpt("force")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		File inputFile = new File(commandLine.getOptionValue("input"));
		File outputFile = new File(commandLine.getOptionValue("output"));
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);

		if (commandLine.hasOption("overwrite")) {
			Files.deleteIfExists(outputFile.toPath());
		}
		
		if (!commandLine.hasOption("force")) {
			MetricFileWriter.failIfOutdated(this, inputFile, outputFile);
		}

		NondominatedPopulation referenceSet = OptionUtils.getReferenceSet(commandLine, false);

		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				ResultFileReader reader = ResultFileReader.open(problem, inputFile)) {
			Indicators indicator = Indicators.standard(reader.getProblem(), referenceSet);

			try (MetricFileWriter writer = MetricFileWriter.append(indicator, outputFile)) {
				if (writer.getNumberOfEntries() > 0) {
					System.out.println("Resuming from existing metrics file " + outputFile);
					System.out.println(writer.getNumberOfEntries() + " valid entries");

					for (int i = 0; i < writer.getNumberOfEntries(); i++) {
						if (reader.hasNext()) {
							reader.next();
						} else {
							fail("Output file '" + outputFile + "' contains more entries than input file '" +
									inputFile + "'");
						}
					}
				}

				while (reader.hasNext()) {
					Timer timer = Timer.startNew();
					
					System.out.print("Processing entry " + (writer.getNumberOfEntries()+1) + "...");
					ResultEntry entry = reader.next();

					if (epsilons != null) {
						entry = new ResultEntry(EpsilonBoxDominanceArchive.of(entry.getPopulation(), epsilons),
								entry.getProperties());
					}
						
					writer.write(entry);
					System.out.print("done!");
					
					Duration elapsedTime = Duration.ofMillis(Math.round(1000 * timer.stop()));
					System.out.print(" (");
					System.out.print(DurationUtils.formatHighResolution(elapsedTime));
					System.out.println(")");
				}
			}
		}
		
		System.out.println("Finished!");
	}
	
	/**
	 * Starts the command line utility for evaluating the approximation sets stored in a result file and computing its
	 * metric file.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new MetricsEvaluator().start(args);
	}

}
