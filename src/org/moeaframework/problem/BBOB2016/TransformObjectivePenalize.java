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
import org.moeaframework.core.variable.RealVariable;

/* 
 * The following source code is derived from the Coco Framework available at
 * <https://github.com/numbbo/coco> under the 3-clause BSD license. The
 * original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS
 * file located in the Coco Framework repository for more details.
 */

/**
 * Transformation that penalizes the objectives if the decision variables
 * fall outside the region of interest.  This transformation currently has no
 * impact on the MOEA Framework since all real-valued decision variables
 * enforce the lower and upper bounds.
 */
public class TransformObjectivePenalize extends BBOBTransformation {
	
	/**
	 * The penalty factor.
	 */
	private final double factor;
	
	/**
	 * Constructs a new instance of the penalty transformation.
	 * 
	 * @param function the inner function
	 * @param factor the penalty factor
	 */
	public TransformObjectivePenalize(BBOBFunction function, double factor) {
		super(function);
		this.factor = factor;
	}

	@Override
	public void evaluate(Solution solution) {
		double penalty = 0.0;
		double lowerBound = -5.0;
		double upperBound = 5.0;
		
		for (int i = 0; i < numberOfVariables; i++) {
			RealVariable v = (RealVariable)solution.getVariable(i);
			double c1 = v.getValue() - upperBound;
			double c2 = lowerBound - v.getValue();
			
			if (c1 > 0.0) {
				penalty += c1*c1;
			} else if (c2 > 0.0) {
				penalty += c2*c2;
			}
		}
		
		function.evaluate(solution);
		
		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setObjective(i, solution.getObjective(i) + factor*penalty);
		}
	}

}
