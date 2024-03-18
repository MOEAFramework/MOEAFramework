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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.CommandLineUtility;

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
		
		OptionUtils.addProblemOption(options, true);
		
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
		options.addOption(Option.builder("r")
				.longOpt("reduced")
				.build());
		options.addOption(Option.builder("n")
				.longOpt("names")
				.hasArg()
				.build());
		
		return options;
	}
	
	/**
	 * Prints the header lines to the ARFF file.
	 * 
	 * @param problem the problem
	 * @param reduced {@code true} if the decision variables are suppressed; {@code false} if they are written to the
	 *        output file
	 * @param attributes the names of the decision variables and/or objectives; the length must match the number of
	 *        variables, objectives, or both, otherwise default names are used
	 * @param writer the writer where the output is written
	 */
	private void printHeader(Problem problem, boolean reduced, List<String> attributes, PrintWriter writer) {
		int numberOfVariables = problem.getNumberOfVariables();
		int numberOfObjectives = problem.getNumberOfObjectives();
		
		if (reduced) {
			numberOfVariables = 0;
		}
		
		writer.println("% Title: MOEA Framework Data Set");
		writer.println("% Date: " + new Date());
		writer.print("@RELATION \"");
		writer.print(problem.getName());
		writer.println("\"");
		
		if (attributes.size() == numberOfObjectives) {
			for (int i = 0; i < numberOfVariables; i++) {
				writer.print("@ATTRIBUTE Var");
				writer.print(i+1);
				writer.println(" NUMERIC");
			}
			
			for (int i = 0; i < numberOfObjectives; i++) {
				writer.print("@ATTRIBUTE ");
				writer.print(attributes.get(i));
				writer.println(" NUMERIC");
			}
		} else if (attributes.size() == numberOfVariables + numberOfObjectives) {
			for (int i = 0; i < numberOfVariables + numberOfObjectives; i++) {
				writer.print("@ATTRIBUTE ");
				writer.print(attributes.get(i));
				writer.println(" NUMERIC");
			}
		} else {
			if (!attributes.isEmpty()) {
				System.err.println("incorrect number of names, using defaults");
			}
			
			for (int i = 0; i < numberOfVariables; i++) {
				writer.print("@ATTRIBUTE Var");
				writer.print(i+1);
				writer.println(" NUMERIC");
			}
			
			for (int i = 0; i < numberOfObjectives; i++) {
				writer.print("@ATTRIBUTE Obj");
				writer.print(i+1);
				writer.println(" NUMERIC");
			}
		}
		
		writer.println("@DATA");
	}
	
	/**
	 * Prints the given population as the ARFF data section.
	 * 
	 * @param problem the problem
	 * @param reduced {@code true} if the decision variables are suppressed; {@code false} if they are written to the
	 *        output file
	 * @param population the population to write
	 * @param writer the writer where the output is written
	 */
	private void printData(Problem problem, boolean reduced, Population population, PrintWriter writer) {
		int numberOfVariables = problem.getNumberOfVariables();
		int numberOfObjectives = problem.getNumberOfObjectives();
		
		if (reduced) {
			numberOfVariables = 0;
		}
		
		for (Solution solution : population) {
			for (int i = 0; i < numberOfVariables; i++) {
				if (i > 0) {
					writer.print(",");
				}
					
				if (solution.getVariable(i) instanceof RealVariable) {
					writer.print(EncodingUtils.getReal(solution.getVariable(i)));
				} else {
					writer.print("?");
				}
			}
			
			for (int i = 0; i < numberOfObjectives; i++) {
				if ((i > 0) || (numberOfVariables > 0)) {
					writer.print(",");
				}
				
				writer.print(solution.getObjective(i));
			}
			
			writer.println();
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		boolean reduced = false;
		List<String> attributes = new ArrayList<String>();
		
		if (commandLine.hasOption("reduced")) {
			reduced = true;
		}
		
		if (commandLine.hasOption("names")) {
			String[] names = commandLine.getOptionValue("names").split(",");
			
			for (String name : names) {
				attributes.add(name.trim());
			}
		}
		
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true)) {
			// read in the last entry from the result file
			NondominatedPopulation population = null;
			
			try (ResultFileReader reader = new ResultFileReader(problem,
						new File(commandLine.getOptionValue("input")))) {
				while (reader.hasNext()) {
					population = reader.next().getPopulation();
				}
			}
			
			// check if the population is empty
			if (population.isEmpty()) {
				throw new FrameworkException("population is empty, can not generate ARFF file");
			}
			
			// write the ARFF file
			try (PrintWriter writer = new PrintWriter(new FileWriter(commandLine.getOptionValue("output")))) {
				printHeader(problem, reduced, attributes, writer);
				printData(problem, reduced, population, writer);
			}
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
