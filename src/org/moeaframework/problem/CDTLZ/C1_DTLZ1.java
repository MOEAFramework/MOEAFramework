/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.problem.CDTLZ;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.DTLZ.DTLZ1;

/**
 * The C1-DTLZ1 test problem.
 */
public class C1_DTLZ1 extends DTLZ1 {

	/**
	 * Constructs a C1-DTLZ1 test problem with the specified number of variables
	 * and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public C1_DTLZ1(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}

	/**
	 * Constructs a C1-DTLZ1 test problem with the specified number of
	 * objectives.  This is equivalent to calling
	 * {@code new DTLZ1(numberOfObjectives+4, numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public C1_DTLZ1(int numberOfObjectives) {
		super(numberOfObjectives);
	}

	@Override
	public int getNumberOfConstraints() {
		return 1;
	}

	@Override
	public void evaluate(Solution solution) {
		super.evaluate(solution);
		
		double c = 1.0 - solution.getObjective(numberOfObjectives-1) / 0.6;
		
		for (int i = 0; i < numberOfObjectives-2; i++) {
			c -= solution.getObjective(i) / 0.5;
		}
		
		solution.setConstraint(0, c >= 0.0 ? 0.0 : c);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives,
				1);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(0.0, 1.0));
		}

		return solution;
	}

}
