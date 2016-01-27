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
 * Performs an affine transformation of the form f(x) = Mx + b.
 */
public class TransformVariablesAffine extends BBOBTransformation {

	/**
	 * The rotation component of the affine transform.
	 */
	private final double[][] M;
	
	/**
	 * The translation component of the affine transform.
	 */
	private final double[] b;
	
	/**
	 * Constructs a new instance of the affine transformation.
	 * 
	 * @param function the inner function
	 * @param M the rotation component of the affine transform
	 * @param b the translation component of the affine transform
	 */
	public TransformVariablesAffine(BBOBFunction function, double[][] M, double[] b) {
		super(function);
		this.M = M;
		this.b = b;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] tx = x.clone();
		
		for (int i = 0; i < x.length; i++) {
			tx[i] = b[i];
			
			for (int j = 0; j < x.length; j++) {
				tx[i] += x[j] * M[i][j];
			}
		}
		
		EncodingUtils.setReal(solution, tx);
		function.evaluate(solution);
		EncodingUtils.setReal(solution, x);
	}

}
