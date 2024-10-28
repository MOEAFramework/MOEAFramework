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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.Constraint;
import org.moeaframework.core.objective.Objective;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.Iterators;

/**
 * Converts a result file into an ARFF file that can be loaded into various data mining software (e.g.,
 * <a href="http://www.cs.waikato.ac.nz/ml/weka/">Weka</a>). This tool has two limitations that the user must be aware.
 * First, it only converts the last entry in the result file.  Second, it can only convert real-valued decision
 * variables.  Any other decision variable types will appear as missing values in the ARFF file.
 */
public class ARFFConverter extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for converting result files into ARFF files.
	 */
	public ARFFConverter() {
		super();
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options);
		
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
		
		return options;
	}
	
	/**
	 * Prints the header lines to the ARFF file.
	 * 
	 * @param problem the problem
	 * @param writer the writer where the output is written
	 */
	private void printHeader(Problem problem, PrintWriter writer) {
		int numberOfVariables = problem.getNumberOfVariables();
		int numberOfObjectives = problem.getNumberOfObjectives();
		int numberOfConstraints = problem.getNumberOfConstraints();
		
		Solution prototype = problem.newSolution();
		
		writer.println("% Title: MOEA Framework Data Set");
		writer.println("% Date: " + new Date());
		writer.print("@RELATION \"");
		writer.print(problem.getName());
		writer.println("\"");
		
		for (int i = 0; i < numberOfVariables; i++) {
			writer.print("@ATTRIBUTE ");
			writer.print(Variable.getNameOrDefault(prototype.getVariable(i), i));
			writer.println(" NUMERIC");
		}
			
		for (int i = 0; i < numberOfObjectives; i++) {
			writer.print("@ATTRIBUTE ");
			writer.print(Objective.getNameOrDefault(prototype.getObjective(i), i));
			writer.println(" NUMERIC");
		}
		
		for (int i = 0; i < numberOfConstraints; i++) {
			writer.print("@ATTRIBUTE ");
			writer.print(Constraint.getNameOrDefault(prototype.getConstraint(i), i));
			writer.println(" NUMERIC");
		}
		
		writer.println("@DATA");
	}
	
	/**
	 * Prints the given population as the ARFF data section.
	 * 
	 * @param problem the problem
	 * @param population the population to write
	 * @param writer the writer where the output is written
	 */
	private void printData(Problem problem, Population population, PrintWriter writer) {
		int numberOfVariables = problem.getNumberOfVariables();
		int numberOfObjectives = problem.getNumberOfObjectives();
		int numberOfConstraints = problem.getNumberOfConstraints();
		
		for (Solution solution : population) {
			boolean includeSeparator = false;

			for (int i = 0; i < numberOfVariables; i++) {
				if (includeSeparator) {
					writer.print(",");
				}
					
				if (solution.getVariable(i) instanceof RealVariable) {
					writer.print(EncodingUtils.getReal(solution.getVariable(i)));
				} else {
					writer.print("?");
				}
				
				includeSeparator = true;
			}
			
			for (int i = 0; i < numberOfObjectives; i++) {
				if (includeSeparator) {
					writer.print(",");
				}
				
				writer.print(solution.getObjective(i).getValue());
				includeSeparator = true;
			}
			
			for (int i = 0; i < numberOfConstraints; i++) {
				if (includeSeparator) {
					writer.print(",");
				}
				
				writer.print(solution.getConstraint(i).getValue());
				includeSeparator = true;
			}
			
			writer.println();
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				ResultFileReader input = new ResultFileReader(problem, new File(commandLine.getOptionValue("input")));
				PrintWriter output = new PrintWriter(new FileWriter(commandLine.getOptionValue("output")))) {
			Population population = Iterators.last(input.iterator()).getPopulation();
			
			// check if the population is empty
			if (population == null || population.isEmpty()) {
				throw new FrameworkException("population is empty, can not generate ARFF file");
			}
			
			// write the ARFF file
			printHeader(input.getProblem(), output);
			printData(input.getProblem(), population, output);
		}
	}
	
	/**
	 * Starts the command line utility for converting result files into ARFF files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ARFFConverter().start(args);
	}

}
