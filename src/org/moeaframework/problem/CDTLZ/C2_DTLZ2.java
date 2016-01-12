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
package org.moeaframework.problem.CDTLZ;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * The C2-DTLZ2 test problem.
 */
public class C2_DTLZ2 extends DTLZ2 {

	/**
	 * Constructs a C2-DTLZ2 test problem with the specified number of variables
	 * and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public C2_DTLZ2(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}

	/**
	 * Constructs a X2-DTLZ2 test problem with the specified number of
	 * objectives.  This is equivalent to calling
	 * {@code new DTLZ2(numberOfObjectives+9, numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public C2_DTLZ2(int numberOfObjectives) {
		super(numberOfObjectives);
	}
	
	@Override
	public int getNumberOfConstraints() {
		return 1;
	}

	@Override
	public void evaluate(Solution solution) {
		super.evaluate(solution);
		
		double r = numberOfObjectives > 3 ? 0.5 : 0.4;
		double v1 = Double.POSITIVE_INFINITY;
		double v2 = 0.0;
		
		for (int i = 0; i < numberOfObjectives; i++) {
			double sum = Math.pow(solution.getObjective(i)-1.0, 2.0);
			
			for (int j = 0; j < numberOfObjectives; j++) {
				if (i != j) {
					sum += Math.pow(solution.getObjective(j), 2.0);
				}
			}
			
			v1 = Math.min(v1, sum - Math.pow(r, 2.0));
			v2 += Math.pow(solution.getObjective(i) -
					1.0 / Math.sqrt(numberOfObjectives), 2.0);
		}
		
		double c = Math.min(v1, v2 - Math.pow(r, 2.0));
		solution.setConstraint(0, c <= 0.0 ? 0.0 : c);
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

	@Override
	public Solution generate() {
		while (true) {
			Solution solution = super.generate();
			
			evaluate(solution);
			
			if (!solution.violatesConstraints()) {
				return solution;
			}
		}
	}
	
}
