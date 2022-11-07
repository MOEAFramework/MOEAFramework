/* Copyright 2009-2022 David Hadka
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
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * A mock problem with different types of variables.
 */
public class MockMultiTypeProblem extends AbstractProblem {
	
	public MockMultiTypeProblem() {
		super(3, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		solution.setObjective(0, 5.0);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(3, 1);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new BinaryVariable(10));
		solution.setVariable(2, new Permutation(5));
		return solution;
	}

}
