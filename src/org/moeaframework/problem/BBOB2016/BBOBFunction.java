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
package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

/**
 * Abstract class for the functions provided by the BBOB test suite.  These
 * functions are exclusively single-objective.
 */
public abstract class BBOBFunction extends AbstractProblem {

	/**
	 * Constructs a new function for the BBOB test suite.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public BBOBFunction(int numberOfVariables) {
		super(numberOfVariables, 1);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 1);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, BBOBUtils.createTransformedVariable());
		}
		
		return solution;
	}

}
