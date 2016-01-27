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
 * The x-hat transformation, which negates some of the decision variables.
 */
public class TransformVariablesXHat extends BBOBTransformation {

	/**
	 * The random number seed controlling which decision variables are negated.
	 */
	private final long seed;
	
	/**
	 * Constructs a new instance of the x-hat transformation.
	 * 
	 * @param function the inner function
	 * @param seed the random number seed controlling which decision variables
	 *        are negated
	 */
	public TransformVariablesXHat(BBOBFunction function, long seed) {
		super(function);
		this.seed = seed;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] tx = BBOBUtils.uniform(x.length, seed);
		
		for (int i = 0; i < x.length; i++) {
			if (tx[i] - 0.5 < 0.0) {
				tx[i] = -x[i];
			} else {
				tx[i] = x[i];
			}
		}
		
		EncodingUtils.setReal(solution, tx);
		function.evaluate(solution);
		EncodingUtils.setReal(solution, x);
	}

}
