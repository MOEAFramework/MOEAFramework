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
package org.moeaframework.core.initialization;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

/**
 * Initializes all built-in decision variables randomly. This invokes {@link Variable#randomize()} on each decision
 * variable.
 */
public class RandomInitialization implements Initialization {

	/**
	 * The problem.
	 */
	protected final Problem problem;

	/**
	 * Constructs a random initialization operator.
	 * 
	 * @param problem the problem
	 */
	public RandomInitialization(Problem problem) {
		super();
		this.problem = problem;
	}

	@Override
	public Solution[] initialize(int populationSize) {
		Solution[] initialPopulation = new Solution[populationSize];

		for (int i = 0; i < populationSize; i++) {
			Solution solution = problem.newSolution();

			for (int j = 0; j < solution.getNumberOfVariables(); j++) {
				solution.getVariable(j).randomize();
			}

			initialPopulation[i] = solution;
		}

		return initialPopulation;
	}

}
