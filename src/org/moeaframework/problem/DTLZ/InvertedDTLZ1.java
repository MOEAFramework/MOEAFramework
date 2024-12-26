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
package org.moeaframework.problem.DTLZ;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

/**
 * The Inverted DTLZ1 test problem.  This problem was introduced to test reference-point based algorithms since not all
 * reference points lie on or intersect with the inverted Pareto front.
 * <p>
 * References:
 * <ol>
 *   <li>Jain, H. and K. Deb. "An Evolutionary Many-Objective Optimization Algorithm Using Reference-Point-Based
 *       Nondominated Sorting Approach, Part II: Handling Constraints and Extending to an Adaptive Approach."
 *       IEEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 * </ol>
 */
public class InvertedDTLZ1 extends DTLZ1 {

	/**
	 * Constructs an Inverted DTLZ1 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public InvertedDTLZ1(int numberOfObjectives) {
		super(numberOfObjectives + 4, numberOfObjectives);
	}
	
	@Override
	public void evaluate(Solution solution) {
		super.evaluate(solution);
		
		// apply the transformation to create the inverted version
		double[] x = RealVariable.getReal(solution);
		double[] f = solution.getObjectiveValues();
		double g = g1(x);
		
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 0.5 * (1.0 + g) - f[i];
		}
		
		solution.setObjectiveValues(f);
	}

}
