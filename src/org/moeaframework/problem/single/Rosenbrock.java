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
package org.moeaframework.problem.single;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * The single-objective Rosenbrock problem with an optimum at {@code x = (1, 1)} with {@code f(x) = 1}.
 */
public class Rosenbrock extends AbstractProblem implements AnalyticalProblem {
	
	/**
	 * Constructs a new instance of the Rosenbrock problem.
	 */
	public Rosenbrock() {
		super(2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		
		solution.setObjective(0, 100*(y - x*x)*(y - x*x) + (1 - x)*(1 - x));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1);
		solution.setVariable(0, EncodingUtils.newReal(-10, 10));
		solution.setVariable(1, EncodingUtils.newReal(-10, 10));
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		EncodingUtils.setReal(solution, new double[] { 1.0, 1.0 });
		evaluate(solution);
		return solution;
	}

}