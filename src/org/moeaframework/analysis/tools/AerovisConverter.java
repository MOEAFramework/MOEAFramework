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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.moeaframework.analysis.sensitivity.ProblemStub;
import org.moeaframework.analysis.sensitivity.ResultEntry;
import org.moeaframework.analysis.sensitivity.ResultFileReader;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.CommandLineUtility;

/**
 * Converts a result file into an Aerovis input file.  Aerovis is a commercial
 * software package for exploring high-dimensional datasets, but is freely
 * available for non-commercial academic use at https://www.decisionvis.com/.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.tools.AerovisConverter <options>}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
 *   <tr>
 *     <td>{@code -b, --problem}</td>
 *     <td>The name of the problem.  This name should reference one of the
 *         problems recognized by the MOEA Framework.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -d, --dimension}</td>
 *     <td>The number of objectives (use instead of -b).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -i, --input}</td>
 *     <td>The result file containing the input data.
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the extract data will be saved.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -r, --reduced}</td>
 *     <td>Only include objective values in the output file.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -n, --names}</td>
 *     <td>The names for the decision variables and objectives, separated by
 *         commas.</td>
 *   </tr>
 * </table>
 */
public class AerovisConverter extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for converting result files into
	 * Aerovis input files.
	 */
	public AerovisConverter() {
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
				.withLongOpt("reduced")
				.create('r'));
		options.addOption(OptionBuilder
				.withLongOpt("names")
				.hasArg()
				.create('n'));
		
		return options;
	}
	
	/**
	 * Prints the header lines to the Aerovis file.
	 * 
	 * @param problem the problem
	 * @param reduced {@code true} if the decision variables are suppressed;
	 *        {@code false} if they are written to the output file
	 * @param attributes the names of the decision variables and/or objectives;
	 *        the length must match the number of variables, objectives, or
	 *        both, otherwise default names are used
	 * @param writer the writer where the output is written
	 */
	private void printHeader(Problem problem, boolean reduced,
			List<String> attributes, PrintWriter writer) {
		int numberOfVariables = problem.getNumberOfVariables();
		int numberOfObjectives = problem.getNumberOfObjectives();
		
		if (reduced) {
			numberOfVariables = 0;
		}
		
		writer.println("# Nondominated Solutions:");
		writer.print("# Format:  Variables = ");
		writer.print(numberOfVariables);
		writer.print(" | Objectives = ");
		writer.println(numberOfObjectives);
		
		if (attributes.size() == numberOfObjectives) {
			writer.print("# <DATA_HEADER>");
			
			for (int i = 0; i < numberOfVariables; i++) {
				writer.print(" Var");
				writer.print(i+1);
			}
			
			for (int i = 0; i < numberOfObjectives; i++) {
				writer.print(" ");
				writer.print(attributes.get(i));
			}
			
			writer.println();
		} else if (attributes.size() == numberOfVariables + numberOfObjectives) {
			writer.print("# <DATA_HEADER>");
			
			for (int i = 0; i < numberOfVariables + numberOfObjectives; i++) {
				writer.print(" ");
				writer.print(attributes.get(i));
			}
			
			writer.println();
		} else {
			if (!attributes.isEmpty()) {
				System.err.println("incorrect number of names, using defaults");
			}
			
			writer.print("# <DATA_HEADER>");
			
			for (int i = 0; i < numberOfVariables; i++) {
				writer.print(" Var");
				writer.print(i+1);
			}
			
			for (int i = 0; i < numberOfObjectives; i++) {
				writer.print(" Obj");
				writer.print(i+1);
			}
			
			writer.println();
		}
		
		writer.println("# <GEN_HEADER> NFE, Time (sec)");
		writer.println("#");
	}
	
	/**
	 * Converts and writes the contents of the result file to the Aerovis
	 * format.
	 * 
	 * @param problem the problem
	 * @param reduced {@code true} if the decision variables are suppressed;
	 *        {@code false} if they are written to the output file
	 * @param reader the result file reader
	 * @param writer the writer where the output is written
	 */
	private void convert(Problem problem, boolean reduced,
			ResultFileReader reader, PrintWriter writer) {
		int numberOfVariables = problem.getNumberOfVariables();
		int numberOfObjectives = problem.getNumberOfObjectives();
		
		if (reduced) {
			numberOfVariables = 0;
		}
		
		while (reader.hasNext()) {
			ResultEntry entry = reader.next();
			Population population = entry.getPopulation();
			Properties properties = entry.getProperties();
			
			if (population.isEmpty()) {
				continue;
			}
			
			if (properties.containsKey("NFE")) {
				writer.print(properties.getProperty("NFE"));
			} else {
				writer.print("0");
			}
			
			writer.print(" ");
			
			if (properties.containsKey("ElapsedTime")) {
				writer.println(properties.getProperty("ElapsedTime"));
			} else {
				writer.println("0");
			}
			
			writer.println("#");
			
			for (Solution solution : population) {
				for (int i = 0; i < numberOfVariables; i++) {
					if (i > 0) {
						writer.print(" ");
					}
						
					writer.print(solution.getVariable(i));
				}
				
				for (int i = 0; i < numberOfObjectives; i++) {
					if ((i > 0) || (numberOfVariables > 0)) {
						writer.print(" ");
					}
					
					writer.print(solution.getObjective(i));
				}
				
				writer.println();
			}
			writer.println("#");
		}
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		Problem problem = null;
		ResultFileReader reader = null;
		PrintWriter writer = null;
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
		
		try {
			if (commandLine.hasOption("problem")) {
				problem = ProblemFactory.getInstance().getProblem(
						commandLine.getOptionValue("problem"));
			} else {
				problem = new ProblemStub(Integer.parseInt(
						commandLine.getOptionValue("dimension")));
			}
			
			try {
				reader = new ResultFileReader(problem,
						new File(commandLine.getOptionValue("input")));
				
				try {
					writer = new PrintWriter(new FileWriter(
							commandLine.getOptionValue("output")));
					
					printHeader(problem, reduced, attributes, writer);
					convert(problem, reduced, reader, writer);
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
	 * Starts the command line utility for converting result files into
	 * Aerovis input files.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new AerovisConverter().start(args);
	}

}
