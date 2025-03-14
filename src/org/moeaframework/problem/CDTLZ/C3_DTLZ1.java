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
package org.moeaframework.problem.CDTLZ;

import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.GreaterThanOrEqual;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.DTLZ.DTLZ1;

/**
 * The C3-DTLZ1 test problem.
 */
public class C3_DTLZ1 extends DTLZ1 {

	/**
	 * Constructs a C3-DTLZ1 test problem with the specified number of variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public C3_DTLZ1(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}

	/**
	 * Constructs a C3-DTLZ1 test problem with the specified number of objectives.  This is equivalent to calling
	 * {@code new DTLZ1(numberOfObjectives+4, numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public C3_DTLZ1(int numberOfObjectives) {
		super(numberOfObjectives);
	}
	
	@Override
	public int getNumberOfConstraints() {
		return numberOfObjectives;
	}

	@Override
	public void evaluate(Solution solution) {
		super.evaluate(solution);
		
		for (int j = 0; j < numberOfObjectives; j++) {
			double c = solution.getObjectiveValue(j) - 1;
			
			for (int i = 0; i < numberOfObjectives; i++) {
				if (i != j) {
					c += solution.getObjectiveValue(i)/0.5;
				}
			}
			
			solution.setConstraintValue(j, c);
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(0.0, 1.0));
		}
		
		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setConstraint(i, GreaterThanOrEqual.to(0.0));
		}

		return solution;
	}

	@Override
	public Solution generate() {
		throw new UnsupportedOperationException();
	}

}
