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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.NondominatedSortingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.binary.BitFlip;
import org.moeaframework.core.operator.binary.HUX;
import org.moeaframework.problem.Knapsack;
import org.moeaframework.util.CommandLineUtility;

/**
 * Example of binary optimization using the {@link Knapsack} problem on the
 * {@code knapsack.100.2} instance.
 */
public class KnapsackExample extends CommandLineUtility {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private KnapsackExample() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		Problem problem = new Knapsack(new File("knapsack.100.2"));

		Variation mutation = new BitFlip(1.0 / 100.0);
		Variation crossover = new HUX(1.0);
		Selection selection = new TournamentSelection(
				new NondominatedSortingComparator());
		Initialization initialization = new RandomInitialization(problem, 100);

		Algorithm algorithm = new NSGAII(problem,
				new NondominatedSortingPopulation(new ChainedComparator(
						new AggregateConstraintComparator(),
						new ParetoDominanceComparator())), null,
				selection, new CompoundVariation(crossover, mutation),
				initialization);

		while (!algorithm.isTerminated() && (algorithm.getNumberOfEvaluations() < 100000)) {
			algorithm.step();
		}

		for (Solution solution : algorithm.getResult()) {
			if (!solution.violatesConstraints()) {
				System.out.println(Arrays.toString(solution.getObjectives()));
			}
		}
	}

	/**
	 * Command line utility for the example of binary optimization using the
	 * {@link Knapsack} problem.
	 * 
	 * @param args the command line arguments
	 * @throws IOException if an I/O error occurred
	 */
	public static void main(String[] args) throws IOException {
		new KnapsackExample().start(args);
	}

}
