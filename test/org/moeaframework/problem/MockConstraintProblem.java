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
package org.moeaframework.problem;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

public class MockConstraintProblem extends MockProblem {

	public MockConstraintProblem() {
		this(1);
	}
	
	public MockConstraintProblem(int numberOfObjectives) {
		super(1, numberOfObjectives, 3);
	}

	@Override
	public void evaluate(Solution solution) {
		super.evaluate(solution);
		
		solution.setConstraint(0, -15.0);
		solution.setConstraint(1, 0.0);
		solution.setConstraint(2, 20.0);
	}

	@Override
	public Solution newSolution() {
		Solution solution = super.newSolution();
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		return solution;
	}
	
}
