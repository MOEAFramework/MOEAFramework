/* Copyright 2009-2022 David Hadka
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
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.TimingProblem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for evaluating an algorithm using many 
 * parameterizations.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.Evaluator <options>}
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
 *     <td>The location of the output file.  Each record in the output file
 *         will correspond to the end-of-run approximation set from each
 *         parameterization read from the parameter sample file.</td>
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
 *     <td>{@code -m, --metrics}</td>
 *     <td>Compute the performance metrics and output a metric file.  This is
 *         equivalent to running {@link ResultFileEvaluator} on the result file
 *         produced by this command.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -r, --reference}</td>
 *     <td>Location of the reference file used when computing the performance
 *         metrics (required if -m is set).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -n, --novariables}</td>
 *     <td>To save on space, do not save decision variables in the results.</td>
 *   </tr>
 * </table>
 */
public class Evaluator extends CommandLineUtility {

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
	public Evaluator() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

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
		options.addOption(Option.builder("b")
				.longOpt("problem")
				.hasArg()
				.argName("name")
				.required()
				.build());
		options.addOption(Option.builder("a")
				.longOpt("algorithm")
				.hasArg()
				.argName("name")
				.required()
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
		options.addOption(Option.builder("e")
				.longOpt("epsilon")
				.hasArg()
				.argName("e1,e2,...")
				.build());
		options.addOption(Option.builder("m")
				.longOpt("metrics")
				.build());
		options.addOption(Option.builder("r")
				.longOpt("reference")
				.hasArg()
				.argName("file")
				.build());
		options.addOption(Option.builder("n")
				.longOpt("novariables")
				.build());
		options.addOption(Option.builder("f")
				.longOpt("force")
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		File outputFile = new File(commandLine.getOptionValue("output"));
		File inputFile = new File(commandLine.getOptionValue("input"));

		ParameterFile parameterFile = new ParameterFile(new File(commandLine
				.getOptionValue("parameterFile")));

		// sanity check to ensure input hasn't been modified after the output
		if (!commandLine.hasOption("force") &&
				(outputFile.lastModified() > 0L) && 
				(inputFile.lastModified() > outputFile.lastModified())) {
			throw new FrameworkException(
					"input appears to be newer than output");
		}

		// open the resources and begin processing
		try {
			problem = ProblemFactory.getInstance().getProblem(commandLine
					.getOptionValue("problem"));

			try {
				input = new SampleReader(new FileReader(inputFile),
						parameterFile);

				try {
					if (commandLine.hasOption("metrics")) {
						NondominatedPopulation referenceSet = null;

						// load reference set and create the quality indicator
						if (commandLine.hasOption("reference")) {
							referenceSet = new NondominatedPopulation(
									PopulationIO.readObjectives(new File(
											commandLine.getOptionValue(
													"reference"))));
						} else {
							referenceSet = ProblemFactory.getInstance()
									.getReferenceSet(commandLine.getOptionValue(
											"problem"));
						}

						if (referenceSet == null) {
							throw new FrameworkException(
									"no reference set available");
						}

						QualityIndicator indicator = new QualityIndicator(
								problem, referenceSet);

						output = new MetricFileWriter(indicator, outputFile);
					} else {
						output = new ResultFileWriter(problem, outputFile,
								!commandLine.hasOption("novariables"));
					}

					// resume at the last good output
					for (int i = 0; i < output.getNumberOfEntries(); i++) {
						if (input.hasNext()) {
							input.next();
						} else {
							throw new FrameworkException(
									"output has more entries than input");
						}
					}

					// setup any default parameters
					TypedProperties defaultProperties = new TypedProperties();

					if (commandLine.hasOption("properties")) {
						for (String property : commandLine
								.getOptionValues("properties")) {
							String[] tokens = property.split("=");
							
							if (tokens.length == 2) {
								defaultProperties.setString(tokens[0],
										tokens[1]);
							} else {
								throw new FrameworkException(
										"malformed property argument");
							}
						}
					}

					if (commandLine.hasOption("epsilon")) {
						defaultProperties.setString("epsilon", commandLine
								.getOptionValue("epsilon"));
					}

					// seed the pseudo-random number generator
					if (commandLine.hasOption("seed")) {
						PRNG.setSeed(Long.parseLong(commandLine
								.getOptionValue("seed")));
					}

					// process the remaining runs
					while (input.hasNext()) {
						TypedProperties properties = input.next();
						properties.addAll(defaultProperties);

						process(commandLine.getOptionValue("algorithm"),
								properties);
					}
				} finally {
					if (output != null) {
						output.close();
					}
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
	}

	/**
	 * Performs a single run of the specified algorithm using the parameters.
	 * 
	 * @param algorithmName the algorithm name
	 * @param properties the parameters stored in a properties object
	 * @throws IOException if an I/O error occurred
	 */
	protected void process(String algorithmName, TypedProperties properties)
			throws IOException {
		// instrument the problem to record timing information
		TimingProblem timingProblem = new TimingProblem(problem);

		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				algorithmName, properties, timingProblem);

		// find the maximum NFE to run
		if (!properties.contains("maxEvaluations")) {
			throw new FrameworkException("maxEvaluations not defined");
		}

		int maxEvaluations = (int)properties.getDouble("maxEvaluations", -1);

		// run the algorithm
		long startTime = System.nanoTime();
		while (!algorithm.isTerminated()
				&& (algorithm.getNumberOfEvaluations() < maxEvaluations)) {
			algorithm.step();
		}
		long endTime = System.nanoTime();

		// extract the result and free any resources
		NondominatedPopulation result = algorithm.getResult();
		algorithm.terminate();

		// apply epsilon-dominance if required
		if (properties.contains("epsilon")) {
			double[] epsilon = properties.getDoubleArray("epsilon", null);
			result = EpsilonHelper.convert(result, epsilon);
		}

		// record instrumented data
		TypedProperties timingData = new TypedProperties();
		timingData.setDouble("EvaluationTime", timingProblem.getTime());
		timingData.setDouble("TotalTime", (endTime - startTime) / 1e9);

		// write result to output
		output.append(new ResultEntry(result, timingData));
	}

	/**
	 * Starts the command line utility for evaluating an algorithm using many
	 * parameterizations.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Evaluator().start(args);
	}

}
