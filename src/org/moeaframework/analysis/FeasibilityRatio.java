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
package org.moeaframework.analysis;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;

/**
 * Computes the feasibility ratio of a given problem, which takes a random sampling of solutions and computes the
 * percentage that are feasible.
 * <p>
 * References:
 * <ol>
 *   <li>R. Tanabe and A. Oyama. "A note on constrained multi-objective optimization benchmark problems."
 *       2017 IEEE Congress on Evolutionary Computation (CEC), Donostia, Spain, 2017, pp. 1127-1134.
 * </ol>
 */
public class FeasibilityRatio {
	
	/**
	 * The number of random samples.
	 */
	private final int samples;
	
	/**
	 * Constructs the class to compute the feasibility ratio.
	 * 
	 * @param samples the number of random samples
	 */
	public FeasibilityRatio(int samples) {
		super();
		this.samples = samples;
	}
	
	/**
	 * Calculates the feasibility ratio of the given problem.
	 * 
	 * @param problem the problem
	 * @return the feasibility ratio
	 */
	public double calculate(Problem problem) {
		if (problem.getNumberOfConstraints() == 0) {
			return 1.0;
		}
		
		RandomInitialization generator = new RandomInitialization(problem);
		Solution[] solutions = generator.initialize(samples);
		
		for (Solution solution : solutions) {
			problem.evaluate(solution);
		}
		
		int feasible = 0;
		
		for (Solution solution : solutions) {
			if (solution.isFeasible()) {
				feasible++;
			}
		}
		
		return feasible / (double)samples;
	}
	
	/**
	 * Calculates the feasibility ratio of the given problem.
	 * 
	 * @param problem the problem
	 * @param samples the number of random samples
	 * @return the feasibility ratio
	 */
	public static double calculate(Problem problem, int samples) {
		return new FeasibilityRatio(samples).calculate(problem);
	}

}
