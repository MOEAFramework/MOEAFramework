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
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.algorithm.extension.ProgressExtension;
import org.moeaframework.algorithm.extension.ProgressExtension.DefaultProgressListener;
import org.moeaframework.algorithm.extension.RuntimeCollectorExtension;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.Defined;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.constraint.Equal;
import org.moeaframework.core.constraint.GreaterThan;
import org.moeaframework.core.constraint.GreaterThanOrEqual;
import org.moeaframework.core.constraint.LessThan;
import org.moeaframework.core.constraint.LessThanOrEqual;
import org.moeaframework.core.constraint.NotEqual;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.objective.Maximize;
import org.moeaframework.core.objective.Minimize;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.problem.ExternalProblem;
import org.moeaframework.problem.ExternalProblem.Builder;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility for solving an optimization problem using any of the supported optimization algorithms.  This
 * utility supports solving problems defined within the MOEA Framework as well as compatible external problems.
 * See {@link ExternalProblem} for details on developing an external problem.
 */
public class Solve extends CommandLineUtility {
	
	private static final Map<String, Class<? extends Variable>> VARIABLE_ALIASES;
	
	private static final Map<String, Class<? extends Objective>> OBJECTIVE_ALIASES;
	
	private static final Map<String, Class<? extends Constraint>> CONSTRAINT_ALIASES;
	
	private static final char TOKEN_SEPARATOR = ';';
	
	static {
		VARIABLE_ALIASES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		OBJECTIVE_ALIASES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		CONSTRAINT_ALIASES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		
		VARIABLE_ALIASES.put("R", RealVariable.class);
		VARIABLE_ALIASES.put("Real", RealVariable.class);
		VARIABLE_ALIASES.put("B", BinaryVariable.class);
		VARIABLE_ALIASES.put("Binary", BinaryVariable.class);
		VARIABLE_ALIASES.put("I", BinaryIntegerVariable.class);
		VARIABLE_ALIASES.put("Int", BinaryIntegerVariable.class);
		VARIABLE_ALIASES.put("Integer", BinaryIntegerVariable.class);
		VARIABLE_ALIASES.put("P", Permutation.class);
		VARIABLE_ALIASES.put("S", Subset.class);
		
		OBJECTIVE_ALIASES.put("Min", Minimize.class);
		OBJECTIVE_ALIASES.put("Max", Maximize.class);
		
		CONSTRAINT_ALIASES.put("LT", LessThan.class);
		CONSTRAINT_ALIASES.put("LEQ", LessThanOrEqual.class);
		CONSTRAINT_ALIASES.put("GT", GreaterThan.class);
		CONSTRAINT_ALIASES.put("GEQ", GreaterThanOrEqual.class);
		CONSTRAINT_ALIASES.put("EQ", Equal.class);
		CONSTRAINT_ALIASES.put("NEQ", NotEqual.class);
	}

	Solve() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
		OptionUtils.addEpsilonOption(options);
		OptionUtils.addPropertiesOption(options);

		options.addOption(Option.builder("f")
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
		options.addOption(Option.builder("s")
				.longOpt("seed")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("n")
				.longOpt("numberOfEvaluations")
				.hasArg()
				.argName("value")
				.required()
				.build());
		options.addOption(Option.builder("F")
				.longOpt("runtimeFrequency")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("v")
				.longOpt("variables")
				.hasArgs()
				.argName("spec")
				.build());
		options.addOption(Option.builder("o")
				.longOpt("objectives")
				.hasArg()
				.argName("spec")
				.build());
		options.addOption(Option.builder("c")
				.longOpt("constraints")
				.hasArg()
				.argName("spec")
				.build());
		options.addOption(Option.builder("l")
				.longOpt("lowerBounds")
				.hasArgs()
				.argName("l1,l2,...")
				.valueSeparator(',')
				.build());
		options.addOption(Option.builder("u")
				.longOpt("upperBounds")
				.hasArgs()
				.argName("u1,u2,...")
				.valueSeparator(',')
				.build());
		
		options.addOption(Option.builder("S")
				.longOpt("useSocket")
				.build());
		options.addOption(Option.builder("H")
				.longOpt("hostname")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("P")
				.longOpt("port")
				.hasArg()
				.argName("value")
				.build());
		options.addOption(Option.builder("r")
				.longOpt("retries")
				.hasArg()
				.argName("value")
				.build());
		
		options.addOption(Option.builder("t")
				.longOpt("test")
				.optionalArg(true)
				.argName("trials")
				.build());
		
		return options;
	}
	
	/**
	 * Parses a single specification token from the command line using {@link Defined#createInstance(Class, String)}.
	 * 
	 * @param returnType the return type or expected type for the specification
	 * @param aliases aliases recognized for this specification
	 * @param token the token to parse
	 * @return the parsed result
	 * @throws ParseException if an error occurred while parsing the specification
	 */
	private <T> T parseSpecificationToken(Class<? extends T> returnType, Map<String, Class<? extends T>> aliases,
			String token) throws ParseException {
		Pattern pattern = Pattern.compile("([a-zA-Z]+)(?:\\(([^\\)]+)\\))?");
		Matcher matcher = pattern.matcher(token);
		
		if (!matcher.matches()) {
			throw new ParseException("Invalid definition for " + returnType.getSimpleName() +
					", not properly formatted: '" + token + "'");
		}
		
		String identifier = matcher.group(1);
		String args = matcher.group(2);
		
		if (aliases.containsKey(identifier)) {
			identifier = aliases.get(identifier).getName();
		}
		
		T result = Defined.createInstance(returnType, identifier + (args == null ? "" : "(" + args + ")"));
		
		if (result == null) {
			throw new ParseException("Invalid definition for " + returnType.getSimpleName() +
					", type not recognized: '" + token + "'");
		}
		
		return result;
	}
	
	private <T> List<T> parseSpecifications(CommandLine commandLine, String option, Class<? extends T> returnType,
			Map<String, Class<? extends T>> aliases, Supplier<T> defaultSupplier) throws ParseException {
		List<T> result = new ArrayList<>();
		
		if (!commandLine.hasOption(option)) {
			return result;
		}
		
		if (defaultSupplier != null) {
			try {
				int numberOfObjectives = Integer.parseInt(commandLine.getOptionValue(option));
				
				for (int i = 0; i < numberOfObjectives; i++) {
					result.add(defaultSupplier.get());
				}
				
				return result;
			} catch (NumberFormatException e) {
				// fall through
			}
		}
		
		String[] tokens = commandLine.getOptionValue(option).split(Pattern.quote(String.valueOf(TOKEN_SEPARATOR)));
		
		for (String token : tokens) {
			result.add(parseSpecificationToken(returnType, aliases, token.trim()));
		}
		
		return result;
	}
	
	List<Objective> parseObjectives(CommandLine commandLine) throws ParseException {
		return parseSpecifications(commandLine, "objectives", Objective.class, OBJECTIVE_ALIASES,
				Objective::createDefault);
	}
	
	List<Constraint> parseConstraints(CommandLine commandLine) throws ParseException {
		return parseSpecifications(commandLine, "constraints", Constraint.class, CONSTRAINT_ALIASES,
				Constraint::createDefault);
	}
	
	/**
	 * Parses the decision variable specification either from the {@code --lowerBounds} and {@code --upperBounds}
	 * options or the {@code --variables} option.
	 * 
	 * @param commandLine the command line arguments
	 * @return the parsed variable specifications
	 * @throws ParseException if an error occurred while parsing the variable specifications
	 */
	List<Variable> parseVariables(CommandLine commandLine) throws ParseException {		
		if (commandLine.hasOption("variables") &&
				(commandLine.hasOption("lowerBounds") || commandLine.hasOption("upperBounds"))) {
			throw new ParseException("Can not combine --variables with --lowerBounds / --upperBounds");
		}
		
		if (commandLine.hasOption("variables")) {
			return parseSpecifications(commandLine, "variables", Variable.class, VARIABLE_ALIASES, null);
		}

		if (commandLine.hasOption("lowerBounds") && commandLine.hasOption("upperBounds")) {
			List<Variable> variables = new ArrayList<>();
			String[] lowerBoundTokens = commandLine.getOptionValues("lowerBounds");
			String[] upperBoundTokens = commandLine.getOptionValues("upperBounds");
			
			if (lowerBoundTokens.length != upperBoundTokens.length) {
				throw new ParseException("--lowerBounds and --upperBounds must be the same length");
			}
			
			for (int i = 0; i < lowerBoundTokens.length; i++) {
				double lowerBound = Double.parseDouble(lowerBoundTokens[i]);
				double upperBound = Double.parseDouble(upperBoundTokens[i]);
				variables.add(new RealVariable(lowerBound, upperBound));
			}
			
			return variables;
		}
		
		if (commandLine.hasOption("lowerBounds") || commandLine.hasOption("upperBounds")) {
			throw new ParseException("Must provide both --lowerBounds and --upperBounds");
		}
		
		throw new ParseException("Missing variable specification");
	}
	
	/**
	 * Creates an external problem using the information provided on the command line.
	 * 
	 * @param commandLine the command line arguments
	 * @return the external problem
	 * @throws ParseException if an error occurred parsing any of the command line options
	 * @throws IOException if an error occurred starting the external program
	 */
	Problem createExternalProblem(final CommandLine commandLine) throws ParseException, IOException {
		final List<Variable> variables = parseVariables(commandLine);
		final List<Objective> objectives = parseObjectives(commandLine);
		final List<Constraint> constraints = parseConstraints(commandLine);
		
		if (variables.isEmpty()) {
			throw new ParseException("At least one variable must be defined");
		}
		
		if (objectives.isEmpty()) {
			throw new ParseException("At least one objective must be defined");
		}
		
		Builder builder = new Builder();
		
		if (commandLine.hasOption("useSocket")) {
			String hostname = "127.0.0.1";
			int port = ExternalProblem.DEFAULT_PORT;
			
			if (commandLine.hasOption("hostname")) {
				hostname = commandLine.getOptionValue("hostname");
			}
			
			if (commandLine.hasOption("port")) {
				port = Integer.parseInt(commandLine.getOptionValue("port"));
			}
			
			builder.withSocket(hostname, port);
			builder.withRetries(Integer.parseInt(commandLine.getOptionValue("retries", "5")), Duration.ofSeconds(1));
		}
		
		if (commandLine.getArgs().length > 0) {
			System.out.print("Running ");
			System.out.println(String.join(" ", commandLine.getArgs()));
			builder.withCommand(commandLine.getArgs());
		}
			
		return new ExternalProblem(builder) {

			@Override
			public String getName() {
				return String.join(" ", commandLine.getArgs());
			}

			@Override
			public int getNumberOfVariables() {
				return variables.size();
			}

			@Override
			public int getNumberOfObjectives() {
				return objectives.size();
			}

			@Override
			public int getNumberOfConstraints() {
				return constraints.size();
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(variables.size(), objectives.size(), constraints.size());

				for (int i = 0; i < variables.size(); i++) {
					solution.setVariable(i, variables.get(i).copy());
				}
				
				for (int i = 0; i < objectives.size(); i++) {
					solution.setObjective(i, objectives.get(i).copy());
				}
				
				for (int i = 0; i < constraints.size(); i++) {
					solution.setConstraint(i, constraints.get(i).copy());
				}

				return solution;
			}

		};
	}
	
	/**
	 * Runs a number of trials as a way to quickly test if the connection between this solver and the problem is
	 * functional.
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
			RandomInitialization initialization = new RandomInitialization(problem);
			
			Solution[] solutions = initialization.initialize(trials);
			
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
					System.out.println(solution.getObjectiveValue(j));
				}
				
				for (int j = 0; j < solution.getNumberOfConstraints(); j++) {
					System.out.print("  Constraint ");
					System.out.print(j+1);
					System.out.print(" = ");
					System.out.println(solution.getConstraintValue(j));
				}
				
				if ((solution.getNumberOfConstraints() > 0) && solution.violatesConstraints()) {
					System.out.println("  Solution is infeasible (non-zero constraint value)!");
				}
			}
			
			System.out.println("Test succeeded!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Test failed!  Please see the error message above for details.");
		}
	}

	@Override
	public void run(CommandLine commandLine) throws IOException, ParseException {
		TypedProperties properties = OptionUtils.getProperties(commandLine);
		Epsilons epsilons = OptionUtils.getEpsilons(commandLine);

		if (epsilons != null) {
			properties.setDoubleArray("epsilon", epsilons.toArray());
		}

		int maxEvaluations = Integer.parseInt(commandLine.getOptionValue("numberOfEvaluations"));

		// seed the pseudo-random number generator
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine.getOptionValue("seed")));
		}

		// parse the runtime frequency
		Frequency frequency = Frequency.ofEvaluations(100);

		if (commandLine.hasOption("runtimeFrequency")) {
			frequency = Frequency.ofEvaluations(Integer.parseInt(commandLine.getOptionValue("runtimeFrequency")));
		}

		// open the resources and begin processing
		Problem problem = null;
		File file = new File(commandLine.getOptionValue("output"));
		
		try {
			if (commandLine.hasOption("problem")) {
				if (commandLine.getArgs().length > 0) {
					throw new ParseException("No arguments should be specified when using --problem");
				}
				
				problem = OptionUtils.getProblemInstance(commandLine, false);
			} else {
				problem = createExternalProblem(commandLine);
			}
			
			if (commandLine.hasOption("test")) {
				runTests(problem, commandLine);
				return;
			}
			
			System.out.println("Starting optimization");

			Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					commandLine.getOptionValue("algorithm"),
					properties,
					problem);

			try (ResultFileWriter writer = ResultFileWriter.open(problem, file)) {
				algorithm.addExtension(new RuntimeCollectorExtension(writer, frequency));
				algorithm.addExtension(new ProgressExtension().withListener(new DefaultProgressListener()));
				algorithm.run(maxEvaluations);
			}
			
			System.out.println("Result written to " + file);
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Solve().start(args);
	}

}
