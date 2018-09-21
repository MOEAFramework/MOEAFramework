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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.moeaframework.algorithm.PeriodicAction;
import org.moeaframework.analysis.sensitivity.ResultEntry;
import org.moeaframework.analysis.sensitivity.ResultFileWriter;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.ExternalProblem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.io.FileUtils;

/**
 * Command line utility for solving an optimization problem using any of the
 * supported optimization algorithms.  This utility supports solving problems
 * defined within the MOEA Framework as well as compatible external problems.
 * See {@link ExternalProblem} for details on developing an external problem.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.ResultFileEvaluator <options>}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
 *   <tr>
 *     <td>{@code -f, --output}</td>
 *     <td>The output file location.</td>
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
 *     <td>{@code -n, --numberOfEvaluations}</td>
 *     <td>The number of function evaluations (NFE) to run.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -F, --runtimeFrequency}</td>
 *     <td>The frequency, in NFE, that records are saved to the output file
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -v, --variables}</td>
 *     <td>Comma-separated list of decision variable specifications.  Use
 *         {@code "R(<lb>;<ub>)"} for real-valued, {@code "B(<length>)"}
 *         for binary, and {@code "P(<length>)"} for permutations.
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --objectives}</td>
 *     <td>The number of objectives.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -c, --constraints}</td>
 *     <td>The number of constraints.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -l, --lowerBounds}</td>
 *     <td>Lower bounds of real-valued decision variables, separated by commas.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -u, --upperBounds}</td>
 *     <td>Upper bounds of real-valued decision variables, separated by commas.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -S, --useSocket}</td>
 *     <td>Communicate with external problem using sockets.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -H, --hostname}</td>
 *     <td>Hostname used when using sockets (default localhost)</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -P, --port}</td>
 *     <td>Port used when using sockets (default 16801).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -D, --startupDelay}</td>
 *     <td>Delay in seconds between running the executable and starting
 *         optimization (default 1).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -t, --test}</td>
 *     <td>Runs a few trials to test the connection with the external problem.
 *     </td>
 *   </tr>
 * </table>
 */
public class Solve extends CommandLineUtility {

	/**
	 * Constructs the command line utility for solving an optimization problem.
	 */
	public Solve() {
		super();
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('f'));
		options.addOption(OptionBuilder
				.withLongOpt("problem")
				.hasArg()
				.withArgName("name")
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
				.withLongOpt("numberOfEvaluations")
				.hasArg()
				.withArgName("value")
				.isRequired()
				.create('n'));
		options.addOption(OptionBuilder
				.withLongOpt("runtimeFrequency")
				.hasArg()
				.withArgName("value")
				.create('F'));
		options.addOption(OptionBuilder
				.withLongOpt("variables")
				.hasArg()
				.withArgName("v1,v2,...")
				.create('v'));
		options.addOption(OptionBuilder
				.withLongOpt("objectives")
				.hasArg()
				.withArgName("value")
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("constraints")
				.hasArg()
				.withArgName("value")
				.create('c'));
		options.addOption(OptionBuilder
				.withLongOpt("lowerBounds")
				.hasArg()
				.withArgName("v1,v2,...")
				.create("l"));
		options.addOption(OptionBuilder
				.withLongOpt("upperBounds")
				.hasArg()
				.withArgName("v1,v2,...")
				.create('u'));
		
		options.addOption(OptionBuilder
				.withLongOpt("useSocket")
				.create('S'));
		options.addOption(OptionBuilder
				.withLongOpt("hostname")
				.hasArg()
				.withArgName("value")
				.create('H'));
		options.addOption(OptionBuilder
				.withLongOpt("port")
				.hasArg()
				.withArgName("value")
				.create('P'));
		options.addOption(OptionBuilder
				.withLongOpt("startupDelay")
				.hasArg()
				.withArgName("value")
				.create('D'));
		
		options.addOption(OptionBuilder
				.withLongOpt("test")
				.hasOptionalArg()
				.withArgName("trials")
				.create('t'));

		return options;
	}
	
	/**
	 * Parses a single variable specification from the command line.  This
	 * method is case sensitive.
	 * 
	 * @param token the variable specification from the command line
	 * @return the generated variable object
	 * @throws ParseException if an error occurred while parsing the variable
	 *         specification
	 */
	private Variable parseVariableSpecification(String token)
			throws ParseException {
		if (!token.endsWith(")")) {
			throw new ParseException("invalid variable specification '" +
					token + "', not properly formatted");
		}
		
		if (token.startsWith("R(")) {
			// real-valued decision variable
			String content = token.substring(2, token.length()-1);
			int index = content.indexOf(';');
			
			if (index >= 0) {
				double lowerBound = Double.parseDouble(
						content.substring(0, index));
				double upperBound = Double.parseDouble(
						content.substring(index+1, content.length()));
				return EncodingUtils.newReal(lowerBound, upperBound);
			} else {
				throw new ParseException("invalid real specification '" +
						token + "', expected R(<lb>,<ub>)");
			}
		} else if (token.startsWith("B(")) {
			String content = token.substring(2, token.length()-1);
			
			try {
				int length = Integer.parseInt(content.trim());
				return EncodingUtils.newBinary(length);
			} catch (NumberFormatException e) {
				throw new ParseException("invalid binary specification '" +
						token + "', expected B(<length>)");
			}
		} else if (token.startsWith("P(")) {
			String content = token.substring(2, token.length()-1);
			
			try {
				int length = Integer.parseInt(content.trim());
				return EncodingUtils.newPermutation(length);
			} catch (NumberFormatException e) {
				throw new ParseException("invalid permutation specification '"
						+ token + "', expected P(<length>)");
			}
		} else {
			throw new ParseException("invalid variable specification '"
					+ token + "', unknown type");
		}
	}
	
	/**
	 * Parses the decision variable specification either from the
	 * {@code --lowerBounds} and {@code --upperBounds} options or the
	 * {@code --variables} option.
	 * 
	 * @param commandLine the command line arguments
	 * @return the parsed variable specifications
	 * @throws ParseException if an error occurred while parsing the variable
	 *         specifications
	 */
	private List<Variable> parseVariables(CommandLine commandLine)
			throws ParseException {
		List<Variable> variables = new ArrayList<Variable>();
		
		if (commandLine.hasOption("lowerBounds") &&
				commandLine.hasOption("upperBounds")) {
			String[] lowerBoundTokens =
					commandLine.getOptionValue("lowerBounds").split(",");
			String[] upperBoundTokens =
					commandLine.getOptionValue("upperBounds").split(",");
			
			if (lowerBoundTokens.length != upperBoundTokens.length) {
				throw new ParseException("lower bound and upper bounds not " +
						"the same length");
			}
			
			for (int i = 0; i < lowerBoundTokens.length; i++) {
				double lowerBound = Double.parseDouble(lowerBoundTokens[i]);
				double upperBound = Double.parseDouble(upperBoundTokens[i]);
				variables.add(EncodingUtils.newReal(lowerBound, upperBound));
			}
		} else if (commandLine.hasOption("variables")) {
			String[] tokens =
					commandLine.getOptionValue("variables").split(",");
			
			for (String token : tokens) {
				variables.add(parseVariableSpecification(
						token.trim().toUpperCase()));
			}
		} else {
			throw new ParseException("must specify either the problem, the " +
					"variables, or the lower and upper bounds arguments");
		}
		
		return variables;
	}
	
	/**
	 * Creates an external problem using the information provided on the
	 * command line.
	 * 
	 * @param commandLine the command line arguments
	 * @return the external problem
	 * @throws ParseException if an error occurred parsing any of the command
	 *         line options
	 * @throws IOException if an error occurred starting the external program
	 */
	private Problem createExternalProblem(final CommandLine commandLine)
			throws ParseException, IOException {
		final int numberOfObjectives = Integer.parseInt(
				commandLine.getOptionValue("objectives"));
		
		final int numberOfConstraints = commandLine.hasOption("constraints") ?
				Integer.parseInt(commandLine.getOptionValue("constraints")) :
				0;
		
		final List<Variable> variables = parseVariables(commandLine);
		
		if (commandLine.hasOption("useSocket")) {
			String hostname = null; // default to localhost
			int port = 16801;
			int delay = 1;
			
			if (commandLine.hasOption("hostname")) {
				hostname = commandLine.getOptionValue("hostname");
			}
			
			if (commandLine.hasOption("port")) {
				port = Integer.parseInt(commandLine.getOptionValue("port"));
			}
			
			if (commandLine.hasOption("startupDelay")) {
				delay = Integer.parseInt(
						commandLine.getOptionValue("startupDelay"));
			}
			
			if (commandLine.getArgs().length > 0) {
				// the command to run is specified on the command line
				System.out.print("Running ");
				System.out.println(StringUtils.join(commandLine.getArgs()));
				new ProcessBuilder(commandLine.getArgs()).start();
			}
			
			try {
				System.out.print("Sleeping for ");
				System.out.print(delay);
				System.out.println(" seconds");
				Thread.sleep(delay*1000);
			} catch (InterruptedException e) {
				// do nothing
			}
			
			System.out.println("Starting optimization");
			return new ExternalProblem(hostname, port) {
				
				@Override
				public String getName() {
					return StringUtils.join(commandLine.getArgs());
				}
	
				@Override
				public int getNumberOfVariables() {
					return variables.size();
				}
	
				@Override
				public int getNumberOfObjectives() {
					return numberOfObjectives;
				}
	
				@Override
				public int getNumberOfConstraints() {
					return numberOfConstraints;
				}
	
				@Override
				public Solution newSolution() {
					Solution solution = new Solution(variables.size(),
							numberOfObjectives, numberOfConstraints);
					
					for (int i = 0; i < variables.size(); i++) {
						solution.setVariable(i, variables.get(i).copy());
					}
					
					return solution;
				}
				
			};
		} else {
			return new ExternalProblem(commandLine.getArgs()) {
	
				@Override
				public String getName() {
					return StringUtils.join(commandLine.getArgs());
				}
	
				@Override
				public int getNumberOfVariables() {
					return variables.size();
				}
	
				@Override
				public int getNumberOfObjectives() {
					return numberOfObjectives;
				}
	
				@Override
				public int getNumberOfConstraints() {
					return numberOfConstraints;
				}
	
				@Override
				public Solution newSolution() {
					Solution solution = new Solution(variables.size(),
							numberOfObjectives, numberOfConstraints);
					
					for (int i = 0; i < variables.size(); i++) {
						solution.setVariable(i, variables.get(i).copy());
					}
					
					return solution;
				}
				
			};
		}
	}
	
	/**
	 * Runs a number of trials as a way to quickly test if the connection
	 * between this solver and the problem is functional.
	 * 
	 * @param problem the problem
	 * @param commandLine the command line arguments
	 */
	private void runTests(Problem problem, CommandLine commandLine) {
		int trials = 5;
		
		if (commandLine.getOptionValue("test") != null) {
			trials = Integer.parseInt(commandLine.getOptionValue("test"));
		}
		
		try {
			int count = 0;
			RandomInitialization initialization = new RandomInitialization(
					problem, trials);
			
			Solution[] solutions = initialization.initialize();
			
			for (Solution solution : solutions) {
				System.out.println("Running test " + (++count) + ":");
				
				for (int j = 0; j < solution.getNumberOfVariables(); j++) {
					System.out.print("  Variable ");
					System.out.print(j+1);
					System.out.print(" = ");
					System.out.println(solution.getVariable(j));
				}
				
				System.out.println("  * Evaluating solution *");
				problem.evaluate(solution);
				System.out.println("  * Evaluation complete *");
				
				for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
					System.out.print("  Objective ");
					System.out.print(j+1);
					System.out.print(" = ");
					System.out.println(solution.getObjective(j));
				}
				
				for (int j = 0; j < solution.getNumberOfConstraints(); j++) {
					System.out.print("  Constraint ");
					System.out.print(j+1);
					System.out.print(" = ");
					System.out.println(solution.getConstraint(j));
				}
				
				if ((solution.getNumberOfConstraints() > 0) &&
						solution.violatesConstraints()) {
					System.out.println("  Solution is infeasible (non-zero " +
							"constraint value)!");
				}
			}
			
			System.out.println("Test succeeded!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Test failed!  Please see the error message " + 
					"above for details.");
		}
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		// parse the algorithm parameters
		Properties properties = new Properties();

		if (commandLine.hasOption("properties")) {
			for (String property : commandLine.getOptionValues("properties")) {
				String[] tokens = property.split("=");

				if (tokens.length == 2) {
					properties.setProperty(tokens[0], tokens[1]);
				} else {
					throw new FrameworkException("malformed property argument");
				}
			}
		}

		if (commandLine.hasOption("epsilon")) {
			properties.setProperty("epsilon", 
					commandLine.getOptionValue("epsilon"));
		}

		int maxEvaluations = Integer.parseInt(
				commandLine.getOptionValue("numberOfEvaluations"));

		// seed the pseudo-random number generator
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}

		// parse the runtime frequency
		int runtimeFrequency = 100;

		if (commandLine.hasOption("runtimeFrequency")) {
			runtimeFrequency = Integer.parseInt(
					commandLine.getOptionValue("runtimeFrequency"));
		}

		// open the resources and begin processing
		Problem problem = null;
		Algorithm algorithm = null;
		ResultFileWriter writer = null;
		File file = new File(commandLine.getOptionValue("output"));
		
		try {
			if (commandLine.hasOption("problem")) {
				problem = ProblemFactory.getInstance().getProblem(
						commandLine.getOptionValue("problem"));
			} else {
				problem = createExternalProblem(commandLine);
			}
			
			if (commandLine.hasOption("test")) {
				runTests(problem, commandLine);
				return;
			}

			try {
				algorithm = AlgorithmFactory.getInstance().getAlgorithm(
						commandLine.getOptionValue("algorithm"),
						properties,
						problem);

				// if the output file exists, delete first to avoid appending
				FileUtils.delete(file);
				
				try {
					writer = new ResultFileWriter(problem, file);

					algorithm = new RuntimeCollector(algorithm,
							runtimeFrequency, writer);
					
					while (!algorithm.isTerminated() &&
							(algorithm.getNumberOfEvaluations() < maxEvaluations)) {
						algorithm.step();
					}
				} finally {
					if (writer != null) {
						writer.close();
					}
				}
			} finally {
				if (algorithm != null) {
					algorithm.terminate();
				}
			}
		} catch (ParseException e) {
			throw new IOException(e);
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
	}

	/**
	 * Wraps an algorithm to write the approximation set and periodic intervals.
	 */
	private static class RuntimeCollector extends PeriodicAction {
		
		/**
		 * The result file writer where the runtime information is stored.
		 */
		private final ResultFileWriter writer;

		/**
		 * The time, in nanoseconds, this collector was created.  This roughly
		 * corresponds to the time the algorithm starts, assuming that the
		 * algorithm is run immediately following its setup.
		 */
		private final long startTime;

		/**
		 * Constructs a new wrapper to collect runtime dynamics.
		 * 
		 * @param algorithm the wrapped algorithm
		 * @param frequency the frequency at which the runtime snapshots are
		 *        recorded
		 * @param writer the result file writer where the runtime information
		 *        is stored
		 */
		public RuntimeCollector(Algorithm algorithm, int frequency,
				ResultFileWriter writer) {
			super(algorithm, frequency, FrequencyType.EVALUATIONS);
			this.writer = writer;
			
			startTime = System.nanoTime();
		}

		@Override
		public void doAction() {
			double elapsedTime = (System.nanoTime() - startTime) * 1e-9;
			NondominatedPopulation result = algorithm.getResult();

			Properties properties = new Properties();
			properties.setProperty("NFE",
					Integer.toString(algorithm.getNumberOfEvaluations()));
			properties.setProperty("ElapsedTime",
					Double.toString(elapsedTime));

			try {
				writer.append(new ResultEntry(result, properties));
			} catch (IOException e) {
				throw new FrameworkException(e);
			}
		}
		
	}

	/**
	 * Starts the command line utility for solving an optimization problem.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Solve().start(args);
	}

}
