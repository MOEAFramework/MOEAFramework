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
package org.moeaframework.examples.misc;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.InjectedInitialization;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates how to inject known good solutions into the initial population of an algorithm.  Here, we inject
 * two solutions corresponding to the Pareto optimal points with objectives (1, 0) and (0, 1), respectively.
 */
public class InjectSolutionsExample {

	public static void main(String[] args) {
		Problem problem = new DTLZ2(2);
		
		Solution solutionA = problem.newSolution();
		RealVariable.setReal(solutionA, new double[] { 0.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 });
		
		Solution solutionB = problem.newSolution();
		RealVariable.setReal(solutionB, new double[] { 1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 });
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.setInitialization(new InjectedInitialization(problem, solutionA, solutionB));
		algorithm.run(10000);
		
		algorithm.getResult().display();
	}

}
