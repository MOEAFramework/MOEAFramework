/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.examples;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.binary.HUX;
import org.moeaframework.problem.OneMax;
import org.moeaframework.util.CommandLineUtility;

/**
 * Example of binary optimization on the {@link OneMax} problem.
 */
public class OneMaxExample extends CommandLineUtility {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private OneMaxExample() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws IOException, ClassNotFoundException {
		Problem problem = new OneMax(100);

		Variation mutation = new BitFlip(1.0 / 100.0);
		Variation crossover = new HUX(1.0);
		Selection selection = new TournamentSelection();
		Initialization initialization = new RandomInitialization(problem, 10);

		Algorithm algorithm = new NSGAII(problem,
				new NondominatedSortingPopulation(), null, selection,
				new CompoundVariation(crossover, mutation), initialization);

		while (!algorithm.isTerminated()) {
			algorithm.step();
			
			NondominatedPopulation population = algorithm.getResult();

			if (population.get(0).getObjective(0) == 0) {
				// if all bits are 1
				System.out.println("Found optimal solution after "
						+ algorithm.getNumberOfEvaluations() + " evaluations!");
				break;
			}
		}
	}

	/**
	 * Command line utility for running the binary optimization example using
	 * the {@link OneMax} problem.
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new OneMaxExample().start(args);
	}

}
