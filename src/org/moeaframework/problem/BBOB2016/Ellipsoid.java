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
 * The Ellipsoid function.  It is not intended for this function to be
 * used directly since the BBOB test suite applies additional transformations
 * to the test functions.
 * <p>
 * Properties:
 * <ul>
 *   <li>Non-separable
 *   <li>Unimodal
 *   <li>Ill-conditioned
 *   <li>Smooth local irregularities
 * </ul>
 */
public class Ellipsoid extends BBOBFunction {
	
	/**
	 * The conditioning factor.
	 */
	public static final double CONDITION = 1.0e6;
	
	/**
	 * Constructs a new instance of the Ellipsoid function.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Ellipsoid(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double result = x[0] * x[0];
		
		for (int i = 1; i < x.length; i++) {
			double exponent = 1.0 * i / (x.length - 1.0);
			result += Math.pow(CONDITION, exponent) * x[i] * x[i];
		}
		
		solution.setObjective(0, result);
	}

}
