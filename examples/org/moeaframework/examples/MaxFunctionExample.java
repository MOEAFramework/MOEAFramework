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
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.grammar.GrammarCrossover;
import org.moeaframework.core.operator.grammar.GrammarMutation;
import org.moeaframework.problem.MaxFunction;
import org.moeaframework.util.CommandLineUtility;

/**
 * Example of grammatical evolution using the {@link MaxFunction} problem.
 */
public class MaxFunctionExample extends CommandLineUtility {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private MaxFunctionExample() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		Problem problem = new MaxFunction();

		Variation crossover = new GrammarCrossover(1.0);
		Variation mutation = new GrammarMutation(0.1);
		Selection selection = new TournamentSelection();
		Initialization initialization = new RandomInitialization(problem, 100);

		Algorithm algorithm = new NSGAII(problem,
				new NondominatedSortingPopulation(), null, selection,
				new CompoundVariation(crossover, mutation), initialization);

		while (!algorithm.isTerminated() && (algorithm.getNumberOfEvaluations() < 10000)) {
			algorithm.step();
			System.out.println(-algorithm.getResult().get(0).getObjective(0));
		}
	}

	/**
	 * Command line utility for the example of grammatical evolution using the
	 * {@link MaxFunction} problem.
	 * 
	 * @param args the command line arguments
	 * @throws IOException if an I/O error occurred
	 */
	public static void main(String[] args) throws IOException {
		new MaxFunctionExample().start(args);
	}

}
