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
 * The Schaffers F7 function.  It is not intended for this function to be
 * used directly since the BBOB test suite applies additional transformations
 * to the test functions.
 * <p>
 * Properties:
 * <ul>
 *   <li>Highly multimodal (frequency and amplitude of modulation vary)
 *   <li>Asymmetric
 *   <li>Rotated
 *   <li>Low conditioning
 * </ul>
 */
public class Schaffers extends BBOBFunction {
	
	/**
	 * Constructs a new instance of the Schaffers F7 problem.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Schaffers(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double result = 0.0;
		
		for (int i = 0; i < x.length - 1; i++) {
			double tmp = x[i] * x[i] + x[i+1] * x[i+1];
			result += Math.pow(tmp, 0.25) * (1.0 + Math.pow(Math.sin(50.0 * Math.pow(tmp, 0.1)), 2.0));
		}
		
		solution.setObjective(0, Math.pow(result / (x.length - 1.0), 2.0));
	}

}
