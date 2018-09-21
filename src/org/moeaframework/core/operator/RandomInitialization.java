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
package org.moeaframework.core.operator;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;

/**
 * Initializes all built-in decision variables randomly. The
 * {@link #initialize(Variable)} method can be extended to provide support for
 * other types.
 */
public class RandomInitialization implements Initialization {

	/**
	 * The problem.
	 */
	protected final Problem problem;

	/**
	 * The initial population size.
	 */
	protected final int populationSize;

	/**
	 * Constructs a random initialization operator.
	 * 
	 * @param problem the problem
	 * @param populationSize the initial population size
	 */
	public RandomInitialization(Problem problem, int populationSize) {
		super();
		this.problem = problem;
		this.populationSize = populationSize;
	}

	@Override
	public Solution[] initialize() {
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

	/**
	 * Initializes the specified decision variable randomly. This method
	 * supports all built-in types, and can be extended to support custom types.
	 * 
	 * @param variable the variable to be initialized
	 * @deprecated Call variable.randomize() instead
	 */
	@Deprecated
	protected void initialize(Variable variable) {
		variable.randomize();
	}

}
