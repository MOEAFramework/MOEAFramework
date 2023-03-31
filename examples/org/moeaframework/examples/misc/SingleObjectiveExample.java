/* Copyright 2009-2023 David Hadka
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
package org.moeaframework.examples.misc;

import java.io.IOException;

import org.moeaframework.algorithm.single.DifferentialEvolution;
import org.moeaframework.algorithm.single.EvolutionStrategy;
import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.algorithm.single.SimulatedAnnealing;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Demonstrates single-objective optimization using the Rosenbrock function.  The optimum is located at
 * x = (1, 1) with an objective value of f(x) = 0.0.
 */
public class SingleObjectiveExample {
	
	public static class RosenbrockProblem extends AbstractProblem {
		
		public RosenbrockProblem() {
			super(2, 1, 0);
		}

		@Override
		public void evaluate(Solution solution) {
			double result = 0.0;
			double[] x = EncodingUtils.getReal(solution);

			for (int i = 0; i < x.length-1; i++) {
				result += 100 * (x[i]*x[i] - x[i+1])*(x[i]*x[i] - x[i+1]) + (x[i] - 1)*(x[i] - 1);
			}

			solution.setObjective(0, result);
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(2, 1, 0);
			solution.setVariable(0, EncodingUtils.newReal(-10, 10));
			solution.setVariable(1, EncodingUtils.newReal(-10, 10));
			return solution;
		}

	};
	
	public static void main(String[] args) throws IOException {
		Problem problem = new RosenbrockProblem();
		
		System.out.println("Genetic Algorithm:");
		GeneticAlgorithm ga = new GeneticAlgorithm(problem);
		ga.run(100000);
		ga.getResult().display();
		
		System.out.println();
		System.out.println("Differential Evolution:");
		DifferentialEvolution de = new DifferentialEvolution(problem);
		de.run(100000);
		de.getResult().display();
		
		System.out.println();
		System.out.println("Evolution Strategy:");
		EvolutionStrategy es = new EvolutionStrategy(problem);
		es.run(100000);
		es.getResult().display();
		
		System.out.println();
		System.out.println("Simulated Annealing:");
		SimulatedAnnealing sa = new SimulatedAnnealing(problem);
		sa.run(100000);
		sa.getResult().display();
	}

}
