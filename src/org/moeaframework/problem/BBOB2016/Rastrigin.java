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
import org.moeaframework.core.variable.EncodingUtils;

/* 
 * The following source code is derived from the Coco Framework available at
 * <https://github.com/numbbo/coco> under the 3-clause BSD license. The
 * original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS
 * file located in the Coco Framework repository for more details.
 */

/**
 * The Rastrigin function.  For a problem with D variables, this highly
 * multimodal function has approximately 10^D local optima.  It is not intended
 * for this function to be used directly since the BBOB test suite applies
 * additional transformations to the test functions.
 * <p>
 * Properties:
 * <ul>
 *   <li>Separable
 *   <li>Multimodal
 * </ul>
 */
public class Rastrigin extends BBOBFunction {
	
	/**
	 * Constructs a new instance of the Rastrigin function.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Rastrigin(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double tmp1 = 0.0;
		double tmp2 = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			tmp1 += Math.cos(2.0 * Math.PI * x[i]);
			tmp2 += x[i] * x[i];
		}
		
		solution.setObjective(0, 10.0 * (x.length - tmp1) + tmp2);
	}

}
