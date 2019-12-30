/* Copyright 2009-2018 David Hadka
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
				.withLongOpt("metrics")
				.create('m'));
		options.addOption(OptionBuilder
				.withLongOpt("reference")
				.hasArg()
				.withArgName("file")
				.create('r'));
		options.addOption(OptionBuilder
				.withLongOpt("novariables")
				.create('n'));
		options.addOption(OptionBuilder
				.withLongOpt("force")
				.create('f'));

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
					Properties defaultProperties = new Properties();

					if (commandLine.hasOption("properties")) {
						for (String property : commandLine
								.getOptionValues("properties")) {
							String[] tokens = property.split("=");
							
							if (tokens.length == 2) {
								defaultProperties.setProperty(tokens[0],
										tokens[1]);
							} else {
								throw new FrameworkException(
										"malformed property argument");
							}
						}
					}

					if (commandLine.hasOption("epsilon")) {
						defaultProperties.setProperty("epsilon", commandLine
								.getOptionValue("epsilon"));
					}

					// seed the pseudo-random number generator
					if (commandLine.hasOption("seed")) {
						PRNG.setSeed(Long.parseLong(commandLine
								.getOptionValue("seed")));
					}

					// process the remaining runs
					while (input.hasNext()) {
						Properties properties = input.next();
						properties.putAll(defaultProperties);

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
	protected void process(String algorithmName, Properties properties)
			throws IOException {
		// instrument the problem to record timing information
		TimingProblem timingProblem = new TimingProblem(problem);

		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				algorithmName, properties, timingProblem);

		// find the maximum NFE to run
		if (!properties.containsKey("maxEvaluations")) {
			throw new FrameworkException("maxEvaluations not defined");
		}

		int maxEvaluations = (int) Double.parseDouble(properties
				.getProperty("maxEvaluations"));

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
		if (properties.containsKey("epsilon")) {
			TypedProperties typedProperties = new TypedProperties(properties);
			double[] epsilon = typedProperties.getDoubleArray("epsilon", null);
			
			result = EpsilonHelper.convert(result, epsilon);
		}

		// record instrumented data
		Properties timingData = new Properties();
		timingData.setProperty("EvaluationTime",
				Double.toString(timingProblem.getTime()));
		timingData.setProperty("TotalTime",
				Double.toString((endTime - startTime) / 1e9));

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
