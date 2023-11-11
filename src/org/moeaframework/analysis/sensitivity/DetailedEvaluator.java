/* Copyright 2009-2023 David Hadka
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
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Observation;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for evaluating an algorithm using many 
 * parameterizations.  Unlike {@link Evaluator}, this class outputs runtime
 * data.  Each run is stored in a separate file.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.DetailedEvaluator <options>}
 * 
 * <table>
 *   <caption style="text-align: left">Arguments:</caption>
 *   <tr>
 *     <td>{@code -p, --parameterFile}</td>
 *     <td>Location of the parameter configuration file (required)</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -i, --input}</td>
 *     <td>Location of the parameter sample file (required)</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>Expression defining where the output files are saved.  The
 *         path should include {@code %d}, which is replaced by the index of
 *         the run.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -b, --problem}</td>
 *     <td>The name of the problem.  This name should reference one of the
 *         problems recognized by the MOEA Framework.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -a, --algorithm}</td>
 *     <td>The name of the algorithm.  This name should reference one of the
 *         algorithms recognized by the MOEA Framework.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -f, --frequency}</td>
 *     <td>The frequency, in NFE, that records are saved to the result file.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -x, --properties}</td>
 *     <td>Semicolon-separated list of additional parameters for the
 *         algorithm (e.g., {@code -x maxEvaluations=10000;populationSize=100}.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -s, --seed}</td>
 *     <td>The random number seed used for each run.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -e, --epsilon}</td>
 *     <td>The epsilon values for limiting the size of the results.  This
 *         epsilon value is also used for any algorithms that include an
 *         epsilon parameter.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -n, --novariables}</td>
 *     <td>To save on space, do not save decision variables in the results.</td>
 *   </tr>
 * </table>
 */
public class DetailedEvaluator extends CommandLineUtility {

	/**
	 * The output writer where end-of-run results are stored.
	 */
	protected OutputWriter output;

	/**
	 * Constructs the command line utility for evaluating an algorithm using
	 * many parameterizations.
	 */
	public DetailedEvaluator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options, false);
		OptionUtils.addEpsilonOption(options);


		options.addOption(Option.builder("p")
				.longOpt("parameterFile")
				.hasArg()
				.argName("file")
				.required()
				.build());
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
		options.addOption(Option.builder("a")
				.longOpt("algorithm")
				.hasArg()
				.argName("name")
				.required()
				.build());
		options.addOption(Option.builder("f")
				.longOpt("frequency")
				.hasArg()
				.argName("nfe")
				.build());
		options.addOption(Option.builder("x")
				.longOpt("properties")
				.hasArgs()
				.argName("p1=v1;p2=v2;...")
				.valueSeparator(';')
				.build());
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("n")
				.longOpt("novariables")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		String outputFilePattern = commandLine.getOptionValue("output");
		ParameterFile parameterFile = new ParameterFile(new File(commandLine.getOptionValue("parameterFile")));
		File inputFile = new File(commandLine.getOptionValue("input"));
		double[] epsilon = OptionUtils.getEpsilon(commandLine);
		
		int frequency = 1000;
		
		if (commandLine.hasOption("frequency")) {
			frequency = Integer.parseInt(commandLine.getOptionValue("frequency"));
		}
		
		// open the resources and begin processing
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, false);
				SampleReader input = new SampleReader(new FileReader(inputFile), parameterFile)) {
			int count = 1;

			while (input.hasNext()) {
				String outputFileName = String.format(outputFilePattern, count);
				System.out.print("Processing " + outputFileName + "...");
				File outputFile = new File(outputFileName);
						
				if (outputFile.exists()) {
					outputFile.delete();
				}	
						
				try (ResultFileWriter output = new ResultFileWriter(problem, outputFile,
						!commandLine.hasOption("novariables"))) {
					// setup any default parameters
					TypedProperties defaultProperties = new TypedProperties();
	
					if (commandLine.hasOption("properties")) {
						for (String property : commandLine.getOptionValues("properties")) {
							String[] tokens = property.split("=");
								
							if (tokens.length == 2) {
								defaultProperties.setString(tokens[0], tokens[1]);
							} else {
								throw new FrameworkException("malformed property argument");
							}
						}
					}
	
					if (epsilon != null) {
						defaultProperties.setDoubleArray("epsilon", epsilon);
					}
	
					// seed the pseudo-random number generator
					if (commandLine.hasOption("seed")) {
						PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
					}
	
					TypedProperties properties = input.next();
					properties.addAll(defaultProperties);
	
					process(commandLine.getOptionValue("algorithm"), properties, problem, frequency);
						
					System.out.println("done.");
				}
					
				count++;
			}
		}
		
		System.out.println("Finished!");
	}

	@SuppressWarnings("unchecked")
	protected void process(String algorithmName, TypedProperties properties, Problem problem, int frequency)
			throws IOException {
		int maxEvaluations = properties.getTruncatedInt("maxEvaluations");
		
		if (maxEvaluations < 0) {
			throw new FrameworkException("maxEvaluations must be a non-negative number");
		}
		
		Instrumenter instrumenter = new Instrumenter()
				.withProblem(problem)
				.withFrequency(frequency)
				.attachApproximationSetCollector()
				.attachElapsedTimeCollector();

		new Executor()
				.withSameProblemAs(instrumenter)
				.withAlgorithm(algorithmName)
				.withMaxEvaluations(maxEvaluations)
				.withInstrumenter(instrumenter)
				.withProperties(properties)
				.run();

		Observations observations = instrumenter.getObservations();

		for (Observation observation : observations) {
			TypedProperties metadata = new TypedProperties();
			metadata.setInt("NFE", observation.getNFE());
			metadata.setString("ElapsedTime", observation.get("Elapsed Time").toString());
			
			Iterable<Solution> solutions = (Iterable<Solution>)observation.get("Approximation Set");
			NondominatedPopulation result = new NondominatedPopulation(solutions);
			
			output.append(new ResultEntry(result, metadata));
		}
	}

	/**
	 * Starts the command line utility for evaluating an algorithm using many
	 * parameterizations.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new DetailedEvaluator().start(args);
	}

}
