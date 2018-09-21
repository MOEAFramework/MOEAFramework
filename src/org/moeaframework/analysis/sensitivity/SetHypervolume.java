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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.util.CommandLineUtility;
import org.moeaframework.util.TypedProperties;

/**
 * Command line utility for calculating the hypervolume of approximation sets.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.SetHypervolume <options> <files>}
 * <p>
 * Arguments:
 * <table border="0" style="margin-left: 1em">
 *   <tr>
 *     <td>{@code -e, --epsilon}</td>
 *     <td>The epsilon values for limiting the size of the results.  This
 *         epsilon value is also used for any algorithms that include an
 *         epsilon parameter.</td>
 *   </tr>
 * </table>
 */
public class SetHypervolume extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for calculating the hypervolume of 
	 * approximation sets.
	 */
	public SetHypervolume() {
		super();
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("epsilon")
				.hasArg()
				.withArgName("e1,e2,...")
				.create('e'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		for (String filename : commandLine.getArgs()) {
			NondominatedPopulation set = new NondominatedPopulation(
					PopulationIO.readObjectives(new File(filename)));
			
			if (commandLine.hasOption("epsilon")) {
				TypedProperties typedProperties = TypedProperties.withProperty(
						"epsilon", commandLine.getOptionValue("epsilon"));
				
				set = new EpsilonBoxDominanceArchive(
						typedProperties.getDoubleArray("epsilon", null), set);
			}
			
			System.out.print(filename);
			System.out.print(' ');
			System.out.println(new Hypervolume(new ProblemStub(
					set.get(0).getNumberOfObjectives()), set).evaluate(set));
		}
	}
	
	/**
	 * Starts the command line utility for calculating the hypervolume of 
	 * approximation sets.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new SetHypervolume().start(args);
	}

}
