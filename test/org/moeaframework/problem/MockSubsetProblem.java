/* Copyright 2009-2016 David Hadka
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
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Subset;

/**
 * A mock problem with a subset variable.  The objective of this problem
 * is to find a subset that sums to 10.
 */
public class MockSubsetProblem extends AbstractProblem {
	
	public MockSubsetProblem() {
		super(1, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		int sum = 0;
		int[] values = EncodingUtils.getSubset(solution.getVariable(0));

		for (int i=0; i<values.length; i++) {
			sum += values[i];
		}
		
		solution.setObjective(0, Math.abs(10.0 - sum));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Subset(5, 10));
		return solution;
	}

}
