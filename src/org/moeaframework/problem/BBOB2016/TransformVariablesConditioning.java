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
 * Transformation that alters the condition number of the function.  Increasing
 * the condition number increases the magnitude of changes observed in the
 * function outputs caused by small changes to the inputs.
 */
public class TransformVariablesConditioning extends BBOBTransformation {

	/**
	 * Factor controlling the increase in conditioning.
	 */
	private final double alpha;
	
	/**
	 * Constructs a new instance of the conditioning transformation.
	 * 
	 * @param function the inner function
	 * @param alpha factor controlling the increase in conditioning
	 */
	public TransformVariablesConditioning(BBOBFunction function, double alpha) {
		super(function);
		this.alpha = alpha;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] tx = x.clone();
		
		for (int i = 0; i < x.length; i++) {
			tx[i] = Math.pow(alpha,  0.5*i / (x.length - 1.0)) * x[i];
		}
		
		EncodingUtils.setReal(solution, tx);
		function.evaluate(solution);
		EncodingUtils.setReal(solution, x);
	}

}
