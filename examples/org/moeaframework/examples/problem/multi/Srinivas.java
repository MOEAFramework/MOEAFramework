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
package org.moeaframework.examples.problem.multi;

import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.LessThanOrEqual;
import org.moeaframework.core.objective.Minimize;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * Implements the Srinivas multi-objective problem.
 */
public class Srinivas extends AbstractProblem {

	/**
	 * Creates the problem with two decision variables, two objectives, and two constraints.
	 */
	public Srinivas() {
		super(2, 2, 2);
	}

	/**
	 * Function to evaluate each solution.
	 */
	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		
		double f1 = Math.pow(x - 2.0, 2.0) + Math.pow(y - 1.0, 2.0) + 2.0;
		double f2 = 9.0*x - Math.pow(y - 1.0, 2.0);
		double c1 = Math.pow(x, 2.0) + Math.pow(y, 2.0);
		double c2 = x - 3.0*y;
		
		solution.setObjectiveValue(0, f1);
		solution.setObjectiveValue(1, f2);
		
		solution.setConstraintValue(0, c1);
		solution.setConstraintValue(1, c2);
	}

	/**
	 * Here we define a solution, which contains two real-valued decision variables, two minimized objectives,
	 * and two constraints.
	 */
	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, new RealVariable(-20.0, 20.0));
		solution.setVariable(1, new RealVariable(-20.0, 20.0));
		
		solution.setObjective(0, new Minimize());
		solution.setObjective(1, new Minimize());
		
		solution.setConstraint(0, LessThanOrEqual.to(225.0));
		solution.setConstraint(1, LessThanOrEqual.to(-10.0));
		
		return solution;
	}
	
}