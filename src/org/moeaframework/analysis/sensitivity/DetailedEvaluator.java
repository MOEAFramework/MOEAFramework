/* Copyright 2009-2016 David Hadka
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
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for evaluating an algorithm using many 
 * parameterizations.  Unlike {@link Evaluator}, this class outputs runtime
 * data.  Each run is stored in a separate file.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.DetailedEvaluator <options>}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
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
	 * The problem being evaluated.
	 */
	protected Problem problem;

	/**
	 * The output writer where end-of-run results are stored.
	 */
	protected OutputWriter output;

	/**
	 * The sample reader from which input parameters are read.
	 */
	protected SampleReader input;

	/**
	 * Constructs the command line utility for evaluating an algorithm using
	 * many parameterizations.
	 */
	public DetailedEvaluator() {
		super();
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(OptionBuilder
				.withLongOpt("parameterFile")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('p'));
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
				.withLongOpt("problem")
				.hasArg()
				.withArgName("name")
				.isRequired()
				.create('b'));
		options.addOption(OptionBuilder
				.withLongOpt("algorithm")
				.hasArg()
				.withArgName("name")
				.isRequired()
				.create('a'));
		options.addOption(OptionBuilder
				.withLongOpt("frequency")
				.hasArg()
				.withArgName("nfe")
				.create('f'));
		options.addOption(OptionBuilder
				.withLongOpt("properties")
				.hasArgs()
				.withArgName("p1=v1;p2=v2;...")
				.withValueSeparator(';')
				.create('x'));
		options.addOption(OptionBuilder
				.withLongOpt("seed")
				.hasArg()
				.withArgName("value")
				.create('s'));
		options.addOption(OptionBuilder
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.create('e'));
		options.addOption(OptionBuilder
				.withLongOpt("novariables")
				.create('n'));

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		String outputFilePattern = commandLine.getOptionValue("output");
		ParameterFile parameterFile = new ParameterFile(new File(commandLine.getOptionValue("parameterFile")));
		File inputFile = new File(commandLine.getOptionValue("input"));
		int frequency = 1000;
		
		if (commandLine.hasOption("frequency")) {
			frequency = Integer.parseInt(commandLine.getOptionValue("frequency"));
		}
		
		// open the resources and begin processing
		try {
			problem = ProblemFactory.getInstance().getProblem(commandLine.getOptionValue("problem"));

			try {
				input = new SampleReader(new FileReader(inputFile), parameterFile);
				int count = 1;

				while (input.hasNext()) {
					try {
						String outputFileName = String.format(outputFilePattern, count);
						System.out.print("Processing " + outputFileName + "...");
						File outputFile = new File(outputFileName);
						
						if (outputFile.exists()) {
							outputFile.delete();
						}
						
						output = new ResultFileWriter(problem, outputFile, !commandLine.hasOption("novariables"));
	
						// setup any default parameters
						Properties defaultProperties = new Properties();
	
						if (commandLine.hasOption("properties")) {
							for (String property : commandLine.getOptionValues("properties")) {
								String[] tokens = property.split("=");
								
								if (tokens.length == 2) {
									defaultProperties.setProperty(tokens[0], tokens[1]);
								} else {
									throw new FrameworkException("malformed property argument");
								}
							}
						}
	
						if (commandLine.hasOption("epsilon")) {
							defaultProperties.setProperty("epsilon", commandLine.getOptionValue("epsilon"));
						}
	
						// seed the pseudo-random number generator
						if (commandLine.hasOption("seed")) {
							PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
						}
	
						Properties properties = input.next();
						properties.putAll(defaultProperties);
	
						process(commandLine.getOptionValue("algorithm"), properties, frequency);
						
						System.out.println("done.");
					} finally {
						if (output != null) {
							output.close();
						}
					}
					
					count++;
				}
			} finally {
				if (input != null) {
					input.close();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
		
		System.out.println("Finished!");
	}

	@SuppressWarnings("unchecked")
	protected void process(String algorithmName, Properties properties, int frequency)
			throws IOException {
		if (!properties.containsKey("maxEvaluations")) {
			throw new FrameworkException("maxEvaluations not defined");
		}
		
		int maxEvaluations = (int)Double.parseDouble(properties.getProperty("maxEvaluations"));
		
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

		Accumulator accumulator = instrumenter.getLastAccumulator();

		for (int i=0; i<accumulator.size("NFE"); i++) {
			Properties metadata = new Properties();
			metadata.setProperty("NFE", accumulator.get("NFE", i).toString());
			metadata.setProperty("ElapsedTime", accumulator.get("Elapsed Time", i).toString());
			
			Iterable<Solution> solutions = (Iterable<Solution>)accumulator.get("Approximation Set", i);
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
