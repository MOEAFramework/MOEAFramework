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
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.moeaframework.algorithm.GDE3;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.real.DifferentialEvolution;
import org.moeaframework.core.operator.real.DifferentialEvolutionSelection;
import org.moeaframework.problem.Rosenbrock;
import org.moeaframework.util.CommandLineUtility;

/**
 * Example of real optimization on the {@link Rosenbrock} problem.
 */
public class RosenbrockExample extends CommandLineUtility {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private RosenbrockExample() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws IOException, ClassNotFoundException {
		Problem problem = new Rosenbrock(10);

		NondominatedSortingPopulation population = new NondominatedSortingPopulation();

		Initialization initialization = new RandomInitialization(problem, 40);

		DominanceComparator comparator = new ChainedComparator(
				new AggregateConstraintComparator(),
				new ParetoDominanceComparator());

		DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection();

		DifferentialEvolution variation = new DifferentialEvolution(1.0, 0.9);

		Algorithm algorithm = new GDE3(problem, population, comparator, 
				selection, variation, initialization);

		while (!algorithm.isTerminated()) {
			algorithm.step();
			
			NondominatedPopulation result = algorithm.getResult();
			
			System.out.println(Arrays.toString(
					CoreUtils.castVariablesToDoubleArray(result.get(0))) + " " +
					result.get(0).getObjective(0));

			if (Math.abs(result.get(0).getObjective(0)) < 0.0000000000000001) {
				System.out.println("Found optimal solution after "
						+ algorithm.getNumberOfEvaluations() + " evaluations!");
				break;
			}
		}
	}

	/**
	 * Command line utility for running the real optimization example using
	 * the {@link Rosenbrock} problem.
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new RosenbrockExample().start(args);
	}

}
