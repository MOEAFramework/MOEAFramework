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
package org.moeaframework.problem.DTLZ;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * The Inverted DTLZ1 test problem.
 */
public class InvertedDTLZ1 extends DTLZ1 {

	/**
	 * Constructs an Inverted DTLZ1 test problem with the specified number of objectives.  This is equivalent to
	 * calling {@code new InvertedDTLZ1(numberOfObjectives+4, numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public InvertedDTLZ1(int numberOfObjectives) {
		this(numberOfObjectives + 4, numberOfObjectives);
	}

	/**
	 * Constructs an Inverted DTLZ1 test problem with the specified number of variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public InvertedDTLZ1(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}
	
	@Override
	public void evaluate(Solution solution) {		
		super.evaluate(solution);
		
		// apply the transformation to create the inverted version
		double[] x = EncodingUtils.getReal(solution);
		double[] f = solution.getObjectives();
		double g = g(x);
		
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 0.5 * (1.0 + g) - f[i];
		}
		
		solution.setObjectives(f);
	}

}
