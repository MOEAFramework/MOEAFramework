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
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for generating reference sets for a given problem.  The
 * reference set is created by randomly sampling points and building a
 * non-dominated set.  Only problems that implement the
 * {@code AnalyticalProblem} interface can be used.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.SetGenerator <options>}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
 *   <tr>
 *     <td>{@code -b, --problem}</td>
 *     <td>The name of the problem (required).  This name should reference one
 *         of the problems recognized by the MOEA Framework.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -n, --numberOfPoints}</td>
 *     <td>The number of solutions to randomly sample (required).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -s, --seed}</td>
 *     <td>The random number generator seed.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the reference set is saved.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -e, --epsilon}</td>
 *     <td>The epsilon values for limiting the size of the results.  This
 *         epsilon value is also used for any algorithms that include an
 *         epsilon parameter.</td>
 *   </tr>
 * </table>
 */
public class SetGenerator extends CommandLineUtility {

	/**
	 * Constructs the command line utility for generating reference sets for a
	 * given problem.
	 */
	public SetGenerator() {
		super();
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("problem")
				.hasArg()
				.withArgName("name")
				.isRequired()
				.create('b'));
		options.addOption(OptionBuilder
				.withLongOpt("numberOfPoints")
				.hasArg()
				.withArgName("value")
				.isRequired()
				.create('n'));
		options.addOption(OptionBuilder
				.withLongOpt("seed")
				.hasArg()
				.withArgName("value")
				.create('s'));
		options.addOption(OptionBuilder
				.withLongOpt("output")
				.hasArg()
				.withArgName("file")
				.isRequired()
				.create('o'));
		options.addOption(OptionBuilder
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.create('e'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		NondominatedPopulation set = null;
		Problem problem = null;
		
		int numberOfPoints = Integer.parseInt(commandLine.getOptionValue(
				"numberOfPoints"));

		// setup the merged non-dominated population
		if (commandLine.hasOption("epsilon")) {
			double[] epsilon = TypedProperties.withProperty("epsilon",
					commandLine.getOptionValue("epsilon")).getDoubleArray(
					"epsilon", null);
			set = new EpsilonBoxDominanceArchive(epsilon);
		} else {
			set = new NondominatedPopulation();
		}
		
		// seed the pseudo-random number generator
		if (commandLine.hasOption("seed")) {
			PRNG.setSeed(Long.parseLong(commandLine
					.getOptionValue("seed")));
		}
		
		//generate the points
		try {
			problem = ProblemFactory.getInstance().getProblem(commandLine
					.getOptionValue("problem"));
			
			if (problem instanceof AnalyticalProblem) {
				AnalyticalProblem generator = (AnalyticalProblem)problem;
				
				for (int i=0; i<numberOfPoints; i++) {
					set.add(generator.generate());
				}
			} else {
				throw new FrameworkException(
						"problem does not have an analytical solution");
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
		
		//output set
		PopulationIO.writeObjectives(new File(commandLine.getOptionValue(
				"output")), set);
	}
	
	/**
	 * Starts the command line utility for generating reference sets for a
	 * given problem.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SetGenerator().start(args);
	}
	
}
