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
 * The Rosenbrock function.  It is not intended for this function to be used
 * directly since the BBOB test suite applies additional transformations to the
 * test functions.
 * <p>
 * Properties:
 * <ul>
 *   <li>Partially separable
 *   <li>Low or moderate conditioning
 * </ul>
 */
public class Rosenbrock extends BBOBFunction {
	
	/**
	 * Constructs a new instance of the Rosenbrock function.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Rosenbrock(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double s1 = 0.0;
		double s2 = 0.0;
		double tmp;
		
		for (int i = 0; i < x.length-1; i++) {
			tmp = (x[i]*x[i] - x[i+1]);
			s1 += tmp*tmp;
			tmp = (x[i] - 1.0);
			s2 += tmp*tmp;
		}
		
		solution.setObjective(0, 100.0*s1 + s2);
	}

}
