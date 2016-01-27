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
 * Transformation that applies a monotone oscillation to the decision variables
 * of the inner function.
 */
public class TransformVariablesOscillate extends BBOBTransformation {
	
	/**
	 * Factor controlling the periodicity of the oscillation.
	 */
	public static final double ALPHA = 0.1;

	/**
	 * Constructs a new transformation that applies an oscillation to the
	 * decision variables.
	 * 
	 * @param function the inner function
	 */
	public TransformVariablesOscillate(BBOBFunction function) {
		super(function);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] tx = x.clone();
		
		for (int i = 0; i < x.length; i++) {
			if (x[i] > 0.0) {
				double tmp = Math.log(x[i]) / ALPHA;
				double base = Math.exp(tmp + 0.49*(Math.sin(tmp) + Math.sin(0.79*tmp)));
				tx[i] = Math.pow(base, ALPHA);
			} else if (x[i] < 0.0) {
				double tmp = Math.log(-x[i]) / ALPHA;
				double base = Math.exp(tmp + 0.49*(Math.sin(0.55*tmp) + Math.sin(0.31*tmp)));
				tx[i] = -Math.pow(base, ALPHA);
			} else {
				tx[i] = 0.0;
			}
		}
		
		EncodingUtils.setReal(solution, tx);
		function.evaluate(solution);
		EncodingUtils.setReal(solution, x);
	}

}
