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

/* 
 * The following source code is derived from the Coco Framework available at
 * <https://github.com/numbbo/coco> under the 3-clause BSD license. The
 * original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS
 * file located in the Coco Framework repository for more details.
 */

/**
 * Transformation that raises the objective value to the power of a given
 * exponent.
 */
public class TransformObjectivePower extends BBOBTransformation {

	/**
	 * The exponent.
	 */
	private final double exponent;
	
	/**
	 * Constructs a new instance of the power transformation.
	 * 
	 * @param function the inner function
	 * @param exponent the exponent
	 */
	public TransformObjectivePower(BBOBFunction function, double exponent) {
		super(function);
		this.exponent = exponent;
	}

	@Override
	public void evaluate(Solution solution) {
		function.evaluate(solution);
		
		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setObjective(i, Math.pow(solution.getObjective(i), exponent));
		}
	}

}
