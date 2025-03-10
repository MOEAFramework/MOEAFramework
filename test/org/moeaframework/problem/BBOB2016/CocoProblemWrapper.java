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
package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class CocoProblemWrapper extends AbstractProblem {
	
	private final Problem problem;
	
	public CocoProblemWrapper(Problem problem) {
		super(problem.getDimension(), problem.getNumberOfObjectives(), problem.getNumberOfConstraints());
		this.problem = problem;
	}

	@Override
	public String getName() {
		return problem.getName();
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		solution.setObjectiveValues(problem.evaluateFunction(x));
		
		if (numberOfConstraints > 0) {
			solution.setConstraintValues(problem.evaluateConstraint(x));
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(
					problem.getSmallestValueOfInterest(i), problem.getLargestValueOfInterest(i)));
		}
		
		return solution;
	}
	
}
