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
package org.moeaframework.examples.ga.onemax;

import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.termination.TargetObjectiveTermination;

/**
 * Example of binary optimization on the {@link OneMax} problem.  The goal of
 * the one-max problem is to maximize the number of {@code 1} bits in the
 * binary variable.  This example runs until all bits are {@code 1} to
 * determine the required number of function evaluations (NFE).
 */
public class OneMaxExample {

	public static void main(String[] args) {
		Problem problem = new OneMax(100);
		GeneticAlgorithm algorithm = new GeneticAlgorithm(problem);
		
		// run until we find a solution with all bits set to 1 (i.e., an objective value of 0)
		algorithm.run(new TargetObjectiveTermination(new double[] { 0 }));
		
		System.out.println("Found optimal solution after " + algorithm.getNumberOfEvaluations() + " evaluations!");
		System.out.println();
		algorithm.getResult().display();
	}

}
