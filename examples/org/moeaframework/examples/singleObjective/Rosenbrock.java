/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.examples.singleObjective;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The single-objective Rosenbrock problem with an optimum at {@code x = (1, 1)} with {@code f(x) = 0}.
 */
public class Rosenbrock extends AbstractProblem {
	
	public Rosenbrock() {
		super(2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = RealVariable.getReal(solution.getVariable(0));
		double y = RealVariable.getReal(solution.getVariable(1));
		
		solution.setObjectiveValue(0, 100*(y - x*x)*(y - x*x) + (1 - x)*(1 - x));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1);
		solution.setVariable(0, new RealVariable(-10, 10));
		solution.setVariable(1, new RealVariable(-10, 10));
		return solution;
	}

}