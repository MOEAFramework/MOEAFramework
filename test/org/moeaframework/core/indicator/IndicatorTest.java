/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.core.indicator;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Abstract class for testing indicators.
 */
public abstract class IndicatorTest {

	/**
	 * Converts a population into a 2D array that can be used by the JMetal
	 * indicators.
	 * 
	 * @param population the population
	 * @return the 2D array representation of the problem
	 */
	protected double[][] toArray(Population population) {
		double[][] array = new double[population.size()][];

		for (int i = 0; i < population.size(); i++) {
			Solution solution = population.get(i);
			array[i] = new double[solution.getNumberOfObjectives()];

			for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
				array[i][j] = solution.getObjective(j);
			}
		}

		return array;
	}

	/**
	 * Returns a new, randomly-generated approximation set for the specified
	 * problem. The resulting approximation set will contain at least {@code N}
	 * solutions, but depending on the degree of dominance the actual size may
	 * be significantly less than {@code N}.
	 * 
	 * @param problemName the problem
	 * @param N the number of randomly-generated solutions
	 * @return a new, randomly-generated approximation set for the specified
	 *         problem
	 */
	protected NondominatedPopulation generateApproximationSet(
			String problemName, int N) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		Initialization initialization = new RandomInitialization(problem, N);
		Solution[] solutions = initialization.initialize();

		for (Solution solution : solutions) {
			problem.evaluate(solution);
		}

		NondominatedPopulation result = new NondominatedPopulation();
		result.addAll(solutions);
		return result;
	}

}
